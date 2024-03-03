package me.solar.apollolauncher.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

public class PagerActivity extends RecyclerView.Adapter<PagerActivity.ViewHolder> {

    private HomeAppModel[][] mPages;
    private final Context mContext;
    private GestureDetector mGestureDetector;

    private static boolean mIsEditMode = false;
    private static boolean mIsDragging = false;
    private CountDownTimer mCountDownTimer;
    private int mDraggedPosition = -1;

    public PagerActivity(Context context, HomeAppModel[][] pages) {
        mContext = context;
        mPages = pages;
        mGestureDetector = new GestureDetector(context, new HomeGestureListener(mContext));
    }


    @NonNull
    @Override
    public PagerActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        recyclerView.setLayoutManager(new GridLayoutManager(mContext,
                ((HomeActivity) mContext).getSettings().getGridRowSize()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        return new ViewHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerActivity.ViewHolder holder, int position) {
        HomeAppModel[] page = mPages[position];
        HomeAppListAdapter adapter = new HomeAppListAdapter(mContext, page, position);
        ((RecyclerView) holder.itemView).setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                mIsDragging = true;
                adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Handle swipe events if needed
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                mIsDragging = false;
            }

            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    mIsDragging = true;
                    int margin = 450;

                    mDraggedPosition = viewHolder.getAdapterPosition();
                    ItemTouchHelper.SimpleCallback simpleCallback = this;

                    if (dX >= recyclerView.getWidth() - viewHolder.itemView.getWidth() - margin) {
                        if (mCountDownTimer != null) {
                            return;
                        }
                        Log.i("PagerActivity", "Countdown started");
                        mCountDownTimer = new CountDownTimer(1500, 1500) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                // Do nothing
                            }

                            @Override
                            public void onFinish() {
                                ((HomeActivity) mContext).moveToNextPageOrAddNew(mDraggedPosition, recyclerView, simpleCallback);
                                Log.i("PagerActivity", "Move to next page");
                            }
                        }.start();
                    } else if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                        mCountDownTimer = null;
                        Log.i("PagerActivity", "Countdown cancelled");
                    }
                }
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView((RecyclerView) holder.itemView);

    }

    @Override
    public int getItemCount() {
        return mPages.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnTouchListener((v, event) -> {
                v.performClick();
                return mGestureDetector.onTouchEvent(event);
            });
        }

    }

    public static boolean isEditMode() {
        return mIsEditMode;
    }

    public static boolean isDragging() {
        return mIsDragging;
    }

    public void addPage() {
        HomeAppModel[][] newPages = new HomeAppModel[mPages.length + 1][];
        System.arraycopy(mPages, 0, newPages, 0, mPages.length);
        newPages[mPages.length] = new HomeAppModel[0];
        mPages = newPages;
        notifyDataSetChanged();
    }
}
