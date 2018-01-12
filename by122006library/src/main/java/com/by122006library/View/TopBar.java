package com.by122006library.View;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.Functions.AttBinder.AttBinder;
import com.by122006library.Functions.AttBinder.AttChemotactic;
import com.by122006library.Functions.AttBinder.AttProgressListener;
import com.by122006library.Functions.AttBinder.MainAtt;
import com.by122006library.Functions.AttBinder.ViewAtt;
import com.by122006library.R;
import com.by122006library.SubclassAttribute;
import com.by122006library.Utils.ViewUtils;
import com.by122006library.databinding.TopbarBinding;
import com.by122006library.item.ColorStyle;

/**
 * Created by admin on 2017/6/15.
 */

public class TopBar {
    static private int defaultTopBarHeightdp = 70;
    TopbarBinding binder;
    BaseActivity act;
    ViewGroup vg;
    public static TopBar createIn(ViewGroup vg) {
        final BaseActivity act = (BaseActivity) vg.getContext();
        SubclassAttribute.with((BaseActivity) act).setFLAG_ACT_NO_TITLE(false);
        TopBar topBar = new TopBar();
        topBar.vg=vg;
        topBar.binder = DataBindingUtil.inflate(act.getLayoutInflater(), R.layout.topbar, null, false);
        topBar.act = act;
        vg.setLayoutParams(new LinearLayout.LayoutParams(-1, ViewUtils.dip2px(defaultTopBarHeightdp)));
        topBar.binder.getRoot().setLayoutParams(new LinearLayout.LayoutParams(-1, ViewUtils.dip2px
                (defaultTopBarHeightdp)));
        topBar.setLeftButton(  "返回"
                , new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act.finish();
                    }
                });
        topBar.removeRightButton();
        topBar.removeLeftButton();
        topBar.setTitle(act.getTitle());
        vg.setBackgroundColor(ColorStyle.getInitData().getTopBarBgColor());
        vg.addView(topBar.binder.getRoot());
        return topBar;
    }

    public void setBackgroundColor(int color) {
        vg.setBackgroundColor(color);
    }


    public static int getDefaultTopBarHeightdp() {
        return defaultTopBarHeightdp;
    }

    public static void setDefaultTopBarHeightdp(int topBarHeightdp) {
        defaultTopBarHeightdp = topBarHeightdp;
    }

    public void removeRightButton() {
        binder.rightbutton.setVisibility(View.INVISIBLE);
        binder.rightbutton.setClickable(false);
    }

    public void removeLeftButton() {
        binder.leftbutton.setVisibility(View.INVISIBLE);
        binder.leftbutton.setClickable(false);
    }

    public void setRightButton(String text, View.OnClickListener onClickListener) {
        binder.rightbutton.setText(text);
        binder.rightbutton.setTextColor(ColorStyle.getInitData().getTopBarTextColor());
        binder.rightbutton.setVisibility(View.VISIBLE);
        binder.rightbutton.setOnClickListener(onClickListener);
        binder.rightbutton.setClickable(true);
    }

    public void setLeftButton(String text, View.OnClickListener onClickListener) {
        binder.leftbutton.setText(text);
        binder.leftbutton.setTextColor(ColorStyle.getInitData().getTopBarTextColor());
        binder.leftbutton.setVisibility(View.VISIBLE);
        binder.leftbutton.setOnClickListener(onClickListener);
        binder.leftbutton.setClickable(true);
    }

    public void setTitle(CharSequence title) {
        binder.title.setTextColor(ColorStyle.getInitData().getTopBarTextColor());
        binder.title.setText(title);
    }

    public static class Action_LargeTopBg {
        private final TopBar topBar;
        private final TopbarBinding binder;
        private final BaseActivity act;

        int oHeight;
        Bitmap bitmap;
        private int maxHeight;

        public Action_LargeTopBg(TopBar topBar) {
            this.topBar = topBar;
            binder = topBar.binder;
            act = topBar.act;
        }

        public Action_LargeTopBg setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public Action_LargeTopBg setMaxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public Action_LargeTopBg apply() {
            binder.getRoot().post(new Runnable() {
                @Override
                public void run() {
                    oHeight = binder.getRoot().getHeight();
                    final AttBinder attBinder = new AttBinder();
                    final ViewAtt viewAtt = (ViewAtt) new ViewAtt(binder.getRoot(), ViewAtt.AttStyle.Height, oHeight, maxHeight).setAttNum(oHeight);
                    MainAtt mainAtt = new MainAtt(viewAtt.max, viewAtt.min) {
                        @Override
                        public void setOutSideListener() {

                            binder.getRoot().setLongClickable(true);
                            binder.getRoot().setOnTouchListener(new View.OnTouchListener() {
                                int downYPaddingBottom = 0;

                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            downYPaddingBottom = (int) (v.getHeight() - event.getY());
                                            break;
                                        case MotionEvent.ACTION_UP:
                                            AttChemotactic.chemotactic(500, binders.get(0));
                                            break;
                                    }
                                    float nowY = (event.getY() + downYPaddingBottom);
                                    fluctuation(nowY);

                                    return false;
                                }
                            });
                        }
                    }.setAttNum(viewAtt.min);

                    ImageView iv = new ImageView(act);
                    iv.setImageBitmap(bitmap);
                    iv.setLayoutParams(new ViewGroup.LayoutParams(binder.getRoot().getWidth(), maxHeight));
                    iv.setTranslationY(-maxHeight + oHeight);
                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                    ((ViewGroup) binder.getRoot()).addView(iv, 0);

                    ViewAtt bitmapAtt =  new ViewAtt(iv, ViewAtt.AttStyle.TranslationY,
                            -maxHeight + oHeight, 0);
                    ViewAtt bitmapAlphaAtt = (ViewAtt) new ViewAtt(iv, ViewAtt.AttStyle.Alpha, -0.2f, 1f);
                    final ViewAtt backAtt = (ViewAtt) new ViewAtt(binder.leftbutton, ViewAtt.AttStyle.Alpha, -0.2f, 1f)
                            .setReverse();
                    ViewAtt backAtt2 = (ViewAtt) new ViewAtt(binder.leftbutton, ViewAtt.AttStyle.TranslationX,-binder.leftbutton.getRight(), 0).setReverse();
                    ViewAtt backAtt3 = (ViewAtt) new ViewAtt(binder.leftbutton, ViewAtt.AttStyle.TranslationY,maxHeight/2-binder.title.getHeight()- ViewUtils.dip2px(10), binder.title.getTranslationY());
                    ViewAtt titleAtt = (ViewAtt) new ViewAtt(binder.title, ViewAtt.AttStyle.TranslationX, ViewUtils.dip2px(10), -binder.title.getLeft()){
                        @Override
                        public ViewAtt setAttNum(double num) {
                            view.setTranslationX((float) num);
                            return this;
                        }

                        @Override
                        public double getMin() {
                            return -view.getLeft();
                        }
                    }.setReverse();
                    ViewAtt titleAtt2 = (ViewAtt) new ViewAtt(binder.title, ViewAtt.AttStyle.TranslationY,maxHeight/2-binder.title.getHeight()- ViewUtils.dip2px(10), binder.title.getTranslationY());
                    ViewAtt titleAtt3 = (ViewAtt) new ViewAtt(binder.title, ViewAtt.AttStyle.Other, binder.title.getTextSize()*2, binder.title.getTextSize()){
                        @Override
                        public ViewAtt setAttNum(double num) {
                            ((TextView)view).setTextSize(TypedValue.COMPLEX_UNIT_PX ,(float) num);
                            return this;
                        }
                    };
                    attBinder.bind(viewAtt,bitmapAtt,bitmapAlphaAtt,mainAtt, backAtt,titleAtt,titleAtt2,titleAtt3,backAtt2,backAtt3);
                    attBinder.setAttProgressListener(new AttProgressListener() {
                        @Override
                        public void progress(double beforeAttNum, double nowAttNum, double nowAttPer) {

//                            mLog.i("progress  beforeAttNum=%f , nowAttNum=%f",beforeAttNum,nowAttNum);
                            if(beforeAttNum<nowAttNum &&beforeAttNum>0.9){
//                                mLog.i("关闭按键事件");
                                binder.leftbutton.setEnabled(false);
                            }
                            if(beforeAttNum>nowAttNum &&nowAttPer>0.9){
                                binder.leftbutton.setEnabled(true);
                            }
                        }
                    });
                    attBinder.fluctuation(0);
                    attBinder.setMinRefreshTime(16);
                }
            });
            return this;
        }


    }
}
