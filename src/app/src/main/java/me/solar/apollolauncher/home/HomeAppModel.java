package me.solar.apollolauncher.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

import com.google.gson.Gson;

import me.solar.apollolauncher.AppModel;

public class HomeAppModel extends AppModel {

    private int mPosition = -1;
    private int mPage = -1;
    private boolean mShowApp;

    public HomeAppModel(Context context, ApplicationInfo info) {
        super(context, info);
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

}
