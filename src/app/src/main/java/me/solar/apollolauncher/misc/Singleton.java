package me.solar.apollolauncher.misc;

public interface Singleton {

    static Singleton sInstance = null;

    static Singleton getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("Singleton instance not initialized");
        }
        return sInstance;
    }

}
