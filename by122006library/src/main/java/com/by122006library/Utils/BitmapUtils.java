package com.by122006library.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.MyException;
import com.by122006library.R;
import com.by122006library.View.mToast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2017/2/13.
 */

public class BitmapUtils {
    public static HashMap<String, Bitmap> bmpmap = new HashMap<String, Bitmap>();
    private static ArrayList<String> nowwebgettinglist = new ArrayList<String>();


    public static void recycleAll(){
        for(String name:bmpmap.keySet()){
            Bitmap bitmap=bmpmap.get(name);
            if (bitmap!=null&&!bitmap.isRecycled()) bitmap.recycle();
        }
        bmpmap.clear();
    }

    /**
     * 获得图片资源，如果本地无缓存自动网络获取
     *
     * @param scr
     * @return
     */
    public static Bitmap getBitmap(Context context, String scr) {
        return getBitmap(context, scr, true);
    }

    // TODO
    public static synchronized Bitmap getBitmap(final Context context,
                                                final String scr, final boolean inPurgeable) {
        if (scr == null || scr.length() == 0)
            return null;

        Bitmap bitmap = bmpmap.get(scr);
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap;
        }

        try {
            AssetManager manager = BaseActivity.getTopActivity().getAssets();
            InputStream open = manager.open(scr);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = inPurgeable;
            opt.inInputShareable = inPurgeable;
            bitmap = BitmapFactory.decodeStream(open, null, opt);
            if (bitmap == null)
                Log.i("", "bitmap " + scr + " is null");
        } catch (Exception e) {
            Log.e("", "bitmap " + scr + " is error:" + e.getMessage());
        }
        if (bitmap == null && scr.startsWith("http")) {
            if (nowwebgettinglist.indexOf(scr) == -1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        nowwebgettinglist.add(scr);
                        Bitmap tmpBitmap = null;
                        try {
                            InputStream is = new java.net.URL(scr).openStream();
                            tmpBitmap = BitmapFactory.decodeStream(is);
                            is.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i("下载图片", e.getMessage());
                        }
                        bmpmap.put(scr, tmpBitmap);
                        nowwebgettinglist.remove(scr);
                    }
                }).start();

            }
            bitmap = getBitmap(context, R.drawable.nopicture,0);
            return bitmap;
        }
        bmpmap.put(scr, bitmap);
        System.gc();
        return bitmap;
    }

    /**
     * 获取图片
     *
     * @param context
     * @param rid
     * @param inSampleSize 这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰
     * @return
     */
    public static Bitmap getBitmap(Context context, int rid, int inSampleSize) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = inSampleSize; // 这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                rid, opts);
        return bitmap;
    }
    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        return newbm;
    }
    /**
     * @param bitmap
     * @param alpha  (0-透明~255-不透明)
     * @return
     */
    public static Bitmap setBitmapAlpha(Bitmap bitmap, int alpha) {
        Paint paint = new Paint();
        paint.setAlpha(alpha);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmap;
    }
    /**
     * 把一个View的对象转换成bitmap
     */
    public static Bitmap getViewBitmap(View v) {

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

    /**
     * 获得圆角图片的方法
     *
     * @param bitmap
     * @param roundPx 一般设成14
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    // 从sd卡上加载图片
    public static Bitmap decodeSampledBitmapFromFd(String pathName,
                                                   int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return zoomImg(src, reqWidth, reqHeight);
    }

    // 从sd卡上加载图片(尺寸相近)
    public static Bitmap decodeSampledBitmapFromFd_Like(String pathName,
                                                        int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return src;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    public static Bitmap toGrayImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap grayImg = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayImg);

        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return grayImg;

    }

    /**
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap  原始图片
     * @param degrees 原始图片的角度
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmp;
    }

    /**
     * 智能旋转图片，使图片保持正确的方向和尺寸大小。<p>
     *     该方法可以同时旋转画布
     *
     * @param bitmap  原始图片
     * @param degrees 原始图片的角度（0,90,180,270）
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmapSmart(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        if (degrees == 180) {
            return rotateBitmap(bitmap, 180);
        }
        if (degrees == 90||degrees==270) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(- bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
            matrix.postRotate(degrees, 0,0);
            matrix.postTranslate( bitmap.getHeight() / 2, bitmap.getWidth() / 2);
            Bitmap bmp = Bitmap.createBitmap( bitmap.getHeight(),bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            Canvas c=new Canvas(bmp);
            c.drawBitmap(bitmap,matrix,new Paint());
            return bmp;
        }

        return bitmap;
    }
    public static Bitmap getCircleBitmap(Bitmap bitmap){

        //前面同上，绘制图像分别需要bitmap，canvas，paint对象
        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        Bitmap bm = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //这里需要先画出一个圆
        canvas.drawCircle(100, 100, 100, paint);
        //圆画好之后将画笔重置一下
        paint.reset();
        //设置图像合成模式，该模式为只在源图像和目标图像相交的地方绘制源图像
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bm;
    }

    public static final int REQUEST_CODE_PICK_IMAGE = 357;
    public static final int REQUEST_CODE_CAPTURE_CAMERA = 951;

    /**
     * 相册获取照片
     */
    public static void getImageFromAlbum(BaseActivity.ActivityResultCallBack callback) throws MyException {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        BaseActivity.getTopActivity().startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        BaseActivity.getTopBaseActivity().registerActivityResultCallBack(callback);
    }

    /**
     * 从相机获取图片
     */
    public static void getImageFromCamera(Activity activity,BaseActivity.ActivityResultCallBack callback) throws MyException {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            BaseActivity.getTopActivity().startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMERA);
            BaseActivity.getTopBaseActivity().registerActivityResultCallBack(callback);
        } else {
            mToast.getInstance(activity).show("请确认已经插入SD卡");
        }
    }




}
