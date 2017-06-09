package com.wemen.samvedna.gesturefeature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Vishant on 6/8/2017.
 */

public class PlayAreaView extends View {
    private Matrix matrix;
    private Bitmap droid;
    private GestureDetector gestures;
    public static final String DEBUG_TAG = "PlayAreaView";

    private Matrix animateStart;
    private Interpolator animateInterpolator;
    private long startTime;
    private long endTime;
    private float totalAnimDx;
    private float totalAnimDy;

    public PlayAreaView(Context context) {
        super(context);
        matrix = new Matrix();
        gestures = new GestureDetector(context,
                new GestureListener(this));
        droid = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(droid,matrix,null);
        Matrix m = canvas.getMatrix();
        Log.d(DEBUG_TAG, "Matrix: "+matrix.toShortString());
        Log.d(DEBUG_TAG, "Canvas: "+m.toShortString());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestures.onTouchEvent(event);
    }

    public void onMove(float dx, float dy) {
        matrix.postTranslate(dx, dy);
        invalidate();
    }

    public void onResetLocation() {
        matrix.reset();
        invalidate();
    }

    private class GestureListener implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {

        PlayAreaView view;

        public GestureListener(PlayAreaView v) {
            this.view = v;
        }


        @Override
        public boolean onDown(MotionEvent motionEvent) {
            Log.v(DEBUG_TAG, "onDown");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            Log.v(DEBUG_TAG, "onScroll");

            view.onMove(-v,-v1);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            Log.v(DEBUG_TAG, "onFling");
            final float distanceTimeFactor = 0.4f;
            final float totalDx = (distanceTimeFactor * v/2);
            final float totalDy = (distanceTimeFactor * v1/2);

            view.onAnimateMove(totalDx, totalDy,
                    (long) (1000 * distanceTimeFactor));
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            Log.v(DEBUG_TAG, "onDoubleTap");
            view.onResetLocation();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return false;
        }
    }

    private void onAnimateMove(float totalDx, float totalDy, long duration) {
        animateStart = new Matrix(matrix);
        animateInterpolator = new OvershootInterpolator();
        startTime = System.currentTimeMillis();
        endTime = startTime + duration;
        totalAnimDx = totalDx;
        totalAnimDy = totalDy;
        post(new Runnable() {
            @Override
            public void run() {
                onAnimateStep();
            }
        });

    }

    private void onAnimateStep() {
        long curTime = System.currentTimeMillis();
        float percentTime = (float) (curTime - startTime)
                / (float) (endTime - startTime);
        float percentDistance = animateInterpolator
                .getInterpolation(percentTime);
        float curDx = percentDistance * totalAnimDx;
        float curDy = percentDistance * totalAnimDy;
        matrix.set(animateStart);
        onMove(curDx, curDy);
        Log.v(DEBUG_TAG, "We're " + percentDistance + " of the way there!");

    }
}
