package com.by122006library.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.UiThread;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.by122006library.MyException;
import com.by122006library.R;
import com.by122006library.Utils.CycleTask;
import com.by122006library.Utils.ThreadUtils;
import com.by122006library.Utils.ViewUtils;
import com.by122006library.Utils.mLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/8.
 */

public class BaseActivity extends Activity {
    public static ArrayList<BaseActivity> list_act = new ArrayList<BaseActivity>();
    public boolean canRotate;
    protected boolean FLAG_ACT_NO_TITLE = true;
    protected boolean FLAG_ACT_FULLSCREEN = true;
    /*
     * 程序的已运行次数<p>
     * 为0时首次运行
     */
    protected int runcount = -1;
    ArrayList<ActivityResultCallBack> activityResultCallBackList;
    private OrientationEventListener mScreenOrientationEventListener;
    private ArrayList<View> needTouchView = new ArrayList<>();

    public static Context getContext() throws MyException {
        return getTopActivity().getDecorView().getContext();
    }

    public static Context optContext() {
        try {
            return getTopActivity().getDecorView().getContext();
        } catch (MyException e) {
            return null;
        }
    }

    public static void showStringPopup_BottomView(String str, View v) throws MyException {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        location[1] += v.getHeight();
        showStringPopup(str, location[0], location[1]);
    }

    public static void showStringPopup(String str) throws MyException {
        showStringPopup(str, 0, 0);
    }

    public static void showStringPopup(String str, int x, int y) throws MyException {
        Context context = getContext();
        LinearLayout popuplayout = new LinearLayout(context);
        popuplayout.setBackgroundColor(0xffdd8c00);
        TextView tv = new TextView(context);
        tv.setText(str);
        tv.setPadding(5, 5, 5, 5);
        tv.setTextColor(Color.WHITE);
        popuplayout.addView(tv);
        PopupWindow popup = new PopupWindow();
        popup.setOutsideTouchable(true);
        popup.setContentView(popuplayout);
        popup.update();
        popup.setWidth(-2);
        popup.setHeight(-2);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.setFocusable(false);
        popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        try {
            popup.showAtLocation(BaseActivity.getTopActivity().getDecorView(), Gravity.LEFT | Gravity.TOP,
                    x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setClickPopupString(final View v,
                                           final String str) {
        v.setClickable(str != null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    showStringPopup_BottomView(str, v);
                } catch (MyException e) {
                    MyException.show(e);
                }
            }
        });

    }

    public static BaseActivity getTopActivity() throws MyException {
        BaseActivity act = null;
        try {
            act = list_act.get(list_act.size() - 1);
        } catch (Exception e) {
            throw new MyException("没有有效的活动窗口");
        }
        if (act.isDestroyed()) throw new MyException("顶层窗口不在活动周期");
        return act;
    }

    /**
     * 判断当前App处于前台还是后台状态
     */
    public static boolean isApplicationBackground() throws MyException {
        ActivityManager am = (ActivityManager) getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(getTopActivity().getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前手机是否处于锁屏(睡眠)状态
     */
    public static boolean isSleeping() throws MyException {
        KeyguardManager kgMgr = (KeyguardManager) getContext()
                .getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
        return isSleeping;
    }

    /**
     * 判断当前是否有网络连接
     *
     * @return
     */
    public static boolean isOnline() throws MyException {
        ConnectivityManager manager = (ConnectivityManager) getContext()
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前是否是WIFI连接状态
     */
    public static boolean isWifiConnected() throws MyException {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 是否有SD卡
     */
    public static boolean haveSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 动态隐藏软键盘
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideSoftInput(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideSoftInput(Context context, EditText edit) {
        edit.clearFocus();
        InputMethodManager inputmanger = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    /**
     * 动态显示软键盘
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void showSoftInput(Context context, EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(edit, 0);
    }

    /**
     * 主动回到Home，后台运行
     */
    public static void goHome() throws MyException {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        getContext().startActivity(mHomeIntent);
    }

    public void setFullScreen(boolean FLAG_ACT_FULLSCREEN) {
        this.FLAG_ACT_FULLSCREEN = FLAG_ACT_FULLSCREEN;
    }

    public void setNoTitle(boolean FLAG_ACT_NO_TITLE) {
        this.FLAG_ACT_NO_TITLE = FLAG_ACT_NO_TITLE;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        runcount++;
        list_act.add(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (FLAG_ACT_FULLSCREEN) getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        if (canRotate) {
            mScreenOrientationEventListener = new OrientationEventListener(this) {
                private int mScreenExifOrientation;

                @Override
                public void onOrientationChanged(int i) {
                    // i的范围是0～359
                    // 屏幕左边在顶部的时候 i = 90;
                    // 屏幕顶部在底部的时候 i = 180;
                    // 屏幕右边在底部的时候 i = 270;
                    // 正常情况默认i = 0;

                    if (45 <= i && i < 135) {
                        mScreenExifOrientation = ExifInterface.ORIENTATION_ROTATE_180;
                    } else if (135 <= i && i < 225) {
                        mScreenExifOrientation = ExifInterface.ORIENTATION_ROTATE_270;
                    } else if (225 <= i && i < 315) {
                        mScreenExifOrientation = ExifInterface.ORIENTATION_NORMAL;
                    } else {
                        mScreenExifOrientation = ExifInterface.ORIENTATION_ROTATE_90;
                    }
                    if (canRotate) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    } else {
                        switch (mScreenExifOrientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                                break;
                            default:
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                break;
                        }
                    }
                }
            };
            mScreenOrientationEventListener.enable();
        }
        if (!FLAG_ACT_NO_TITLE) {super.setContentView(R.layout.activity_base);findViewById(R.id.titlebar).setVisibility(View.VISIBLE);}
    }

    public void setRightButton(String text, View.OnClickListener onClickListener) {
        if (FLAG_ACT_NO_TITLE) return;
        ((TextView) findViewById(R.id.rightbutton)).setText(text);
        ((TextView) findViewById(R.id.rightbutton)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.rightbutton)).setOnClickListener(onClickListener);
    }

    public void setTitle(CharSequence title) {
        if (FLAG_ACT_NO_TITLE) return;
        ((TextView) findViewById(R.id.title)).setText(title);
    }

    @Override
    public void setContentView(@LayoutRes int layoutres) {
        if (!FLAG_ACT_NO_TITLE)
            ((ViewGroup) findViewById(R.id.content)).addView(getLayoutInflater().inflate(layoutres, null));
        else super.setContentView(layoutres);
    }

    @Override
    public void setContentView(View layout) {
        if (!FLAG_ACT_NO_TITLE) ((ViewGroup) findViewById(R.id.content)).addView(layout);
        else super.setContentView(layout);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        onUpdateUi();
    }


    public View getDecorView() {
        return getWindow().getDecorView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        list_act.remove(this);
        needTouchView.clear();
        needTouchView = null;
        CycleTask.unRegister(this);
    }

    /**
     * 预设的退出按钮响应方法<p> 控件onclick为back;clickable为true
     *
     * @param v 退出按钮
     */
    public void back(View v) {
        finish();
    }

    /**
     * 安全退出程序<p> 会关闭所有activity并杀死进程
     */
    public void exit() {
        for (Activity activity : list_act) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
        Process.killProcess(Process.myPid());
        Paint p = new Paint();
    }

    /**
     * 将在控件构建之后于主线程运行<p> 运行于onAttachedToWindow()中，onResume()后<p> 可以正确测量控件数据，用于控件初始化
     */
    @UiThread
    @CallSuper
    public void onUpdateUi() {
        if (!getDecorView().isShown()) {
            mLog.w("Stop the onUpdateUi() action : isShow() = false");
            return;
        }
        if (!ThreadUtils.isUIThread()) {
            mLog.e("Stop the onUpdateUi() action : is now UI Thread");
            return;
        }

    }

    public Activity getAct() {
        return this;
    }

    /**
     * 获得布局的截图
     *
     * @param resId 布局id
     * @return
     * @throws MyException
     */
    public Bitmap getViewBitmapById(@IdRes int resId) throws MyException {
        return ViewUtils.getViewBitmap(resId == -1 ? findViewById(resId) : getDecorView());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResultCallBackList == null) return;
        for (ActivityResultCallBack callback : (ArrayList<ActivityResultCallBack>) activityResultCallBackList.clone()) {
            if (callback.match(requestCode, resultCode)) {
                callback.callback(data);
                activityResultCallBackList.remove(callback);
            }
        }
    }

    /**
     * 注册界面回传的回调事件
     *
     * @param activityResultCallBack
     */
    public void registerActivityResultCallBack(ActivityResultCallBack activityResultCallBack) {
        if (activityResultCallBackList == null) activityResultCallBackList = new ArrayList<ActivityResultCallBack>();
        activityResultCallBackList.add(activityResultCallBack);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (needTouchView != null) {
            for (View v : needTouchView) {
                Rect rect = new Rect();
                v.getLocalVisibleRect(rect);
                if (v.isShown() && rect.contains((int) ev.getRawX(), (int) ev.getRawY())) v.onTouchEvent(ev);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    public void registerNeedTouchView(View v) {
        if (needTouchView.contains(v)) return;
        needTouchView.add(v);
    }

    /**
     * 界面回传的回调事件
     */
    public interface ActivityResultCallBack {
        /**
         * 返回是否匹配
         *
         * @param requestCode
         * @param resultCode
         * @return
         */
        boolean match(int requestCode, int resultCode);

        void callback(Intent data);
    }
}
