package npe.hackfmi.mediacontrollerapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kiwiwearables.kiwilib.DetectionCallback;
import com.kiwiwearables.kiwilib.DetectionInfo;
import com.kiwiwearables.kiwilib.Kiwi;
import com.kiwiwearables.kiwilib.KiwiCallback;
import com.kiwiwearables.kiwilib.Motion;
import com.kiwiwearables.kiwilib.SensorUnits;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class MainActivity extends Activity {

    private GraphView mGraphViewOx;
    private GraphView mGraphViewOy;
    private GraphView mGraphViewOz;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;

    public SensorData mAccelerometerData = new SensorData();


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


    private SensorData integrateData(SensorData data) {

        SensorData integratedData = new SensorData();

        if (data.mData.isEmpty())
            return integratedData;

        Iterator itr = data.mData.iterator();
        SensorData.Data curr = (SensorData.Data) itr.next();
        SensorData.Data prev;

        while(itr.hasNext()) {
            prev = curr;
            curr = (SensorData.Data) itr.next();

            SensorData.Data newValue = new SensorData.Data();

            double delta = (curr.time - prev.time) / 2.0;
            newValue.time = prev.time + (long) delta;

            for (int i = 0; i < 3; ++i)
                newValue.value[i] += prevValue[i] + (curr.value[i] + prev.value[i]) * delta;

            integratedData.add(newValue);

            prevValue = newValue.value;

            //System.out.println("x: " + newValue.value[0] + ", y: " + newValue.value[1] + ", z: " + newValue.value[2]);
            //System.out.println("time: " + newValue.time);
        }

        return integratedData;
    }

    private SensorEventListener mAccelerometerEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mAccelerometerData.dropOldData(event.timestamp);

            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            SensorData.Data newData = new SensorData.Data();
            newData.value = new double[] {x, y, z};
            newData.time = event.timestamp;

            if (lengthSqr(newData.value) < 1)
                newData.value = new double[] {0, 0, 0};

            mAccelerometerData.push(newData);

            mGraphViewOx.invalidate();
            mGraphViewOy.invalidate();
            mGraphViewOz.invalidate();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGraphViewOx = (GraphView) findViewById(R.id.graphview_ox);
        mGraphViewOy = (GraphView) findViewById(R.id.graphview_oy);
        mGraphViewOz = (GraphView) findViewById(R.id.graphview_oz);

        mGraphViewOx = (GraphView) findViewById(R.id.graphview_ox);
        mGraphViewOy = (GraphView) findViewById(R.id.graphview_oy);
        mGraphViewOz = (GraphView) findViewById(R.id.graphview_oz);

        mGraphViewOx.mData = mAccelerometerData;
        mGraphViewOy.mData = mAccelerometerData;
        mGraphViewOz.mData = mAccelerometerData;

        mGraphViewOx.mAxis = 0;
        mGraphViewOy.mAxis = 1;
        mGraphViewOz.mAxis = 2;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(mAccelerometerEventListener, mAccelerometer, 1);
        //mSensorManager.registerListener(mGyroscopeEventListener, mGyroscope, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
