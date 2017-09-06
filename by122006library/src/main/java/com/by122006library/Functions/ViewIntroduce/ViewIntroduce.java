package com.by122006library.Functions.ViewIntroduce;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.by122006library.Functions.mLog;
import com.by122006library.Utils.ViewUtils;

/**
 * Created by admin on 2017/9/5.
 */

public class ViewIntroduce {
    String str;
    Rect rect;
    Activity context;
    ShapeStyle shapeStyle;
    private int[] wh;
    public static ViewIntroduce create(Activity context){
        return new ViewIntroduce(context);
    }
    public static ViewIntroduce create(View v){
        return new ViewIntroduce(v);
    }
    public PopupWindow.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public ViewIntroduce setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    PopupWindow.OnDismissListener onDismissListener;

    public ViewIntroduce(Activity context) {
        this.context = context;
    }

    public ViewIntroduce(View v) {
        this.context = (Activity) v.getContext();
        setView(v);
    }

    public String getStr() {
        return str;
    }

    public ViewIntroduce setStr(String str) {
        this.str = str;
        return this;
    }

    public Rect getRect() {
        return rect;
    }

    public ViewIntroduce setRect(Rect rect) {
        this.rect = rect;
        return this;
    }
    public ViewIntroduce setRect(View v) {
        Rect rect2=new Rect();
        v.getLocalVisibleRect(rect2);
        this.rect = rect2;
        int[] w=new int[2];
        v.getLocationOnScreen(w);
        rect.offset(w[0],w[1]);
        mLog.array(rect2.left,rect2.top,rect2.right,rect2.bottom);
        return this;
    }
    public ViewIntroduce setView(View v) {
        setRect(v);
        return this;
    }
    public ShapeStyle getShapeStyle() {
        return shapeStyle;
    }

    public ViewIntroduce setShapeStyle(ShapeStyle shapeStyle) {
        this.shapeStyle = shapeStyle;
        return this;
    }
    int r=0;
    public Rect getMaskRect(){
        switch (shapeStyle){
            case Circle:
                r= (int) ( Math.sqrt(rect.width()*rect.width()+rect.height()*rect.height()))/2+padding;
                return new Rect(rect.centerX()-r, rect.centerY()-r,rect.centerX()+r, rect.centerY()+r);
            case Rect:
                return new Rect(rect.left-padding, rect.top-padding, rect.right+padding, rect.bottom+padding);
            case RoundRect:
                return new Rect(rect.left-padding, rect.top-padding, rect.right+padding, rect.bottom+padding);
        }
        return new Rect(rect);
    }
    public void drawMaskRect( Canvas c,Rect rect2,Paint paint){
        switch (shapeStyle){
            case Circle:
                c.drawCircle(rect2.centerX(), rect2.centerY(), r, paint);
                break;
            case Rect:
                c.drawRect(rect2, paint);
               break;
            case RoundRect:
                c.drawRoundRect(new RectF(rect2),5,5,paint);
                break;
        }
    }

    public int padding = 4;
    public PopupWindow show() {
        final PopupWindow popupWindow = new PopupWindow();
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setWidth(-2);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        final ImageView iv = new ImageView(context);
        if(wh==null)wh = ViewUtils.getWindowWH((Activity) context);
        final Bitmap rootbitmap = Bitmap.createBitmap(wh[0], wh[1], Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rootbitmap);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        Rect rect2 =getMaskRect();

        Bitmap mask = Bitmap.createBitmap(wh[0], wh[1], Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(mask);
        drawMaskRect(c,rect2,paint);

        Bitmap bitmap2 = Bitmap.createBitmap(wh[0], wh[1], Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas(bitmap2);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        cc.drawRect(new Rect(0, 0, wh[0], wh[1]), paint);
        int sc = canvas.saveLayer(0, 0, wh[0], wh[1], null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(bitmap2, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        canvas.drawCircle(rect.centerX(), rect.centerY(), (rect.height() + rect.width()) / 2, paint);
        canvas.drawBitmap(mask, 0,0, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);
        drawText(canvas,rect2,str);
        iv.setImageBitmap(rootbitmap);
        iv.setAlpha(alpha);
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
        if (onDismissListener!=null) popupWindow.setOnDismissListener(onDismissListener);
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        return popupWindow;
    }
    private float alpha=0.8f;

    public void drawText(Canvas canvas,Rect mask,String str){
        int width=Math.max(wh[0]/3,mask.width());
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        if(mask.centerX()>wh[0]/2){
            paint.setTextAlign(Paint.Align.RIGHT);
        }else{
            paint.setTextAlign(Paint.Align.LEFT);
        }
        StaticLayout staticLayout2 = new StaticLayout(str,new TextPaint(paint) , width,
                Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        staticLayout2.getHeight();
        canvas.save();
        int x=0,y=0;
        if(mask.centerY()>wh[1]/2)
            y= mask.top-staticLayout2.getHeight()-padding*3;else y=  mask.bottom+padding*3;

        if(mask.centerX()<wh[0]/2)
            x=mask.left;else x=mask.right+padding*3;
            canvas.translate(x, y);
        staticLayout2.draw(canvas);
        canvas.restore();
    }

    public float getAlpha() {
        return alpha;
    }

    public ViewIntroduce setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }


    public enum ShapeStyle {
        Circle, Rect, RoundRect
    }


}
