package me.solar.apollolauncher.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.solar.apollolauncher.AppSettings;
import me.solar.apollolauncher.R;
import me.solar.apollolauncher.misc.SingletonActivity;

public class HomeActivity extends SingletonActivity<HomeActivity> {

    private AppSettings mSettings;
    private ViewPager2 mViewPager;
    private PagerActivity mPagerAdapter;

    private static HomeAppModel[][] homeApps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInstance(this);
        mSettings = new AppSettings(getPreferences(Activity.MODE_PRIVATE));

        setContentView(R.layout.activity_home);

        HomeAppModel[][] mAppsArray = getAppsArray();

        mViewPager = findViewById(R.id.view_pager);
        mPagerAdapter = new PagerActivity(this, mAppsArray);
        mViewPager.setAdapter(mPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    private List<HomeAppModel> getApps() {
        // retrieve the list of installed applications
        List<ApplicationInfo> apps = getPackageManager().getInstalledApplications(0);

        if (apps == null) {
            apps = new ArrayList<>();
        }

        // create corresponding apps and load their labels and icons
        ArrayList<HomeAppModel> items = new ArrayList<>(apps.size());
        for (int i = 0; i < apps.size(); i++) {
            String pkg = apps.get(i).packageName;

            if (getPackageManager().getLaunchIntentForPackage(pkg) != null) {
                HomeAppModel app = new HomeAppModel(this, apps.get(i));
                app.loadLabel(this);
                items.add(app);
            }
        }

        return items;
    }

    private HomeAppModel[][] getAppsArray() {
        if (homeApps != null) {
            return homeApps;
        }

        HomeAppModel[][] appModels = HomeActivity.load(this);

        if (appModels != null) {
            HomeActivity.homeApps = appModels;
            return appModels;
        }

        List<HomeAppModel> apps = getApps();
        int appsPerPage = mSettings.getGridRowSize() * mSettings.getGridColSize();
        HomeAppModel[][] appsArray = new HomeAppModel[mSettings.getHomePageCount()][appsPerPage];

        // Initialize the array with null values
        for (HomeAppModel[] page : appsArray) {
            Arrays.fill(page, null);
        }

        // Assign apps to their positions
        for (HomeAppModel app : apps) {
            int page = app.getPage();
            int position = app.getPosition();

            if (page == -1 || position == -1) {
                Pair<Integer, Integer> nextAvailablePosition = getNextAvailablePosition(appsArray);
                if (nextAvailablePosition == null) {
                    continue;
                }
                page = nextAvailablePosition.first;
                position = nextAvailablePosition.second;
                app.setPage(page);
                app.setPosition(position);
            }

            if (page < mSettings.getHomePageCount() && position < appsPerPage) {
                appsArray[page][position] = app;
            }
        }

        HomeActivity.homeApps = appsArray;
        return appsArray;
    }

    public static void save(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("home_apps", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        int appsOnHome = 0;

        for (int i = 0; i < homeApps.length; i++) {
            for (int j = 0; j < homeApps[i].length; j++) {
                if (homeApps[i][j] != null) {
                    String json = gson.toJson(homeApps[i][j]);
                    editor.putString("home_app_" + appsOnHome, json);
                    appsOnHome++;
                }
            }
        }

        editor.putInt("apps_on_home", appsOnHome);
        editor.apply();
    }

    public static HomeAppModel[][] load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("home_apps", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        int appsOnHome = prefs.getInt("apps_on_home", 0);

        if (appsOnHome == 0) {
            return null;
        }

        HomeActivity homeActivity = (HomeActivity) HomeActivity.getInstance();
        int pageCount = homeActivity.getSettings().getHomePageCount();
        int itemCount = homeActivity.getSettings().getGridColSize() * homeActivity.getSettings().getGridRowSize();

        HomeAppModel[][] homeApps = new HomeAppModel[pageCount][itemCount];

        for (int i = 0; i < appsOnHome; i++) {
            String json = prefs.getString("home_app_" + i, null);
            if (json != null) {
                HomeAppModel app = gson.fromJson(json, HomeAppModel.class);
                homeApps[app.getPage()][app.getPosition()] = app;
            }
        }

        return homeApps;
    }

    private Pair<Integer, Integer> getNextAvailablePosition(HomeAppModel[][] appsArray) {
        for (int i = 0; i < appsArray.length; i++) {
            for (int j = 0; j < appsArray[i].length; j++) {
                if (appsArray[i][j] == null) {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
    }

    public void moveToNextPageOrAddNew(int draggedPosition, RecyclerView itemView, ItemTouchHelper.SimpleCallback simpleCallback) {
        // Get the current page
        int currentPage = mViewPager.getCurrentItem();

        // Check if there is a next page
        if (currentPage < mPagerAdapter.getItemCount() - 1) {
            // Move to the next page
            mViewPager.setCurrentItem(currentPage + 1);
        } else {
            // Add a new page
            mPagerAdapter.addPage();
            mPagerAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(mPagerAdapter.getItemCount() - 1);
        }

    }

    public AppSettings getSettings() {
        return mSettings;
    }


}
