package me.solar.apollolauncher.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Collections;

import me.solar.apollolauncher.R;

public class HomeAppListAdapter extends RecyclerView.Adapter<HomeAppListAdapter.ViewHolder> {
    private final HomeAppModel[] appArray;
    private final Context mContext;
    private final int mPage;

    public HomeAppListAdapter(Context context, HomeAppModel[] apps, int page) {
        this.mContext = context;
        appArray = apps;
        mPage = page;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeAppModel app = appArray[position];
        if (app == null) {
            holder.appLabel.setText("");
            holder.appIcon.setImageDrawable(null);
            return;
        }

        holder.appLabel.setText(app.getLabel());
        holder.appIcon.setImageDrawable(app.getIcon());
    }

    @Override
    public int getItemCount() {
        return appArray.length;
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(Arrays.asList(appArray), fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        HomeAppModel app = appArray[toPosition];
        app.setPage(mPage);
        app.setPosition(toPosition);
//        HomeActivity.save(mContext);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appLabel;

        ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.home_card_app_icon);
            appLabel = itemView.findViewById(R.id.home_card_app_label);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    HomeAppModel app = appArray[position];
                    if (app == null) {
                        return;
                    }

                    String appPackageName = app.getApplicationPackageName();
                    if (appPackageName == null || appPackageName.isEmpty()) {
                        return;
                    }

                    Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(appPackageName);
                    if (intent != null) {
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}