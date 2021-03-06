package com.by122006library.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.Functions.ViewsReplace;
import com.by122006library.Functions.mLog;
import com.by122006library.MyException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by admin on 2017/2/21.
 */

public class ViewUtils {
    /**
     * 获得ImageView的图片
     *
     * @param v
     * @return
     */
    public static Drawable loadBitmapFromView(ImageView v) {
        Drawable drawable = null;
        try {
            Field field = v.getClass().getDeclaredField("mDrawable");
            field.setAccessible(true);
            drawable = (Drawable) field.get(v);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    public static void getAllChildViewList(ArrayList<View> vlist, View vg,
                                           boolean groupself) {
        if (groupself)
            vlist.add(vg);
        if (vg instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) vg).getChildCount(); i++) {
                getAllChildViewList(vlist, ((ViewGroup) vg).getChildAt(i),
                        groupself);
            }
        }
    }

    /**
     * 把一个View的对象转换成bitmap
     */
    public static Bitmap getViewBitmap(View v) throws MyException {
        if (v == null) throw new MyException("View不能为null");
        v.clearFocus();
        v.setPressed(false);

        // 能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }
    public static void countViews(ArrayList<View> views, ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        views.clear();
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                views.add(view);
                countViews(views, (ViewGroup) view);
            } else {
                views.add(view);
            }
        }
    }

    /**
     * 控件包裹方法 <p>Created by 122006
     *
     * @param v
     * @param viewGroupClassName 控件class
     * @return
     */
    public static <T extends ViewGroup> T surroundViewGroup(View v, Class<T> viewGroupClassName, Object... parameters) {
        T t = null;
        try {
            mLog.i(viewGroupClassName.getName());
            t = ReflectionUtils.newInstance(viewGroupClassName, parameters);
            mLog.isNull(t);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return surroundViewGroup(v, t);
    }

    /**
     * 控件包裹方法 <p>Created by 122006
     *
     * @param v
     * @param viewGroup 控件对象
     * @return
     */
    public static <T extends ViewGroup> T surroundViewGroup(View v, T viewGroup) {
        try {
            ViewGroup parent = null;
            int parentIndex = -1;
            if (viewGroup == null) return null;
            if (v.getParent() != null) {
                parent = (ViewGroup) v.getParent();
                parentIndex = parent.indexOfChild(v);
                parent.removeView(v);
            }
            viewGroup.addView(v);
            ViewGroup.MarginLayoutParams params = null;
            if (v.getLayoutParams() == null) {
                params = new ViewGroup.MarginLayoutParams(-2, -2);
            } else {
                params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            }
            viewGroup.setLayoutParams(params);
            if (parent == null) return viewGroup;
            parent.addView(viewGroup, parentIndex);
            return viewGroup;
        } catch (Exception e) {
            return viewGroup;
        }
    }

    public static int[] getWindowWH(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;    //得到宽度
        int height = dm.heightPixels;  //得到高度
        return new int[]{width, height};
    }

    /**
     * 控件介绍类按钮 <p>Created by 122006 <p>你需要将其运行在控件绘制后
     *
     * @param v                  要展示的控件对象
     * @param str                要显示的文字
     * @param roundRect          true:展示圆形控件框 false:矩形控件框
     * @param onDismissLinstener popup隐藏监听事件（用于跳转或下一个提示）
     */
    public static PopupWindow introduceView(View v, String str, boolean roundRect, PopupWindow.OnDismissListener
            onDismissLinstener) {
        final PopupWindow popupWindow = new PopupWindow();
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setWidth(-2);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        final ImageView iv = new ImageView(v.getContext());
        int wh[] = getWindowWH((Activity) v.getContext());
        View rootView = v.getRootView();
        final Bitmap rootbitmap = Bitmap.createBitmap(wh[0], wh[1], Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rootbitmap);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        Rect rect = new Rect();
        v.getGlobalVisibleRect(rect);
        final int r = (rect.height() + rect.width()) / 2;
        Bitmap mask = null;
        if (roundRect) {
            mask = Bitmap.createBitmap(r * 2, r * 2, Bitmap.Config.ARGB_8888);
            Canvas cc = new Canvas(mask);
            cc.drawCircle(r, r, r, paint);
        } else {
            int padding = 4;
            mask = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
            Canvas cc = new Canvas(mask);
            Rect rect2=new Rect(0,0, rect.width()+padding*2, rect.height()+padding*2);
            cc.drawRect(rect2, paint);
        }

        Bitmap bitmap2 = Bitmap.createBitmap(wh[0], wh[1], Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas(bitmap2);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        cc.drawRect(new Rect(0, 0, wh[0], wh[1]), paint);
        int sc = canvas.saveLayer(0, 0, wh[0], wh[1], null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(bitmap2, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        canvas.drawCircle(rect.centerX(), rect.centerY(), (rect.height() + rect.width()) / 2, paint);
        canvas.drawBitmap(mask, v.getX() + v.getWidth() / 2 - mask.getWidth() / 2, v.getY() + v.getHeight() / 2 -
                mask.getHeight() / 2, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        int x = rect.left;
        int y = rect.top;
        if(x + v.getWidth() / 2 > wh[0] / 2) {
            x -= 100;
        } else {
            x += 100+v.getWidth();
        }

        if(y + v.getHeight() / 2 > wh[1] / 2) {
            y -= 100;
        } else {
            y += 100+ v.getHeight();
        }
        canvas.drawText(str, x, y, paint);
        iv.setImageBitmap(rootbitmap);
        iv.setAlpha(0.8f);
//        iv.setBackgroundColor(Color.BLACK);
        iv.setClickable(true);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rootbitmap != null && !rootbitmap.isRecycled()) rootbitmap.recycle();
                popupWindow.dismiss();
            }
        });
        mask.recycle();
        bitmap2.recycle();
        popupWindow.setContentView(iv);
        popupWindow.setOnDismissListener(onDismissLinstener);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    /**
     * 控件介绍类按钮 <p>Created by 122006 <p>你需要将其运行在控件绘制后
     *
     * @param rect               要展示的Rect区域
     * @param str                要显示的文字
     * @param roundRect          true:展示圆形控件框 false:矩形控件框
     * @param onDismissLinstener popup隐藏监听事件（用于跳转或下一个提示）
     */
    public static PopupWindow introduceView(Activity context, Rect rect, String str, boolean roundRect, PopupWindow
            .OnDismissListener
            onDismissLinstener) {
        final PopupWindow popupWindow = new PopupWindow();
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setWidth(-2);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        final ImageView iv = new ImageView(context);
        int wh[] = getWindowWH((Activity) context);
        final Bitmap rootbitmap = Bitmap.createBitmap(wh[0], wh[1], Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rootbitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        final int r = (rect.height() + rect.width()) / 2;
        Bitmap mask = null;
        if (roundRect) {
            mask = Bitmap.createBitmap(r * 2, r * 2, Bitmap.Config.ARGB_8888);
            Canvas cc = new Canvas(mask);
            cc.drawCircle(r, r, r, paint);
        } else {
            int padding = 4;
            mask = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
            Canvas cc = new Canvas(mask);
            Rect rect2=new Rect(0,0, rect.width()+padding*2, rect.height()+padding*2);
            cc.drawRect(rect2, paint);
        }

        Bitmap bitmap2 = Bitmap.createBitmap(wh[0], wh[1], Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas(bitmap2);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        cc.drawRect(new Rect(0, 0, wh[0], wh[1]), paint);
        int sc = canvas.saveLayer(0, 0, wh[0], wh[1], null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(bitmap2, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        canvas.drawCircle(rect.centerX(), rect.centerY(), (rect.height() + rect.width()) / 2, paint);
        canvas.drawBitmap(mask, rect.left + rect.width() / 2 - mask.getWidth() / 2, rect.top + rect.height() / 2 -
                mask.getHeight() / 2, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        int x = (int) rect.left, y = (int) rect.top;
        if (x + rect.width() / 2 > wh[0] / 2) x -= 100;
        else x += 100;
        if (y + rect.height() / 2 > wh[1] / 2) y -= 100;
        else y += 100;
        canvas.drawText(str, x, y, paint);
        iv.setImageBitmap(rootbitmap);
        iv.setAlpha(0.8f);
//        iv.setBackgroundColor(Color.BLACK);
        iv.setClickable(true);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rootbitmap != null && !rootbitmap.isRecycled()) rootbitmap.recycle();
                popupWindow.dismiss();
            }
        });
        mask.recycle();
        bitmap2.recycle();
        popupWindow.setContentView(iv);
        popupWindow.setOnDismissListener(onDismissLinstener);
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    /**
     * dp转px
     */
    public static int dip2px(float dpValue) {
        final float scale;
        try {
            scale = BaseActivity.getContext().getResources().getDisplayMetrics().density;
        } catch (MyException e) {
            return 0;
        }
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dip(float pxValue) {
        final float scale;
        try {
            scale = BaseActivity.getContext().getResources().getDisplayMetrics().density;
        } catch (MyException e) {
            return 0;
        }
        return (int) (pxValue / scale + 0.5f);
    }

    public static View getLayout(Context context, @LayoutRes int res) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(res, null);
    }

    /**
     * 将某控件从父布局中移除，该操作会检查可能出错的位置
     */
    public static <T extends View> T removeViewFromParents(T view) {
        if (view.getParent() != null && ((ViewGroup) view.getParent()).indexOfChild(view) >= 0)
            ((ViewGroup) view.getParent()).removeView(view);
        return view;
    }

    public static void smoothReplace_Smart(View oldV, View newV, long dur) {
        DebugUtils.runningDurtime();
        if (oldV.getParent() != null && newV.getParent() == null) {
            smoothReplace(oldV, newV, dur);
        } else if (oldV.getParent() == null && newV.getParent() != null) {
            smoothReplace(newV, oldV, dur);
        } else if (oldV.getParent() != null && newV.getParent() != null) {
            removeViewFromParents(newV);
            smoothReplace(oldV, newV, dur);
        } else if (oldV.getParent() == null && newV.getParent() == null) {
            mLog.e("转化错误，新旧布局均为自由布局");
        }
    }

    /**
     * 平滑替换<p> 该变换会使新控件完全继承于旧控件所在的布局属性<p>旧控件会被移除
     *
     * @param oldV 原控件
     * @param newV 新控件
     * @param dur  转变时间
     */
    @SuppressLint("NewApi")
    public static void smoothReplace(final View oldV, final View newV, long dur) {
        //todo 需要新建类以解决线程安全性
        ViewsReplace.from(oldV).to(newV).setDurTime(dur).transform();
    }

    public void showAdapterViewEmptyView(AdapterView view, int imgres, String showTxt) {
        LinearLayout rootlayout = new LinearLayout(view.getContext());
        ImageView iv = new ImageView(view.getContext());
        iv.setImageResource(imgres);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(200, 200);
        iv.setLayoutParams(new LinearLayout.LayoutParams(iv.getMeasuredWidth() / 3, iv.getMeasuredHeight() / 5));
        rootlayout.addView(iv);
        TextView tv = new TextView(rootlayout.getContext());
        tv.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        tv.setText(showTxt);
        rootlayout.addView(tv);

        rootlayout.setVisibility(View.GONE);
        ((ViewGroup) view.getParent()).addView(rootlayout);
        view.setEmptyView(tv);
    }

    /**
     * 判断界面是否绘制结束<p> 用于自定义控件的onDraw()
     *
     * @return
     */
    public boolean isActivityFinished() {
        try {
            return BaseActivity.getTopBaseActivity().getDecorView().getWidth() != 0;
        } catch (MyException e) {
            mLog.i("获取decor界面失败");
            return false;
        }
    }


}