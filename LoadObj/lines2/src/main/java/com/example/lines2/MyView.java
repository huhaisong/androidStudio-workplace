package com.example.lines2;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;


public class MyView extends View {

    private static final String TAG = "MyView";

    float width, height;
    private static final float K1 = 0.25f;
    private static final float K2 = 0.2f;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        width = canvas.getWidth();
        height = canvas.getHeight();

        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);

/*        canvas.drawLine(0, height / 2, width, height / 2, paint);
        canvas.drawLine(0, height / 3, width, height / 3, paint);
        canvas.drawLine(0, height / 4, width, height / 4, paint);
        canvas.drawLine(0, height / 6, width, height / 6, paint);
        canvas.drawLine(0, height / 12, width, height / 12, paint);

        canvas.drawLine(width / 2, 0, width / 2, height, paint);
        canvas.drawLine(width / 3, 0, width / 3, height, paint);
        canvas.drawLine(width / 4, 0, width / 4, height, paint);
        canvas.drawLine(width / 6, 0, width / 6, height, paint);
        canvas.drawLine(width / 12, 0, width / 12, height, paint);*/

        for (int i = 0; i < 50; i++) {
            drawLine(0, i * height / 50, width, i * height / 50, canvas, paint, 100);
            drawLine(i*width / 50, 0, i*width / 50, height, canvas, paint, 100);
        }


       /* drawLine(0, height / 3, width, height / 3, canvas, paint, 100);
        drawLine(0, height / 4, width, height / 4, canvas, paint, 100);
        drawLine(0, height / 6, width, height / 6, canvas, paint, 100);
        drawLine(0, height / 12, width, height / 12, canvas, paint, 100);

        drawLine(width / 3, 0, width / 3, height, canvas, paint, 100);
        drawLine(width / 4, 0, width / 4, height, canvas, paint, 100);
        drawLine(width / 6, 0, width / 6, height, canvas, paint, 100);
        drawLine(width / 12, 0, width / 12, height, canvas, paint, 100);*/

        super.onDraw(canvas);
    }

    private void drawLine(float x1, float y1, float x2, float y2, Canvas canvas, Paint paint, int count) {

        Path path = new Path();
        path.reset();
        path.moveTo(x1, y1);
        float xSpacing = Math.abs(x2 - x1) / count;
        float ySpacing = Math.abs(y2 - y1) / count;
        float x, y;
        float tx, ty;
        float r;


        for (int i = 0; i < count; i++) {
            x = x1 + xSpacing * i;
            y = y1 + ySpacing * i;

            tx = x / width - 0.5f;
            ty = y / height - 0.5f;

            r = tx * tx + ty * ty;

            tx = tx / (1 + K1 * r + K2 * r * r);
            ty = ty / (1 + K1 * r + K2 * r * r);

            x = (tx + 0.5f) * width;
            y = (ty + 0.5f) * height;

            path.lineTo(x, y);
        }
        canvas.drawPath(path, paint);
        path.reset();
    }
}