package com.by122006.modelprojectby122006;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.by122006.modelprojectby122006.databinding.LayoutFunctionsSpaceBinding;
import com.by122006library.Utils.ViewUtils;
import com.by122006library.View.PullDownTopbarMenu;
import com.by122006library.View.SlideSpinner;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fynn.fluidlayout.FluidLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/6/28.
 */

public abstract class SF extends SV {
    RecyclerView recycer;
    ControlAdapter adapter;
    ArrayList<String> strs = new ArrayList<String>();
    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.layoutViewAtt.setVisibility(View.GONE);
        LayoutFunctionsSpaceBinding binding2 = DataBindingUtil.inflate(getLayoutInflater(), R.layout
                .layout_functions_space, null, false);
        recycer = binding2.recycer;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);
        params.setMargins(ViewUtils.dip2px(10), ViewUtils.dip2px(5), ViewUtils.dip2px(10), ViewUtils.dip2px(5));
        binding2.getRoot().setLayoutParams(params);
        layout_observer = binding2.layoutObserver;
        binding.space.addView(binding2.getRoot());
        List<PinnedHeaderEntity<LogData>> list = new ArrayList<PinnedHeaderEntity<LogData>>();
        adapter = new ControlAdapter(list);
        recycer.setAdapter(adapter);
        recycer.setLayoutManager(new LinearLayoutManager(this));

        SlideSpinner.LongClickBy(binding2.layoutRefreshDurtime).clickTurn().setEveryTime(300).setLinearColor
                (0xffd4ee57, 0xffe6ee9c).setTextView(binding2.tvRefreshDurtime).setData("1ms", "16ms", "100ms",
                "300ms", "750ms", "1000ms", "5000ms").setSelectedListener(new SlideSpinner.OnItemSelectedListener() {


            @Override
            public void onItemSelected(SlideSpinner slideSpinner, TextView textView, String[] data, String chooseStr,
                                       int chooseIndex) {
                durObserverTime = Long.valueOf(chooseStr.replace("ms", ""));
                textView.setText(chooseStr);
            }

            @Override
            public void onNoChoose(SlideSpinner slideSpinner, TextView textView, String[] data) {

            }
        }).select(2);


    }

    public void showViewAtt() {

    }

    @Override
    public PullDownTopbarMenu getPullDownTopbarMenu() {
        return null;
    }

    @Override
    public View putLayoutSpace(ViewGroup space) {
        return null;
    }

    public void println(String str) {
        println(str, Color.BLACK);
    }

    public void println(String str, int tvColor) {
        strs.add(str);
        adapter.addData(new PinnedHeaderEntity<LogData>(new LogData(System.currentTimeMillis(), str, tvColor),
                BaseHeaderAdapter.TYPE_DATA, ""));
        adapter.notifyDataSetChanged();
        recycer.smoothScrollToPosition(adapter.getData().size() - 1);
    }


    public void clear(View v) {
        strs.clear();
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clickItem() {
        startThisActivity(optContext());
    }


    class ControlAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<LogData>> {
        private SparseIntArray mRandomHeights;

        public ControlAdapter(List<PinnedHeaderEntity<LogData>> list) {
            super(list);
        }


        @Override
        protected void convert(BaseViewHolder holder, PinnedHeaderEntity<LogData> item) {
            switch (holder.getItemViewType()) {
                case BaseHeaderAdapter.TYPE_HEADER:
                    holder.setText(R.id.name, item.getPinnedHeaderName().startsWith("TiaoShi") ? "演示程序 调试方法" : item
                            .getPinnedHeaderName());
                    break;
                case BaseHeaderAdapter.TYPE_DATA:

                    int position = holder.getLayoutPosition();

                    if (binding.recycer.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                        // 瀑布流布局记录随机高度，就不会导致Item由于高度变化乱跑，导致画分隔线出现问题
                        // 随机高度, 模拟瀑布效果.

                        if (mRandomHeights == null) {
                            mRandomHeights = new SparseIntArray(getItemCount());
                        }

                        if (mRandomHeights.get(position) == 0) {
                            mRandomHeights.put(position, ViewUtils.dip2px((int) (100 + Math.random() * 100)));
                        }

                        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                        lp.height = mRandomHeights.get(position);
                        holder.itemView.setLayoutParams(lp);

                    }
                    LogData d = (LogData) ((PinnedHeaderEntity<LogData>) getData().get(position)).getData();
                    FluidLayout fluidLayout = (FluidLayout) holder.getView(R.id.fluidlayout);
                    fluidLayout.removeAllViews();
                    TextView textView = new TextView(SF.this);
                    textView.setText(Html.fromHtml(d.str));
                    textView.setTextColor(d.tvColor);
                    fluidLayout.addView(textView);
                    break;
            }
        }


        @Override
        protected void addItemTypes() {
            addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_sv_pinnedheader);
            addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_sv_smethod);
        }

    }


    private class LogData {
        long time;
        String str;
        int tvColor;

        LogData(long time, String str, int tvColor) {
            this.time = time;
            this.str = str.replace("\n","<br>").replace(" ","&nbsp;&nbsp;").replace("\b","&nbsp;&nbsp;");
            this.tvColor = tvColor;
        }
    }

}
