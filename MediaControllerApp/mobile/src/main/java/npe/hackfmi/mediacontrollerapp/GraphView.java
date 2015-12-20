package npe.hackfmi.mediacontrollerapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;

/**
 * Created by wetslap7 on 12/20/15.
 */
public class GraphView extends View {

    public SensorData mData;
    public int mAxis;
    public long mScale = 900000;

    private Paint mPaint;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        mPaint =  new Paint();
        mPaint.setStyle(Paint.Style.FILL);

    }

    private void drawCoordinateSystem(Canvas canvas) {

        canvas.drawARGB(255, 0, 0, 50);

        int start = getHeight() / 2;
        int offset = getHeight() / 6;

        mPaint.setARGB(255, 255, 0, 0);
        canvas.drawLine(0, start, getWidth(), start, mPaint);
        mPaint.setARGB(255, 50, 0, 0);

        while (offset <= getHeight() / 2) {
            canvas.drawLine(0, start - offset, getWidth(), start - offset, mPaint);
            canvas.drawLine(0, start + offset, getWidth(), start + offset, mPaint);

            offset += getHeight() / 6;
        }


    }

    private void plot(Canvas canvas) {

        System.out.println("SIZE: " + mData.size());

        if (mData.size() < 2)
            return;

        mPaint.setARGB(255, 205, 205, 205);

        Iterator itr = mData.mData.descendingIterator();
        SensorData.Data prev = (SensorData.Data) itr.next();
        SensorData.Data first = prev;


        while(itr.hasNext()) {
            System.out.println("SIZE: " + mData.size());
            SensorData.Data curr = (SensorData.Data) itr.next();

            if (mData.isEmpty())
                curr = new SensorData.Data();

            canvas.drawLine((prev.time - first.time) / mScale,
                    (int) prev.value[mAxis] * 100 + getHeight() / 2,
                    (curr.time - first.time) / mScale,
                    (int) curr.value[mAxis] * 100 + getHeight() / 2,
                    mPaint);

            if ((curr.time - first.time) / mScale > getWidth())
                break;

            prev = curr;

        }

    }

    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        drawCoordinateSystem(canvas);
        plot(canvas);

    }
}