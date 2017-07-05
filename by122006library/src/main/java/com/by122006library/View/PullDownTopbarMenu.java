package com.by122006library.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.by122006library.R;
import com.by122006library.Utils.MathUtils;
import com.by122006library.Utils.ViewUtils;

/**
 * Created by admin on 2017/6/15.
 */

public class PullDownTopbarMenu extends PopupWindow {
    LinearLayout rootLayout;

    Context context;
//    boolean Flag_Scrolling = false;
    int topbarh = 0;
    View contextLayout, sliderView, topView;


    long pullSpeed = 300;
    private Builder builder;

    public PullDownTopbarMenu(Context context) {
        this.context = context;
        rootLayout = new LinearLayout(context);
        rootLayout.setBackgroundColor(Color.BLUE);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        setOutsideTouchable(false);
        setBackgroundDrawable(new BitmapDrawable());
        //设置弹出窗体需要软键盘，
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//再设置模式，和Activity的一样，覆盖。
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public PullDownTopbarMenu show() {
        topView.post(new Runnable() {
            @Override
            public void run() {
                setWidth(topView.getWidth());
                setHeight(-2);
                try {
                    showAsDropDown(topView);
                }catch (Exception e){}
            }
        });

//        ((Activity)context).getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int
//                    oldRight, int oldBottom) {
//                setWidth(topView.getWidth());
//                setHeight(-2);
//                update(topView,0,0);
//            }
//        });
        return this;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void scrollToClose(long time) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -(rootLayout.getHeight
                () - (sliderView == null ? 0 : sliderView.getHeight()) + rootLayout.getTranslationY()));
        animation.setDuration(time);
//        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootLayout.clearAnimation();
                rootLayout.setTranslationY(-(rootLayout.getHeight() -
                        (sliderView == null ? 0 : sliderView.getHeight())));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rootLayout.startAnimation(animation);
    }

    public void scrollToSide(long time) {
        float thisy = rootLayout.getHeight() + rootLayout
                .getTranslationY();
        if (thisy > rootLayout.getHeight() / 2) {
            scrollToClose(time);
        } else {
            scrollToOpen(time);
        }
    }


    public void scrollToOpen(long time) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -rootLayout
                .getTranslationY());
        animation.setDuration(time);
//        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootLayout.clearAnimation();
                rootLayout.setTranslationY(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rootLayout.startAnimation(animation);
    }

    public static class Builder {
        PullDownTopbarMenu pullDownTopbarMenu;
        Context context;
        boolean ifUseSliderView = true;

        private int backGround=0xff607d8b;

        public Builder(Context context) {
            this.context = context;
            pullDownTopbarMenu = new PullDownTopbarMenu(context);
        }

        public static Builder create(Context context) {
            return new Builder(context);
        }

        public Builder setContextLayout(View v) {
            pullDownTopbarMenu.contextLayout = v;
            return this;
        }

        public Builder setTopView(View v) {
            pullDownTopbarMenu.topView = v;
            return this;
        }

        public Builder setPullSpeed(long pullSpeed) {
            pullDownTopbarMenu.pullSpeed = pullSpeed;
            return this;
        }

        public Builder setSliderView(View v) {
            if(v==null){
                v=new View(context);
                v.setLayoutParams(new ViewGroup.LayoutParams(0,0));
            }
            pullDownTopbarMenu.sliderView = v;
            return this;
        }

        public void useSliderView(boolean ifUseSliderView) {
            this.ifUseSliderView = ifUseSliderView;
        }

        public PullDownTopbarMenu build() {
            pullDownTopbarMenu.builder = this;
            if (pullDownTopbarMenu.contextLayout == null) {
                TextView tv = new TextView(context);
                tv.setText("没有内容");
                tv.setHeight(200);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.RED);
                tv.setTextSize(20);
                pullDownTopbarMenu.contextLayout = tv;
            }
            if (ifUseSliderView && pullDownTopbarMenu.sliderView == null) {
                ImageView iv = new ImageView(context);
                iv.setImageResource(R.drawable.dividingstrip);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setLayoutParams(new LinearLayout.LayoutParams(-1, ViewUtils.dip2px(5)));
                iv.setBackgroundColor(Color.WHITE);
                setSliderView(iv);
            }
            pullDownTopbarMenu.rootLayout.setBackgroundColor(backGround);
            pullDownTopbarMenu.rootLayout.setLongClickable(true);
            pullDownTopbarMenu.rootLayout.setClickable(true);
            pullDownTopbarMenu.rootLayout.setOnTouchListener(new View.OnTouchListener() {
                float downY, downTranslationY;
                long downTime;
                int rooth;

                @Override
                public boolean onTouch(View view, MotionEvent ev) {
                    if (pullDownTopbarMenu.topbarh == 0)
                        pullDownTopbarMenu.topbarh = pullDownTopbarMenu.topView.getBottom();

//                    pullDownTopbarMenu.Flag_Scrolling = false;
                    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                        downY = ev.getRawY();
                        downTranslationY = pullDownTopbarMenu.rootLayout.getTranslationY();
                        downTime = System.currentTimeMillis();
                        rooth = pullDownTopbarMenu.rootLayout.getHeight();
                    }
//                    mLog.array("ev.getRawY()="+ev.getRawY(),"ev.getY()="+ev.getY(),"topbarh="+pullDownTopbarMenu
// .topbarh,"downY="+downY,"rooth="+rooth,"contextLayout="+pullDownTopbarMenu.contextLayout.getHeight());
                    float h = ev.getRawY() - downY + downTranslationY;
                    pullDownTopbarMenu.rootLayout.setTranslationY((float) MathUtils.getLegalNumInRange(h,
                            -pullDownTopbarMenu.contextLayout.getHeight(), 0d));
                    if (ev.getAction() == MotionEvent.ACTION_UP) {
                        float thisy = rooth + pullDownTopbarMenu.rootLayout
                                .getTranslationY();
                        if (thisy < rooth) {
                            if (thisy < rooth / 2) {
                                pullDownTopbarMenu.scrollToClose(pullDownTopbarMenu.pullSpeed);
                            } else {
                                pullDownTopbarMenu.scrollToOpen(pullDownTopbarMenu.pullSpeed);
                            }
                        } else {
                            pullDownTopbarMenu.scrollToOpen(pullDownTopbarMenu.pullSpeed);
                        }
                        long curTime = System.currentTimeMillis() - downTime;
                        if (curTime < pullDownTopbarMenu.pullSpeed && ev.getY() - downTime < pullDownTopbarMenu.topbarh)
                            pullDownTopbarMenu.scrollToSide(pullDownTopbarMenu.pullSpeed);
                    }
                    return true;
                }
            });
            pullDownTopbarMenu.rootLayout.removeAllViews();
            pullDownTopbarMenu.rootLayout.addView(pullDownTopbarMenu.contextLayout);
            if (ifUseSliderView) pullDownTopbarMenu.rootLayout.addView(pullDownTopbarMenu.sliderView);
            pullDownTopbarMenu.setContentView(pullDownTopbarMenu.rootLayout);

            return pullDownTopbarMenu;
        }

        public int getBackGround() {
            return backGround;
        }

        public Builder setBackGround(int backGround) {
            this.backGround = backGround;
            return this;
        }
    }


}
