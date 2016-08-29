package com.kerwin.leafProgressBar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.cpxiao.leafprogressbar.R;

/**
 * Created by kerwin on 16/8/26.
 */
public class LearnView extends View {
    private Paint mPaint0;
    private Paint mPaint1;
    private Paint mPaint2;
    private Bitmap bitmap;

    public LearnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint0 = new Paint();
        mPaint1 = new Paint();
        mPaint2 = new Paint();
        mPaint0.setStyle(Paint.Style.FILL);
        mPaint1.setStyle(Paint.Style.FILL);
        mPaint2.setStyle(Paint.Style.FILL);
        mPaint0.setColor(Color.RED);
        mPaint1.setColor(Color.BLACK);
        mPaint2.setColor(Color.YELLOW);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //        Rect rect = new Rect(0, 0, 400, 400);
        //        canvas.drawRect(rect, mPaint0);
        //        canvas.save();
        //        canvas.scale(0.5f, 0.5f);
        //        canvas.drawRect(rect, mPaint1);
        //        canvas.restore();
        //        canvas.translate(100, 100);
        //        canvas.rotate(45, 100, 100);
        //        Rect rect1 = new Rect(0, 0, 200, 200);
        //        canvas.drawRect(rect1, mPaint2);
        for (int i = 1; i < 5; i++) {
            canvas.scale((float) 1 / i, (float) 1 / i);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }
}
