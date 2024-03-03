package me.solar.apollolauncher.misc;

import android.app.Activity;

public abstract class SingletonActivity<T> extends Activity implements Singleton {

    private static SingletonActivity<?> sInstance = null;

    protected void setInstance(SingletonActivity<T> instance) {
        SingletonActivity.sInstance = instance;
    }

    public static SingletonActivity<?> getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("Singleton instance not initialized");
        }
        return sInstance;
    }


}
