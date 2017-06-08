package com.by122006library.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
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
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.by122006.annotation.Attribute;
import com.by122006.annotation.Subclass;
import com.by122006library.Function.SubclassAttribute;
import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Functions.SmartRun;
import com.by122006library.Functions.mLog;
import com.by122006library.Interface.NoConfusion_All;
import com.by122006library.Interface.SpecialMethod;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;
import com.by122006library.R;
import com.by122006library.Utils.*;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 122006 on 2016/12/8.
 */
@Subclass(att = {@Attribute(name = "FLAG_ACT_FULLSCREEN", type = boolean.class, defaultValue = "false"),
        @Attribute(name = "FLAG_ACT_NO_TITLE", type = boolean.class, defaultValue = "true")})
public abstract class BaseActivity extends Activity implements NoConfusion_All{
    public static ArrayList<BaseActivity> list_act = new ArrayList<BaseActivity>();
    public static HashMap<String, CustomView> customViewHashMap = new HashMap<>();
    private static ArrayList<Activity> act_out_list;
    public boolean canRotate;
    /*
     * 程序的已运行次数<p>
     * 为0时首次运行
     */
    protected int runcount = -1;
    ArrayList<ActivityResultCallBack> activityResultCallBackList;
    /**
     * 存放代码化属性所依赖的实体对象
     */
    HashMap<String, Object> xmlAttDataHashMap = new HashMap<>();
    private boolean useBindingContentView = false;
    private OrientationEventListener mScreenOrientationEventListener;
    private ArrayList<View> needTouchView = new ArrayList<>();
    private Field bindingField;

    public static Context getContext() throws MyException {
        Activity baseActivity = getTopActivity();
        return baseActivity;
    }

    public static Context optContext() {
        try {
            Activity baseActivity = getTopActivity();
            return baseActivity;
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
            popup.showAtLocation(BaseActivity.getTopBaseActivity().getDecorView(), Gravity.LEFT | Gravity.TOP,
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

    public static Activity getTopActivity() throws MyException {
        BaseActivity act = null;
        try {
            act = getTopBaseActivity();
        } catch (MyException e) {
        }
        if (act == null) return getTopOutActivity();
        else
            return act;
    }

    /**
     * @return
     * @throws MyException
     */
    @SpecialMethod
    public static BaseActivity getTopBaseActivity() throws MyException {
        BaseActivity act = null;
        try {
            act = list_act.get(list_act.size() - 1);
        } catch (Exception e) {
            throw new MyException("没有有效的活动窗口");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (act.isDestroyed()) throw new MyException("顶层窗口不在活动周期");
        }
        return act;
    }

    public static Activity getTopOutActivity() throws MyException {
        Activity act = null;
        try {
            act = act_out_list.get(act_out_list.size() - 1);
        } catch (Exception e) {
            throw new MyException("没有有效的活动窗口");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (act.isDestroyed()) throw new MyException("顶层窗口不在活动周期");
        }
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

    /**
     * 将本框架绑定到原有框架上，可以使用部分功能<p> 请在原有Application上对该方法进行调用
     *
     * @param application
     */
    public static void bindActList(Application application) {
        act_out_list = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                    act_out_list.add(activity);
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    act_out_list.remove(activity);
                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        } else {
            mLog.e("无法兼容Api11以下版本");
        }
    }

    public static void addCustomView(String name, CustomView customView) {
        if (customViewHashMap.containsKey(name)) {
            mLog.w(String.format("自定义View替换规则冲突：%s->%s 被覆盖为 %s->%s", customViewHashMap.get(name).aClass.getName(),
                    name, customView.aClass.getName(), name));
        }
        customViewHashMap.put(name, customView);
    }

    public static void addCustomView(CustomView customView) {
        addCustomView(customView.aClass.getSuperclass().getSimpleName(), customView);
    }

    /**
     * 初始化默认的自定义View替换规则集
     */
    public static void initCustomView() {
//        addCustomView("TextView", new CustomView(AutofitTextView.class) {
//            @Override
//            View onCreate(View parent, String name, Context context, AttributeSet attrs) {
//                AutofitTextView tv = new AutofitTextView(context, attrs);
//                return tv;
//            }
//        });

    }

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        Picasso.with(view.getContext()).load(url).into(view);
    }

    @BindingAdapter("android:text")
    public static void setTvObjectValue(TextView view, Object i) {
//        mLog.i(i.toString());
//        if (i instanceof String) return;
        view.setText(i.toString());
    }

    /**
     * 系统Activity注入方法，你需要在Application里注册，注册后可以获得类似于registerActivityLifecycleCallbacks
     *
     * @throws Throwable
     */
    public static void hookActivityManagerService() throws Throwable {
        Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
        //4.0以后，ActivityManagerNative有gDefault单例来进行保存，这个代码中一看就知道了
        Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
        gDefaultField.setAccessible(true);
        Object gDefault = gDefaultField.get(null);

        Class<?> singleton = Class.forName("android.util.Singleton");
        //mInstance其实就是真正的一个对象
        Field mInstance = singleton.getDeclaredField("mInstance");
        mInstance.setAccessible(true);

        //真正的对象，就是干活的对象啦,其实就是ActivityManagerProxy而已啦
        Object originalIActivityManager = mInstance.get(gDefault);

        //通过动态代理生成一个接口的对象
        Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
        Object object = Proxy.newProxyInstance(iActivityManagerInterface.getClassLoader(),
                new Class[]{iActivityManagerInterface}, new IActivityManagerServiceHandler(originalIActivityManager));
        //这里偷梁换柱，替换为我们自己的对象进行干活就好了
        mInstance.set(gDefault, object);
        mLog.i("By122006Library注入主程序成功");
    }

    public void setFullScreen(boolean FLAG_ACT_FULLSCREEN) {
        SubclassAttribute.with(this).setFLAG_ACT_FULLSCREEN(FLAG_ACT_FULLSCREEN);
    }

    public void setNoTitle(boolean FLAG_ACT_NO_TITLE) {
        SubclassAttribute.with(this).setFLAG_ACT_NO_TITLE(FLAG_ACT_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        runcount++;
        list_act.add(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if ( SubclassAttribute.with(this).getFLAG_ACT_FULLSCREEN())
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new MyLayoutFactory());
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

    }

    public void setRightButton(String text, View.OnClickListener onClickListener) {
        if (SubclassAttribute.with(this).getFLAG_ACT_NO_TITLE()) return;
        ((TextView) findViewById(R.id.rightbutton)).setText(text);
        ((TextView) findViewById(R.id.rightbutton)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.rightbutton)).setOnClickListener(onClickListener);
    }

    public void setTitle(CharSequence title) {
        if ( SubclassAttribute.with(this).getFLAG_ACT_NO_TITLE()) return;
        ((TextView) findViewById(R.id.title)).setText(title);
    }

    @Override
    public void setContentView(@LayoutRes int layoutres) {
        mLog.i(ifHaveBinding() + "");
        if (ifHaveBinding()) {
            if (useBindingContentView) {
                if (!SubclassAttribute.with(this).getFLAG_ACT_NO_TITLE()) {
                    super.setContentView(R.layout.activity_base);
                    findViewById(R.id.titlebar).setVisibility(View.VISIBLE);
                    ((ViewGroup) findViewById(R.id.content)).addView(getLayoutInflater().inflate(layoutres, null));
                } else {
                    super.setContentView(layoutres);
                }
            } else {
                mLog.i("set a DataBinding ContentView in window");
                DataBinding_SetContentView(layoutres);
            }

        } else {
            if (!SubclassAttribute.with(this).getFLAG_ACT_NO_TITLE()) {
                super.setContentView(R.layout.activity_base);
                findViewById(R.id.titlebar).setVisibility(View.VISIBLE);
                ((ViewGroup) findViewById(R.id.content)).addView(getLayoutInflater().inflate(layoutres, null));
            } else {
                super.setContentView(layoutres);
            }
        }
        mLog.i("setContentView()完成");


    }

    /**
     * 封装super.setContentView(layout);以提供其他BaseActivity的继承覆盖
     */
    protected void DataBinding_SetContentView(View layout) {
        if (ifHaveBinding()) mLog.e("错误：DataBinding不支持通过View实体进行setContentView()操作");
        else
            super.setContentView(layout);
    }

    /**
     * 封装super.setContentView(layout);以提供其他BaseActivity的继承覆盖
     */
    protected void DataBinding_SetContentView(@LayoutRes int layoutres) {
        if (ifHaveBinding()) {
            useBindingContentView = true;
            ViewDataBinding bind = DataBindingUtil.setContentView(this, layoutres);
            try {
                getBindingField().set(this, bind);
            } catch (NoSuchFieldException e) {
                mLog.e("DataBindingBaseActivity中必须定义变量 xml");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else
            super.setContentView(layoutres);
    }

    @Override
    public void setContentView(View layout) {
        if (!SubclassAttribute.with(this).getFLAG_ACT_NO_TITLE())
            ((ViewGroup) findViewById(R.id.content)).addView(layout);
        else DataBinding_SetContentView(layout);
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
     * 将在控件构建之后于主线程运行<p> 运行于onAttachedToWindow()中，onResume()后<p> 可以正确获得控件params
     */
    public void onUpdateUi() {
//        if (SmartRun.sPrepare(this)) return;
        if (!getDecorView().isShown()) {
            mLog.w("Stop the onUpdateUi() action : isShow() = false");
            return;
        }

    }

    public Activity getAct() {
        return this;
    }

    ;

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

    ;

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

    public TextView findTextView(@IdRes int id) {
        return (TextView) findViewById(id);
    }

    @UIThread
    public TextView setTextView(@IdRes int id, String str) {
        if (SmartRun.sPrepare(this, id, str)) return null;
        TextView tv = findTextView(id);
        tv.setText(str);
        return tv;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return null;
    }

    /**
     * 修改即将被显示的View属性
     *
     * @param thisView   即将显示的view
     * @param parentView 父view
     * @param orlName    View的原始类名
     * @param context    上下文
     * @param attrs      属性
     */
    @CallSuper
    public void specialView(View thisView, View parentView, String orlName, Context context, AttributeSet attrs) {
        if (orlName.equals("TextView")) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                String key = attrs.getAttributeName(i);
                String value = attrs.getAttributeValue(i);

            }
        }


    }

    /**
     * 增加代码化属性所依赖的实体对象，引用名为该类Class简写名
     */
    public void addXmlAttData(@Nullable Object xmlAttData) {
        xmlAttDataHashMap.put(xmlAttData.getClass().getSimpleName(), xmlAttData);
    }

    /**
     * 增加代码化属性所依赖的实体对象，自定义名
     */
    protected void addXmlAttData(String name, @Nullable Object xmlAttData) {
        xmlAttDataHashMap.put(name, xmlAttData);
    }

    protected ViewDataBinding getBinding() throws MyException {
        try {
            return (ViewDataBinding) getBindingField().get(this);
        } catch (Exception e) {
            throw new MyException("你必须在Activity里声明一个ViewDataBinding子类变量");
        }
    }

    protected boolean ifHaveBinding() {
        try {
            return getBindingField() != null;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected ViewDataBinding optBinding() {
        try {
            return (ViewDataBinding) getBindingField().get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Field getBindingField() throws NoSuchFieldException {
        if (bindingField != null) return bindingField;
        for (Field field : ReflectionUtils.getFieldArray(this)) {
            if (ViewDataBinding.class.isAssignableFrom(field.getType())) {
                bindingField = field;
                return field;
            }

        }
        return null;
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

    public abstract static class CustomView {
        Class<? extends View> aClass;

        CustomView(Class<? extends View> aClass) {
            this.aClass = aClass;
        }

        abstract View onCreate(View parent, String name, Context context, AttributeSet attrs);
    }

    public static class IActivityManagerServiceHandler implements InvocationHandler {

        private Object base;

        public IActivityManagerServiceHandler(Object base) {
            this.base = base;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //判断是不是activityResumed,如果是的话，那么拦截参数，然后反射获取实例就好
            if (method.getName().equals("activityResumed")) {
                //这里拿到想要的IBinder啦，就是token
                IBinder iBinder = (IBinder) args[0];
                Class<?> clazz = Class.forName("android.app.ActivityThread");
                Method method1 = clazz.getDeclaredMethod("currentActivityThread");
                Object object = method1.invoke(null);
                Method getActivity = clazz.getDeclaredMethod("getActivity", IBinder.class);
                Activity mActivity = (Activity) getActivity.invoke(object, iBinder);
            }
            return method.invoke(base, args);
        }
    }

    private class MyLayoutFactory implements LayoutInflaterFactory {
        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            View v = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                v = getAct().onCreateView(parent, name, context, attrs);
            }

            if (v == null) {
                CustomView customView = customViewHashMap.get(name);
                if (customView != null) v = customView.onCreate(parent, name, context, attrs);
            }
            specialView(v, parent, name, context, attrs);
            return v;
        }
    }


}
