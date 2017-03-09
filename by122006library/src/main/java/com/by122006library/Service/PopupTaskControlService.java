package com.by122006library.Service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.MyException;
import com.by122006library.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2017/2/27.
 */

public class PopupTaskControlService extends Service {
    public static WindowManager.LayoutParams wmParams;
    public static ArrayList<HashMap<String, Object>> task = new ArrayList<HashMap<String, Object>>();
    public static int wmParamsx = 100;
    public static int wmParamsy = 200;
    private static boolean running = false;
    private static WindowManager mWindowManager;
    private static LinearLayout mFloatLayoutfx;
    LinearLayout list;

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        mWindowManager.removeView(mFloatLayoutfx);
    }

    public void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(
                Context.WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = wmParamsx;
        wmParams.y = wmParamsy;
        wmParams.alpha = 1.0f;

        wmParams.width = 600;
        wmParams.height =200;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayoutfx = (LinearLayout) inflater.inflate(
                R.layout.popuptaskcontrollayout, null);
        (mFloatLayoutfx.findViewById(R.id.zhangua)).setClickable(true);
        (mFloatLayoutfx.findViewById(R.id.zhangua)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    zhanGua();
                } catch (MyException e) {
                    e.printStackTrace();
                }
            }
        });
        list = (LinearLayout) mFloatLayoutfx.findViewById(R.id.list);
        try {
            mWindowManager.addView(mFloatLayoutfx, wmParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 浮动窗口按钮
        // mFloatView = (Button)mFloatLayout.findViewById(R.id.float_id);

        mFloatLayoutfx.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    private void zhanGua() throws MyException {
        if (task == null) task = new ArrayList<>();
        if (BaseActivity.list_act.size() == 0) {
            Toast.makeText(getApplicationContext(), "你必须在软件中才能使用此功能！", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseActivity topActivity = null;
        try {
            topActivity = BaseActivity.getTopActivity();
        } catch (MyException e) {
            Toast.makeText(getApplicationContext(), "获取主界面失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (BaseActivity.list_act.get(0).getTaskId()==topActivity.getTaskId()) {
            Toast.makeText(topActivity, "根目录栈不可暂挂！", Toast.LENGTH_SHORT).show();
        }
        addTask(topActivity);
        topActivity.moveTaskToBack(true);

    }

    private void addTask(final BaseActivity topActivity) throws MyException {
        for (HashMap hashMap : (ArrayList<HashMap>) task.clone()) {
            if (hashMap.get("obj").equals(topActivity)) {
                hashMap.put("bmp", topActivity.getViewBitmapById(-1));
                Toast.makeText(getApplicationContext(), "界面已更新", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        final HashMap map = new HashMap<String, Object>();
        map.put("obj", topActivity);
        Bitmap bitmap = topActivity.getViewBitmapById(-1);
        map.put("bmp", bitmap);
        map.put("taskid", topActivity.getTaskId());
        task.add(map);
        ImageView iv = new ImageView(this);
        int r = mFloatLayoutfx.findViewById(R.id.zhangua).getHeight();
        iv.setLayoutParams(new LinearLayout.LayoutParams(r, r));
        iv.setImageBitmap(bitmap);
        map.put("view", iv);
        iv.setLongClickable(true);
        iv.setClickable(true);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                mAm.moveTaskToFront((Integer) map.get("taskid"), 0);
            }
        });
        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeTask(v);
                return true;
            }
        });

        list.addView(iv);
    }

    public void removeTask(View v){
        for (HashMap hashMap : (ArrayList<HashMap>) task.clone()) {
            if (hashMap.get("view").equals(v)) {
                list.removeView((View) hashMap.get("view"));
                ((Bitmap)hashMap.get("bmp")).recycle();
                int taskid= (int) hashMap.get("taskid");
                for(Activity activity:BaseActivity.list_act){
                    if(activity.getTaskId()==taskid){
                        activity.finish();
                    }
                }
                return;
            }
        }
    }


}
