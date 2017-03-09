package com.by122006library.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.by122006library.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Tools {



    /**
     * 图片左右拼合
     *
     * @param firstBitmap
     * @param secondBitmap
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth()
                        + secondBitmap.getWidth(),
                Math.max(firstBitmap.getHeight(), secondBitmap.getHeight()),
                firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, 0,
                (bitmap.getHeight() - firstBitmap.getHeight()) / 2, null);
        canvas.drawBitmap(secondBitmap, firstBitmap.getWidth(),
                (bitmap.getHeight() - firstBitmap.getHeight()) / 2, null);
        return bitmap;
    }

    /**
     * 将text制作为图片
     *
     * @param text
     * @param textSize
     * @param color
     * @return
     */
    public static Bitmap drawStringBitmap(String text, int textSize, int color) {
        Bitmap bitmap = Bitmap.createBitmap(
                Math.max(textSize * (text.length() * 2 / 3), 1), textSize,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(color);
        canvas.drawText(text, 0, 0, paint);
        return bitmap;
    }



    public static float countDistance_2(float x, float y, float f, float g) {
        return (x - f) * (x - f) + (y - g) * (y - g);
    }

    public static double countDistance_2(double x, double y, double f, double g) {
        return (x - f) * (x - f) + (y - g) * (y - g);
    }

    public static boolean showPopupWindow(final View showview,
                                          final View parentview) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                PopupWindow popup = new PopupWindow(showview);
                popup.setOutsideTouchable(true);
                popup.update();
                popup.setBackgroundDrawable(new BitmapDrawable());
                popup.showAtLocation(parentview, Gravity.CENTER, 0, -200);
                popup.setFocusable(false);
                for (int i = -200 + 5; i <= -100; i += 5) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    popup.showAtLocation(parentview, Gravity.CENTER, 0, i);
                }
            }
        });
        return true;
    }




    public static ViewGroup lockedLayout(View layout) {
        ViewGroup rootlayout = (ViewGroup) layout.getParent();
        FrameLayout newrootlayout = new FrameLayout(layout.getContext());
        int index = 0;
        if (rootlayout != null) {
            for (index = 0; index < rootlayout.getChildCount(); index++) {
                if (rootlayout.getChildAt(index) == layout)
                    break;
            }
            LayoutParams params = layout.getLayoutParams();
            rootlayout.removeView(layout);
            newrootlayout.setLayoutParams(params);
        }
//		ArrayList<View> vlist = new ArrayList<View>();
//		setAllChildViewList(vlist, layout, true);
//		for (View v : vlist) {
//			if (v instanceof ImageView) {
//				Drawable drawable = copy(loadBitmapFromView((ImageView) v));
//				if (drawable != null) {
//					drawable.setColorFilter(0xffcccccc,
//							PorterDuff.Mode.MULTIPLY);
//					((ImageView) v).setImageBitmap(((BitmapDrawable) drawable)
//							.getBitmap());
//				} else {
//					Log.e("error", "getDrawingCache()=null !");
//				}
//
//			}
//			if (v instanceof TextView) {
//				((TextView) v).setTextColor(0xff444444);
//			}
//			if (v.getBackground() != null) {
//				Drawable drawable = copy((Drawable) v.getBackground());
//				drawable.setColorFilter(0xffcccccc, PorterDuff.Mode.MULTIPLY);
//				v.setBackgroundDrawable(drawable);
//			}
//		}
        newrootlayout.addView(layout);
        ImageView iv = new ImageView(layout.getContext());
        iv.setImageResource(R.drawable.button_lock);
        int w = layout.getWidth() < 400 ? 100 : 200;
        iv.setLayoutParams(new LayoutParams(w, w));
        newrootlayout.addView(iv);
        if (rootlayout != null) {
            rootlayout.addView(newrootlayout, index);
        }
        return newrootlayout;

    }


    public static Bitmap copy(Bitmap bmsRc) {
        Bitmap bmCopy = Bitmap.createBitmap(bmsRc.getWidth(), bmsRc.getHeight(), bmsRc.getConfig());
        return bmCopy;
    }

    public static Drawable copy(Drawable bmsRc) {
        if (bmsRc == null) return null;
        return bmsRc.getConstantState().newDrawable();
    }


}
