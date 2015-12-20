package npe.hackfmi.mediacontrollerapp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Created by wetslap7 on 12/19/15.
 */
public class SensorData {
    public static final long MAX_GESTURE_DURATION = 2000;

    public static class Data {
        public double value[] = new double[3];
        public long time;
    }

    public static class AnalyzeData {
        public double integral[] = new double[3];
        public long time;
    }

    public Deque<Data> mData = new ArrayDeque<>();

    public void dropOldData(long time) {
        if (mData.isEmpty())
            return;

        long oldestDataTime = mData.peekLast().time;

        while ((time - oldestDataTime) > MAX_GESTURE_DURATION) {
            mData.pollLast();
            if (mData.isEmpty())
                return;
            oldestDataTime = mData.peekLast().time;
        }
    }

    public AnalyzeData analyze() {
        AnalyzeData result = new AnalyzeData();

        if (mData.isEmpty())
            return result;

        Iterator itr = mData.iterator();
        Data curr = (Data) itr.next();
        Data prev;

        while(itr.hasNext()) {
            prev = curr;
            curr = (Data) itr.next();
            double delta = (curr.time - prev.time) / 2.0;
            for (int i = 0; i < 3; ++i)
                result.integral[i] += (curr.value[i] + prev.value[i]) * delta;
        }

        result.integral[2] /= 1.5;
        return result;
    }

    public void empty() {
        System.out.println("EMPTY!!!");
        while(!mData.isEmpty()) {
            mData.pop();
        }
    }

    public SensorData integrateData() {

        SensorData integratedData = new SensorData();

        if (mData.isEmpty())
            return integratedData;

        Iterator itr = mData.iterator();
        SensorData.Data curr = (SensorData.Data) itr.next();
        SensorData.Data prev;

        double prevValue[] = new double[3];

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

    public void add(Data newData) {
        mData.push(newData);
    }
}
