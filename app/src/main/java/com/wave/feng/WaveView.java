package com.wave.feng;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liu on 2017/10/19.
 */

public class WaveView extends View{
    private final static float X_SPACE = 20.0f;
    private final int DEFAULT_WAVE_COLOR = 0xffffffff;
    private final int DEFAULT_WAVE_HEIGHT = 80;
    private final float DEFAULT_WAVE_LENGTH_MULTIPLE = 0.7f;
    private int waveColor = 0xffffffff;
    private int waveHeight = DEFAULT_WAVE_HEIGHT;
    private float waveLengthMultiple = DEFAULT_WAVE_LENGTH_MULTIPLE;

    private Path firstWavePath = new Path();
    private Path twoWavePath = new Path();
    private Path threeWavePath = new Path();

    private Paint firstWavePaint = new Paint();
    private Paint twoWavePaint = new Paint();
    private Paint threeWavePaint = new Paint();

    private float WaveHz = 0.05f;

    private float firstOffset = 0;
    private float twoOffset = 1.0f;
    private float threeOffset = 3.0f;


    private int maxRight = 0;

    private final double PI2 = 2 * Math.PI;
    // ω
    private double omega;


    private RefreshProgressRunnable mRefreshProgressRunnable;


    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        waveColor = array.getColor(R.styleable.WaveView_wave_color, DEFAULT_WAVE_COLOR);
        waveLengthMultiple = array.getFloat(R.styleable.WaveView_wave_length_multiple, DEFAULT_WAVE_LENGTH_MULTIPLE);
        array.recycle();
        init();
    }

    private void init() {
        firstWavePaint.setColor(waveColor);
        firstWavePaint.setStyle(Paint.Style.FILL);
        firstWavePaint.setAntiAlias(true);


        twoWavePaint.setColor(waveColor);
        twoWavePaint.setAlpha(140);
        twoWavePaint.setStyle(Paint.Style.FILL);
        twoWavePaint.setAntiAlias(true);

        threeWavePaint.setColor(waveColor);
        threeWavePaint.setAlpha(40);
        threeWavePaint.setStyle(Paint.Style.FILL);
        threeWavePaint.setAntiAlias(true);

    }


    /**
     * calculate wave track
     */
    private void calculatePath() {
        Log.d("aaa", "执行了");
        firstWavePath.reset();
        twoWavePath.reset();
        threeWavePath.reset();

        getWaveOffset();

        float y;
        firstWavePath.moveTo(getLeft(), getHeight() + 1);
        for (float x = 0; x <= maxRight; x += X_SPACE) {
            y = (float) (waveHeight * Math.sin(omega * x  + firstOffset) + waveHeight);
            Log.d("aaa", y + "---" + x);
            firstWavePath.lineTo(x, y);
        }
        firstWavePath.lineTo(getRight(), getHeight() + 1);
        twoWavePath.moveTo(getLeft(), getHeight());
        for (float x = 0; x <= maxRight; x += X_SPACE) {
            y = (float) (waveHeight * 0.8f * Math.sin(omega * x + twoOffset) + waveHeight);
            twoWavePath.lineTo(x, y);
        }
        twoWavePath.lineTo(getRight(), getHeight());

        threeWavePath.moveTo(getLeft(), waveHeight * 2);
        for (float x = 0; x <= maxRight; x += X_SPACE) {
            y = (float) (waveHeight * 0.6 * Math.sin(omega * x + threeOffset) + waveHeight);
            threeWavePath.lineTo(x, y);
        }
        threeWavePath.lineTo(getRight(), waveHeight * 2);
    }


    private void getWaveOffset() {
        if (firstOffset > Float.MAX_VALUE - 100) {
            firstOffset = 0;
        } else {
            firstOffset += WaveHz;
        }

        if (twoOffset > Float.MAX_VALUE - 100) {
            twoOffset = 0;
        } else {
            twoOffset += WaveHz;
        }

        if (threeOffset > Float.MAX_VALUE - 100) {
            threeOffset = 0;
        } else {
            threeOffset += WaveHz;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        maxRight = getRight();
        waveHeight = getHeight() / 2;
        omega = PI2 / getWidth() * waveLengthMultiple;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(firstWavePath, firstWavePaint);
        canvas.drawPath(twoWavePath, twoWavePaint);
        canvas.drawPath(threeWavePath, threeWavePaint);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }


    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (WaveView.this) {
                calculatePath();
                invalidate();
                long gap = 70;
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }
}
