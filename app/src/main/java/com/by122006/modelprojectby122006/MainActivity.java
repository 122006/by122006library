package com.by122006.modelprojectby122006;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.by122006.modelprojectby122006.databinding.ActivityMainBinding;
import com.by122006.modelprojectby122006.databinding.ItemMainActivityListViewStyleBinding;
import com.by122006library.Activity.BaseActivity;
import com.by122006library.Utils.BitmapUtils;
import com.by122006library.Utils.ReflectionUtils;
import com.by122006library.Utils.ViewUtils;
import com.by122006library.View.TopBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fynn.fluidlayout.FluidLayout;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * by122006<p> </>
 */
public class MainActivity extends BaseActivity {
    static public Class<? >[] class1=new Class[]{SV_ViewsReplace.class,SV_SlideSpinner.class,SV_PullDownTopbarMenu.class};
    static public Class<? >[] class2=new Class[]{SV_ViewsReplace.class,SV_SlideSpinner.class,SV_PullDownTopbarMenu.class};
    static public Class<? >[] class3=new Class[]{SV_ViewsReplace.class,SV_SlideSpinner.class,SV_PullDownTopbarMenu.class};
    static public Class<? >[] class4=new Class[]{SV_ViewsReplace.class,SV_SlideSpinner.class,SV_PullDownTopbarMenu.class};


     public String name = "by122006库 演示";
    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;
    ActivityMainBinding binding = null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding.setAct(this);
        binding.recycer.setLayoutManager(new LinearLayoutManager(this));
        List<PinnedHeaderEntity<Object>> data = new ArrayList<>();
        data.add(new PinnedHeaderEntity<>(null, BaseHeaderAdapter.TYPE_HEADER, "原创控件"));
        for (Object item :class1) {
            data.add(new PinnedHeaderEntity<Object>(item, BaseHeaderAdapter.TYPE_DATA, "原创控件"));
        }
        data.add(new PinnedHeaderEntity<>(null, BaseHeaderAdapter.TYPE_HEADER, "原创功能"));
        for (Object item :class2) {
            data.add(new PinnedHeaderEntity<Object>(item, BaseHeaderAdapter.TYPE_DATA, "原创功能"));
        }
        data.add(new PinnedHeaderEntity<>(null, BaseHeaderAdapter.TYPE_HEADER, "第三方"));
        for (Object item :class3) {
            data.add(new PinnedHeaderEntity<Object>(item, BaseHeaderAdapter.TYPE_DATA, "第三方"));
        }
        data.add(new PinnedHeaderEntity<>(null, BaseHeaderAdapter.TYPE_HEADER, "开发中"));
        for (Object item :class4) {
            data.add(new PinnedHeaderEntity<Object>(item, BaseHeaderAdapter.TYPE_DATA, "开发中"));
        }
        MyAdapter adapter=new MyAdapter(data);

        binding.recycer.setAdapter(adapter);
        binding.recycer.setItemAnimator(new DefaultItemAnimator());
        binding.recycer.setHasFixedSize(true);
        getTopBar().setTitle(name);
        getTopBar().setLeftButton("退出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        (new TopBar.Action_LargeTopBg(getTopBar())).setMaxHeight(800).setBitmap(BitmapUtils.getBitmap(this, R
                .drawable.topbar_bg, 0)).apply();
        binding.recycer.addItemDecoration(new PinnedHeaderItemDecoration.Builder(BaseHeaderAdapter.TYPE_HEADER)
                .setHeaderClickListener(null).create());
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Class clazz=(Class) ((PinnedHeaderEntity<Object> )adapter.getData().get(position)).getData();
                startActivity(new Intent(MainActivity.this,clazz));
            }
        });

    }


    class MyAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<Object>>{
        private SparseIntArray mRandomHeights;

        public MyAdapter(List<PinnedHeaderEntity<Object>> data) {
            super(data);
        }



        @Override
        protected void convert(BaseViewHolder holder, PinnedHeaderEntity<Object> item) {
            switch (holder.getItemViewType()) {
                case BaseHeaderAdapter.TYPE_HEADER:
                    holder.setText(R.id.name, item.getPinnedHeaderName());
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
                    Class clazz = (Class) ((PinnedHeaderEntity<Object> )getData().get(position)).getData();
                    try {
                        final FunctionActivity functionActivity = (FunctionActivity) ReflectionUtils.newInstance(clazz);
                        holder.setVisible(R.id.photo,false);
                        holder.setText(R.id.describe,functionActivity.getFuncitonDescribe()!=null?functionActivity.getFuncitonDescribe():"没有描述数据");
                        holder.setText(R.id.title,functionActivity.getFunctionEnName()!=null?functionActivity.getFunctionEnName():"Unknown");
                        holder.setText(R.id.title2,functionActivity.getFunctionChName()!=null?functionActivity.getFunctionChName():"Unknown");
                        functionActivity.loadFuncitonCachePhoto( ((ImageView) holder.getView(R.id.photo)));
                        ((FluidLayout)holder.getView(R.id.fluidlayout)).removeAllViews();
                        ((FluidLayout)holder.getView(R.id.fluidlayout)).setGravity(Gravity.LEFT);
                        String tags[] = functionActivity.getFuncitonTags();
                        for (String tag : tags) {
                            TextView tv = new TextView(MainActivity.this);
                            tv.setText(tag);
                            tv.setTextSize(12);
                            tv.setGravity(Gravity.CENTER);
                            tv.setTextColor(0xaa000000);
                            FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                                    FluidLayout.LayoutParams.WRAP_CONTENT,
                                    FluidLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(10, 5, 10, 5);
                            CardView cardView=new CardView(MainActivity.this);
                            cardView.addView(tv);
                            cardView.setCardElevation(ViewUtils.dip2px(2));
                            cardView.setContentPadding(ViewUtils.dip2px(5),ViewUtils.dip2px(1),ViewUtils.dip2px(5),ViewUtils.dip2px(1));
                            cardView.setRadius(ViewUtils.dip2px(6));
                            ((FluidLayout)holder.getView(R.id.fluidlayout)).addView(cardView,params);
                        }



                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }



        @Override
        protected void addItemTypes() {
            addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_main_activity_pinnedheader);
            addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_main_activity_list_view_style);
        }

        class ViewVH extends RecyclerView.ViewHolder {
            ItemMainActivityListViewStyleBinding binding;

            public ViewVH(ItemMainActivityListViewStyleBinding binding) {
                super((View) binding.getRoot().getParent());
                this.binding=binding;
            }

        }


    }

}


