package me.solar.apollolauncher;

import android.content.SharedPreferences;

public class AppSettings {

    private static final int GRID_COL_SIZE = 6;
    private static final int GRID_ROW_SIZE = 4;

    private static final int HOME_PAGE_COUNT = 3;

    private SharedPreferences mPrefs;

    public AppSettings(SharedPreferences prefs) {
        mPrefs = prefs;
    }

    public int getGridColSize() {
        return mPrefs.getInt("grid_col_size", GRID_COL_SIZE);
    }

    public void setGridColSize(int size) {
        mPrefs.edit().putInt("grid_col_size", size).apply();
    }

    public int getGridRowSize() {
        return mPrefs.getInt("grid_row_size", GRID_ROW_SIZE);
    }

    public void setGridRowSize(int size) {
        mPrefs.edit().putInt("grid_row_size", size).apply();
    }

    public int getHomePageCount() {
        return mPrefs.getInt("home_page_count", HOME_PAGE_COUNT);
    }

    public void setHomePageCount(int count) {
        mPrefs.edit().putInt("home_page_count", count).apply();
    }

}
