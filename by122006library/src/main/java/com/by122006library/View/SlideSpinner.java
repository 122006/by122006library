package com.by122006library.View;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.by122006library.Functions.mLog;
import com.by122006library.R;
import com.by122006library.Utils.MathUtils;

import me.grantland.widget.AutofitTextView;

/**
 * Created by 122006 on 2017/6/14.
 */

public class SlideSpinner extends PopupWindow {
    Activity context;
    FrameLayout layout;
    boolean useCardView = true;
    int itemHeight = 0;
    OnItemSelectedListener selectedListener;
    long everyTime;
    int[] itemsColor;
    private String[] data;
    private TextView defaultTv;
    private int startColor = -2, endColor = -2;
    private int fistItemBgColor = -2;
    private ScrollView sv;

    public SlideSpinner(Activity context) {
        super(context);
        this.context = context;
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    public static SlideSpinner ClickBy(final View v) {
        final SlideSpinner slideSpinner = new SlideSpinner((Activity) v.getContext());
        slideSpinner.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                v.setTag(R.id.Tag_SlideSpinner, null);
            }
        });
        v.setClickable(true);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag(R.id.Tag_SlideSpinner) != null && view.getTag(R.id.Tag_SlideSpinner) instanceof
                        SlideSpinner) {
                    ((SlideSpinner) view.getTag(R.id.Tag_SlideSpinner)).dismiss();
                    return;
                }
                if (slideSpinner.getItemHeight() == 0) slideSpinner.setItemHeight(slideSpinner.defaultTv.getHeight());
                if (slideSpinner.getWidth() == 0) slideSpinner.setWidth(slideSpinner.defaultTv.getWidth());

                slideSpinner.showDropDown(view);
                view.setTag(R.id.Tag_SlideSpinner, slideSpinner);
            }
        });

        return slideSpinner;
    }
    @Override
    public void setWidth(int width){
        super.setWidth(width);
    }


    public boolean isUseCardView() {
        return useCardView;
    }

    public SlideSpinner setUseCardView(boolean useCardView) {
        this.useCardView = useCardView;
        return this;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public SlideSpinner setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
        return this;
    }

    public SlideSpinner setData(String... data) {
        this.data = data;
        return this;
    }

    public SlideSpinner setTextView(View tv) {
        this.defaultTv = (TextView) tv;
        setWidth(tv.getWidth());
        return this;
    }

    public SlideSpinner setTextView(View tv, String defaultStr) {
        this.defaultTv = (TextView) tv;
        defaultTv.setText(defaultStr);
        setWidth(tv.getWidth());
        return this;
    }

    public SlideSpinner setTextView(View tv, int index) {
        this.defaultTv = (TextView) tv;
        try {
            defaultTv.setText(data[index]);
        } catch (Exception e) {
        }
        setWidth(tv.getWidth());
        return this;
    }

    private void build() {
        sv = new ScrollView(context) ;
        sv.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        sv.setFillViewport(true);
        sv.setVerticalScrollBarEnabled(false);
        layout = new FrameLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(-1, LinearLayout.LayoutParams.FILL_PARENT));
        if (useCardView) {
            CardView cv = new CardView(context);
            cv.setRadius(5);
            cv.addView(layout);
            setContentView(cv);
        } else {
            sv.addView(layout);
            setContentView(sv);
        }
    }

    public long getEveryTime() {
        return everyTime;
    }

    public SlideSpinner setEveryTime(long everyTime) {
        this.everyTime = everyTime;
        return this;
    }

    public OnItemSelectedListener getSelectedListener() {
        return selectedListener;
    }

    public SlideSpinner setSelectedListener(OnItemSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
        return this;
    }

    public SlideSpinner setFistItemBgColor(int fistItemBgColor) {
        this.fistItemBgColor = fistItemBgColor;
        return this;
    }

    public int[] getItemsColor() {
        return itemsColor;
    }

    public SlideSpinner setItemsColor(int[] itemsColor) {
        this.itemsColor = itemsColor;
        return this;
    }

    public SlideSpinner setLinearColor(int start, int end) {
        startColor = start;
        endColor = end;
        return this;
    }
    private int textColor= Color.WHITE,separateColor=Color.GRAY;

    private int maxShowCount=20;
    public SlideSpinner setBgColor(int end) {
        startColor = end;
        endColor = end;
        return this;
    }

    public SlideSpinner showDropDown(final View v) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!v.isShown()) return;
                build();
                setHeight(itemHeight*maxShowCount);
                layout.setMinimumHeight(itemHeight*data.length);

                final View[] items = new View[data.length];
                for (int i = 0; i < data.length; i++) {
                    final AutofitTextView tv = new AutofitTextView(context);
                    tv.setText(data[i]);
                    tv.setLayoutParams(new ViewGroup.LayoutParams(-1, itemHeight));
//                    tv.setPadding(defaultTv.getPaddingLeft(), defaultTv.getPaddingTop(), defaultTv.getPaddingRight(),
//                            defaultTv.getPaddingBottom());
                    tv.setGravity(defaultTv.getGravity());
                    tv.setLines(1);
                    tv.setTextSize(defaultTv.getTextSize() / 3);
                    tv.setTextColor(getTextColor());
                    tv.setTranslationY(-itemHeight);
                    if (itemsColor != null) {
                        tv.setBackgroundColor(fistItemBgColor != -2 && i == 0 ? fistItemBgColor : itemsColor[i]);
                    } else if (startColor != -2 && endColor != -2) {
                        int r = MathUtils.mappingRange(i, 0, data.length, Color.red(startColor), Color.red(endColor));
                        int g = MathUtils.mappingRange(i, 0, data.length, Color.green(startColor), Color.green
                                (endColor));
                        int b = MathUtils.mappingRange(i, 0, data.length, Color.blue(startColor), Color.blue(endColor));
                        tv.setBackgroundColor(fistItemBgColor != -2 && i == 0 ? fistItemBgColor : Color.argb(255, r,
                                g, b));
                    } else {
                        tv.setBackgroundColor(Color.argb(255, (int) (Math.random() * 155) + 100, (int) (Math.random()
                                * 105) + 80, (int) (Math.random() * 105) + 80));
                    }

                    final int finalI = i;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                            selectedListener.onItemSelected(SlideSpinner.this, defaultTv, data, data[finalI], finalI);
                        }
                    });
                    tv.setClickable(false);
                    layout.addView(tv, 0);
                    items[i] = tv;
                }

                showAsDropDown(v);

                AnimationSet set = new AnimationSet(true);
                for (int i = 0; i < data.length; i++) {
                    TranslateAnimation animation = new TranslateAnimation(0, 0, 0, (i + 1) * itemHeight);
                    animation.setDuration(everyTime);
                    final int finalI = i;
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            items[finalI].clearAnimation();
                            items[finalI].setTranslationY(itemHeight * finalI);
                            items[finalI].setClickable(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            mLog.i(String.format("v%d  y=%d", finalI, items[finalI].getTranslationY()));
//                            if (finalI == data.length - 1) {
//                                setHeight((int) (items[data.length - 1].getTranslationY() + itemHeight));
//                            }
                        }
                    });
                    items[i].setAnimation(animation);
                    set.addAnimation(animation);
                }
                set.start();
            }
        });
        return this;
    }

    /**
     * 手动选择
     *
     * @param index
     */
    public void select(int index) {
        selectedListener.onItemSelected(SlideSpinner.this, defaultTv, data, data[index], index);
    }

    public int getMaxShowCount() {
        return maxShowCount;
    }

    public void setMaxShowCount(int maxShowCount) {
        this.maxShowCount = maxShowCount;
    }

    public int getTextColor() {
        return textColor;
    }

    public SlideSpinner setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public int getSeparateColor() {
        return separateColor;
    }

    @Deprecated
    public SlideSpinner setSeparateColor(int separateColor) {
        this.separateColor = separateColor;
        return this;
    }

    ;

    public interface OnItemSelectedListener {
        void onItemSelected(SlideSpinner slideSpinner, TextView textView, String[] data, String chooseStr, int
                chooseIndex);

        void onNoChoose(SlideSpinner slideSpinner, TextView textView, String[] data);
    }


}
