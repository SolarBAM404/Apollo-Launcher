package me.solar.apollolauncher.home;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import me.solar.apollolauncher.drawer.AppDrawerActivity;

public class HomeGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 50;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private Context mContext;

    public HomeGestureListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (PagerActivity.isEditMode()) {
            return false;
        }

        if (e1 == null || e2 == null) {
            return false;
        }

        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();

        if (diffY < SWIPE_THRESHOLD) {
            onSwipeTop();
        } else if (diffY > -SWIPE_THRESHOLD) {
            onSwipeBottom();
        }

        return false;
    }

    private void onSwipeBottom() {
        Log.d("HomeGestureListener", "onSwipeBottom");
    }

    private void onSwipeTop() {
        Log.d("HomeGestureListener", "onSwipeTop");
        showApps();
    }

    public void showApps() {
        Intent i = new Intent(mContext, AppDrawerActivity.class);
        startActivity(mContext, i, null);
    }

}
