package npe.hackfmi.mediacontrollerapp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Created by wetslap7 on 12/19/15.
 */
public class SensorData {
    public static final long MAX_GESTURE_DURATION = 900000000;

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
        while(!mData.isEmpty()) {
            mData.pop();
        }
    }

    public void add(Data newData) {
        mData.push(newData);
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    public int size() {
        return mData.size();
    }

    public void push(Data data) {
        mData.push(data);
    }
}
