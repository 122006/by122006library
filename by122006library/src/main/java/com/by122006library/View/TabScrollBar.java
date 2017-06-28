package com.by122006library.View;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.by122006library.Functions.AttBinder.AttBinder;
import com.by122006library.Functions.AttBinder.ViewAtt;
import com.by122006library.R;
import com.by122006library.Utils.MathUtils;
import com.by122006library.Utils.ViewUtils;
import com.by122006library.databinding.ItemTabscrollbarBinding;

import java.util.ArrayList;

/**
 * Created by 122006 on 2017/6/19.
 */

public class TabScrollBar extends LinearLayout {
    ScrollView sv = null;
    int smallWidth = ViewUtils.dip2px(20), smallHeight = ViewUtils.dip2px(40);
    int largeWidth = ViewUtils.dip2px(40), largeHeight = ViewUtils.dip2px(40);
    int showCenter = smallHeight / 2;
    int lastScrollY = 0;
    ArrayList<Item> list = new ArrayList<Item>();
    private int nowIndex = 0;
    /**
     * 用于用户手指离开MyScrollView的时候获取MyScrollView滚动的Y距离，然后回调给onScroll方法中
     */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int scrollY = getScrollY();

            //此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
            if (lastScrollY != scrollY) {
                lastScrollY = scrollY;
                handler.sendMessageDelayed(handler.obtainMessage(), 5);
            }
            svOnScroll(scrollY);

        }

        ;

    };
    private float downY, downX, downRawY, downRawX;
    private long downTime;

    private TabScrollBar(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getNowItem().binding.vTv.getAlpha() == 0) {halfDissmissItemView();}
                if (getNowItem().binding.vTv.getAlpha() == 1) halfShowItemView();
            }
        });
        post(new Runnable() {
            @Override
            public void run() {
                smallHeight =getHeight();
                largeHeight = smallHeight;
            }
        });

    }

    public static TabScrollBar createBar(Context context) {
        TabScrollBar tabScrollBar = new TabScrollBar(context);
        tabScrollBar.setLayoutParams(new FrameLayout.LayoutParams(tabScrollBar.smallWidth, tabScrollBar.smallHeight));
        ((FrameLayout.LayoutParams) tabScrollBar.getLayoutParams()).gravity = Gravity.RIGHT | Gravity.TOP;

        return tabScrollBar;
    }

    public void setScrollView(ScrollView sv) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        ViewUtils.surroundViewGroup(sv, frameLayout);
        sv.addView(this);
        frameLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                svOnScroll(lastScrollY = getScrollY());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        handler.sendMessageDelayed(handler.obtainMessage(), 5);
                        return true;
                }
                return false;
            }
        });
    }

    public void svOnScroll(int scrollY) {
        showCenter = MathUtils.mappingRange(scrollY, 0, sv.getHeight(), smallHeight / 2, sv.getMeasuredHeight() -
                smallHeight / 2);
        setY(showCenter - smallHeight / 2);
        for (int i = 0; i < list.size(); i++) {
            Item item = list.get(i);
            if (item.checkIndex != null && item.checkIndex.isThisIndex()) {
                if (i != nowIndex) {
                    changeItem_small_to_small(i, nowIndex);
                }
                nowIndex = i;
                break;
            }
        }
    }

    /**
     * 前后不一样
     *
     * @param beforeIndex
     * @param nowIndex
     */
    public void changeItem_small_to_small(int beforeIndex, int nowIndex) {
        View v1 = getItemIndexBinder(beforeIndex).getRoot();
        View v2 = getItemIndexBinder(nowIndex).getRoot();
        AttBinder attBinder = new AttBinder();
        ViewAtt viewAtt = (ViewAtt) new ViewAtt(v1, ViewAtt.AttStyle.Alpha, 0, 255).setReverse();
        ViewAtt viewAtt2 = new ViewAtt(v2, ViewAtt.AttStyle.Alpha, 0, 255);
        attBinder.startWithDuringTime(200).bind(viewAtt2, viewAtt);
    }

    /**
     * 半隐藏控件
     */
    public void halfDissmissItemView() {
        AttBinder attBinder = new AttBinder();
        for (Item item : list) {
            item.binding.vTv.setAlpha(0);
            item.binding.hTv.setAlpha(1);
            attBinder.bind((ViewAtt) new ViewAtt(item.binding.vTv, ViewAtt.AttStyle.Alpha, 0, 255));
            attBinder.bind((ViewAtt) new ViewAtt(item.binding.hTv, ViewAtt.AttStyle.Alpha, 0, 255).setReverse());
            if (!item.equals(getNowItem())) {
                attBinder.bind((ViewAtt) new ViewAtt(item.binding.getRoot(), ViewAtt.AttStyle.Alpha, 0, 255)
                        .setReverse());
            }
        }
        ViewAtt att = new ViewAtt(this, ViewAtt.AttStyle.TranslationX, 0, getWidth() / 3);
        attBinder.bind(att).startWithDuringTime(300);
    }

    /**
     * 半显示控件
     */
    public void halfShowItemView() {
        AttBinder attBinder = new AttBinder();
        for (Item item : list) {
            item.binding.vTv.setAlpha(1);
            item.binding.hTv.setAlpha(0);
            attBinder.bind((ViewAtt) new ViewAtt(item.binding.vTv, ViewAtt.AttStyle.Alpha, 0, 255).setReverse());
            attBinder.bind((ViewAtt) new ViewAtt(item.binding.hTv, ViewAtt.AttStyle.Alpha, 0, 255));
            if (!item.equals(getNowItem())) {
                attBinder.bind((ViewAtt) new ViewAtt(item.binding.getRoot(), ViewAtt.AttStyle.Alpha, 0, 255));
            }
        }
        ViewAtt att = (ViewAtt) new ViewAtt(this, ViewAtt.AttStyle.TranslationX, 0, getWidth() / 3)
                .setReverse();
        attBinder.bind(att).startWithDuringTime(300);
    }

    public ItemTabscrollbarBinding getItemIndexBinder(int index) {
        return list.get(index).binding;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                downX = ev.getX();
                downRawY = ev.getRawY();
                downRawX = ev.getRawX();
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                showCenter = (int) (ev.getRawY() - ev.getY() + downY);
                setY(showCenter - getNowItem().binding.getRoot().getHeight() / 2);
                if (downTime != 0 && System.currentTimeMillis() - downTime > 300) {
                    halfShowItemView();
                    downTime = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
                showCenter = (int) (ev.getRawY() - ev.getY() + downY);
                setY(showCenter - getNowItem().binding.getRoot().getHeight() / 2);
                if(downTime==0) halfDissmissItemView();

                break;
        }

        return super.onTouchEvent(ev);
    }

    public void addItem(String name, int bgColor, Bitmap icon, int txColor, @Nullable OnClickListener onClick,
                        @NonNull CheckIndex checkIndex) {
        Item item = new Item(name);
        item.bgColor = bgColor;
        item.icon = icon;
        item.txColor = txColor;
        item.onClick = onClick;
        item.checkIndex = checkIndex;
        list.add(item);
    }

    ;

    public int getNowIndex() {
        return nowIndex;
    }

    public Item getNowItem() {
        return list.get(nowIndex);
    }

    public void showItem(int index) {
        if (list.get(index).onClick != null)
            list.get(index).onClick.onClick(this);
    }

    public abstract class CheckIndex {
        public abstract boolean isThisIndex();
    }

    public class Item {
        String name;
        int bgColor, txColor;
        Bitmap icon;
        CheckIndex checkIndex;
        OnClickListener onClick;
        ItemTabscrollbarBinding binding;

        public Item(String name) {
            this.name = name;
        }

        public View getView() {
            binding = DataBindingUtil.inflate(((Activity) getContext()).getLayoutInflater(), R.layout.item_tabscrollbar, null, false);
            binding.setData(this);
            return binding.getRoot();
        }


    }

}
