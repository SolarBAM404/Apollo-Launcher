package me.solar.apollolauncher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.loader.content.AsyncTaskLoader;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppLoader extends AsyncTaskLoader<ArrayList<AppModel>> {

    ArrayList<AppModel> mInstalledApps;

    final PackageManager mPackageManager;

    public AppLoader(Context context) {
        super(context);
        mPackageManager = context.getPackageManager();
    }

    @Override
    public ArrayList<AppModel> loadInBackground() {
        // retrieve the list of installed applications
        List<ApplicationInfo> apps = mPackageManager.getInstalledApplications(0);

        if (apps == null) {
            apps = new ArrayList<>();
        }

        final Context context = getContext();

        // create corresponding apps and load their labels and icons
        ArrayList<AppModel> items = new ArrayList<>(apps.size());
        for (int i = 0; i < apps.size(); i++) {
            String pkg = apps.get(i).packageName;

            if (context.getPackageManager().getLaunchIntentForPackage(pkg) != null) {
                AppModel app = new AppModel(context, apps.get(i));
                app.loadLabel(context);
                items.add(app);
            }
        }

        Collections.sort(items, ALPHA_COMPARATOR);
        return items;
    }

    @Override
    public void deliverResult(ArrayList<AppModel> apps) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (apps != null) {
                onReleaseResources(apps);
            }
            return;
        }

        ArrayList<AppModel> oldApps = mInstalledApps;
        mInstalledApps = apps;

        if (isStarted()) {
            super.deliverResult(apps);
        }

        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mInstalledApps != null) {
            deliverResult(mInstalledApps);
        }

        if (takeContentChanged() || mInstalledApps == null) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mInstalledApps != null) {
            onReleaseResources(mInstalledApps);
            mInstalledApps = null;
        }
    }

    /**
     * Helper method to do the cleanup wo rk if needed, for example if we're
     * using Cursor, then we should be closing it here
     *
     * @param apps
     */
    protected void onReleaseResources(ArrayList<AppModel> apps) {
        // do nothing
    }


    /**
     * Perform alphabetical comparison of application entry objects.
     */
    public static final Comparator<AppModel> ALPHA_COMPARATOR = new Comparator<AppModel>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(AppModel object1, AppModel object2) {
            return sCollator.compare(object1.getLabel(), object2.getLabel());
        }
    };
}
