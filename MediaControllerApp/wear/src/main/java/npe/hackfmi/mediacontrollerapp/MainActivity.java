package npe.hackfmi.mediacontrollerapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.kiwiwearables.kiwilib.DetectionCallback;
import com.kiwiwearables.kiwilib.DetectionInfo;
import com.kiwiwearables.kiwilib.Kiwi;
import com.kiwiwearables.kiwilib.KiwiCallback;
import com.kiwiwearables.kiwilib.SensorUnits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {


    public static final int NONE      = 0;
    public static final int PLAY_PREV = 1;
    public static final int PLAY_NEXT = 2;
    public static final int PAUSE     = 3;

    public static final long CHANGE_GESTURE_AVARAGE = 7000;
    public static final long PAUSE_GESTURE_AVARAGE = 2000;
    public static final long GESTURE_LOCK_TIME = 2000;

    public static final double EPSILON = 1e-6;
    public static final double MAX_GESTURE_ANGLE = Math.cos(Math.PI / 16);

    public static final double SWIPE_LEFT_NORMAL[] = new double[] {0, 1, 0};
    public static final double SWIPE_RIGHT_NORMAL[] = new double[] {0, 1, 0};
    public static final double SWIPE_UP_NORMAL[] = new double[] {0, 1, 0};
    public static final double SWIPE_DOWN_NORMAL[] = new double[] {0, 1, 0};
    public static final double SWIPE_FORWARD_NORMAL[] = new double[] {0, 1, 0};
    public static final double SWIPE_BACKWARD_NORMAL[] = new double[] {0, 1, 0};

    private long lockTimeStart = 0;

    private TextView mTextViewOutput;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;

    private SensorData.Data mAcceleration = new SensorData.Data();
    private SensorData.Data mVelocity = new SensorData.Data();

    public SensorData mAccelerometerData = new SensorData();
    public SensorData mPositionData = new SensorData();

    /*private double[] displacement(SensorData accelerometerData) {


        SensorData velocityData = mAccelerometerData.integrateData().integrateData();

        if (velocityData.mData.isEmpty()) {
            return new double[] {0, 0, 0};
        }

        return velocityData.mData.peekFirst().value;
    }*/

    private double lengthSqr(double[] a) {
        return a[0] * a[0] + a[1] * a[1] + a[2] * a[2];
    }

    private double[] normalize(double[] a) {
        double size = Math.sqrt(lengthSqr(a));
        return new double[] { a[0] / size,  a[1] / size, a[2] / size };
    }

    private double scalar(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];

    }

    private double calculateCos(double[] a, double[] b) {
        double normalizedA[] = normalize(a);
        double normalizedB[] = normalize(b);

        return scalar(normalizedA, normalizedB);
    }

    private double[] plus(double[] a, double[] b) {
        return new double[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
    }

    private double[] minus(double[] a, double[] b) {
        return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }

    private double[] mult(double[] a, double scalar) {
        return new double[] { a[0] * scalar, a[1] * scalar, a[2] * scalar };
    }

    private void update(SensorData.Data acceleration) {
        double delta = (acceleration.time - mAcceleration.time) / 2.0;

        System.out.println("acceleration =  x: " + acceleration.value[0] + ", y: " + acceleration.value[1] + ", z: " + acceleration.value[2]);
        System.out.println("Velocity =  x: " + mVelocity.value[0] + ", y: " + mVelocity.value[1] + ", z: " + mVelocity.value[2]);
        mVelocity.value = plus(mVelocity.value, mult(acceleration.value, delta));
        System.out.println("Velocity =  x: " + mVelocity.value[0] + ", y: " + mVelocity.value[1] + ", z: " + mVelocity.value[2]);

        mVelocity.time = acceleration.time;
        mAcceleration = acceleration;

        double prevLocation[] = new double[] {0, 0, 0};

        if (!mPositionData.mData.isEmpty())
            prevLocation = mPositionData.mData.peekFirst().value;

        SensorData.Data newLocation = new SensorData.Data();
        newLocation.value = plus(prevLocation, mult(mVelocity.value, delta));
        newLocation.time = acceleration.time;

        mPositionData.add(newLocation);
    }

    private int checkForGesture() {
        if (mPositionData.mData.isEmpty()) {
            return NONE;
        }

        //System.out.println("Acceleration = x: " + mAcceleration.value[0] + ", y: " + mAcceleration.value[1] + ", z: " + mAcceleration.value[2]);
        //System.out.println("Velocity =  x: " + mVelocity.value[0] + ", y: " + mVelocity.value[1] + ", z: " + mVelocity.value[2]);

        double displVec[] = minus(mPositionData.mData.peekFirst().value,
                mPositionData.mData.peekLast().value);

        //System.out.println("Displacement = x: " + displVec[0] + ", y: " + displVec[1] + ", z: " + displVec[2]);

        if (lockTimeStart != 0) {
            if (System.currentTimeMillis() - lockTimeStart < GESTURE_LOCK_TIME)
                return NONE;
            lockTimeStart = 0;
        }

        if (lengthSqr(displVec) > 6
                && Math.abs(calculateCos(displVec, SWIPE_LEFT_NORMAL)) < MAX_GESTURE_ANGLE) {
            if (displVec[2] > 0) {
                lockTimeStart = System.currentTimeMillis();
                mTextViewOutput.setText("PLAY NEXT");
                return PLAY_NEXT;
            }
        }

        /*SensorData.AnalyzeData data = mAccelerometerData.analyze();



        double proportionXtoZ = Math.abs(data.integral[0] / data.integral[2]);
        double proportionXtoY = Math.abs(data.integral[0] / data.integral[1]);

        if (proportionXtoZ > MIN_GESTURE_ANGLE && proportionXtoY > MIN_GESTURE_ANGLE) {
            if (data.integral[0] > 0) {
                if (data.integral[0] < CHANGE_GESTURE_AVARAGE && data.integral[0] > 6000) {
                    lockTimeStart = System.currentTimeMillis();
                    mTextViewOutput.setText("PLAY NEXT");
                    return PLAY_NEXT;
                }
            }
            else {
        Scanner scanner = null;
        try (ServerSocket server = new ServerSocket(6699)) {
            System.out.println("Waiting for connection");
            Socket socket = server.accept();
            scanner = new Scanner(socket.getInputStream());

            System.out.println("Server connected");

            while (scanner.hasNext()) {
                String result = scanner.nextLine();
                System.out.println(result);
                JSONObject command = new JSONObject(result);

                System.out.println(command.getString("gesture"));
                System.out.println(command.getString("full_name"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
                if (-data.integral[0] < CHANGE_GESTURE_AVARAGE && -data.integral[0] > 4500) {
                    lockTimeStart = System.currentTimeMillis();
                    mTextViewOutput.setText("PLAY PREV");
                    return PLAY_PREV;
                }
            }
            //System.out.println("x: " + data.integral[0] + ", y: " + data.integral[1] + ", z: " + data.integral[2]);
        }

        double proportionZtoX = 1 / proportionXtoZ;
        double proportionZtoY = Math.abs(data.integral[2] / data.integral[1]);

        if (proportionZtoX > MIN_GESTURE_ANGLE && proportionZtoY > MIN_GESTURE_ANGLE) {
            if (data.integral[2] < 0) {
                if (data.integral[2] < CHANGE_GESTURE_AVARAGE && data.integral[2] > 6000) {
                    lockTimeStart = System.currentTimeMillis();
                    mTextViewOutput.setText("Pause");
                    return PAUSE;
                }
            }
            System.out.println("x: " + data.integral[0] + ", y: " + data.integral[1] + ", z: " + data.integral[2]);
        }*/

        return NONE;
    }

    private SensorEventListener mAccelerometerEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long currTime = System.currentTimeMillis();
            System.out.println("size: " + mPositionData.mData.size());
            mPositionData.dropOldData(currTime);
            System.out.println("size: " + mPositionData.mData.size());

            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            SensorData.Data newData = new SensorData.Data();
            newData.value = new double[] {x, y, z};
            newData.time = currTime;

            if (lengthSqr(newData.value) < 0.5)
                newData.value = new double[] {0, 0, 0};

            update(newData);

            if (lockTimeStart != 0) {
                mPositionData.empty();
            }

            int gesture = checkForGesture();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener mGyroscopeEventListener = new SensorEventListener() {
        private static final double NS2S = 1.0f / 1000000000.0f;
        private final float deltaRotationVector[] = new float[4];
        long timestamp = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            // This timestep's delta rotation to be multiplied by the current rotation
            // after computing it from the gyro sample data.
            if (timestamp != 0) {
                final double dT = (event.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                double axisX = event.values[0];
                double axisY = event.values[1];
                double axisZ = event.values[2];

                // Calculate the angular speed of the sample
                double omegaMagnitude = (double) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                double thetaOverTwo = omegaMagnitude * dT / 2.0f;
                double sinThetaOverTwo = Math.sin(thetaOverTwo);
                double cosThetaOverTwo = Math.cos(thetaOverTwo);
                deltaRotationVector[0] = (float) (sinThetaOverTwo * axisX);
                deltaRotationVector[1] = (float) (sinThetaOverTwo * axisY);
                deltaRotationVector[2] = (float) (sinThetaOverTwo * axisZ);
                deltaRotationVector[3] = (float) (cosThetaOverTwo);
            }
            timestamp = event.timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            float[] angles = new float[3];
            SensorManager.getOrientation(deltaRotationMatrix, angles);

            //System.out.println("azimuth: " + angles[0] + ", pitch: " + angles[1] + ", rol: " + angles[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener mProximityListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.out.println(event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    Kiwi kiwiInstance;

    DetectionCallback mMotionCallback = new DetectionCallback() {
        @Override
        public void onMotionDetected(DetectionInfo info) {
            mTextViewOutput.setText(info.motion.motionName);

        }

        @Override
        public void onScoreAvailable(DetectionInfo info) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextViewOutput = (TextView) stub.findViewById(R.id.text);
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        /*kiwiInstance = Kiwi.with(this);
        kiwiInstance.initApp("antoan.angelov@gmail.com", "3h4Y836hG57hBgg1Q2mq", new KiwiCallback() {
            @Override
            public void onUserInit() {
                List<String> motionIds = new ArrayList<String>();
                motionIds.add("73552ae73a33b96427856be80d09aaf8"); // bicep curl
                //kiwiInstance.setEnabledMotions(motionIds);
                kiwiInstance.setSensorUnits(SensorUnits.MS2_AND_RPS);
                kiwiInstance.setCallback(mMotionCallback);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });

        Sensor sensor = mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(mProximityListener, sensor, 1); */

        mSensorManager.registerListener(mAccelerometerEventListener, mAccelerometer, 1);
        mSensorManager.registerListener(mGyroscopeEventListener, mGyroscope, 1);
    }
}
