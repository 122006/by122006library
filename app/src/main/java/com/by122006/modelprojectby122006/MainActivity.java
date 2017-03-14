package com.by122006.modelprojectby122006;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.Interface.BGThread;
import com.by122006library.Interface.DefaultThread;
import com.by122006library.Interface.ThreadStyle;
import com.by122006library.Interface.UIThread;
import com.by122006library.Utils.DebugUtils;
import com.by122006library.Utils.SmartRun;
import com.by122006library.Utils.ThreadUtils;
import com.by122006library.Utils.ViewUtils;
import com.by122006library.Utils.mLog;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout=new LinearLayout(this);
        layout.setGravity(Gravity.CENTER);
        TextView tv=new TextView(this);
        tv.setText("TestActivity");
        layout.addView(tv);
        final TextView tv2=new TextView(this);
        tv2.setText(getTaskId()+"");
        layout.addView(tv2);
        tv.setClickable(true);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                Intent intent=new Intent(MainActivity.this,TestActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                startActivity(intent);
                new SmartRun() {
                    @Override
                    @UIThread
                    public void action() {
                        ViewUtils.introduceView(v, "这个是展示的控件  "+ThreadUtils.getThreadStytle().toString(), true, new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                dismiss();
                            }
                        });


                    }

                    @UIThread
                    public void dismiss(){
                        if(prepare()) return;
                        ViewUtils.introduceView(tv2, "这个是展示的控件2  "+ThreadUtils.getThreadStytle().toString(), true,  new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                dismiss("这是传递的数据");
                            }
                        });
                    }
                    @UIThread
                    public void dismiss(String a){
                        if(prepare(a)) return;
                        tv2.setText("即将调用测试");
                        test();
                    }
                    @DefaultThread
                    public void test(){
                        if(prepare()) return;
                        ViewUtils.introduceView(v, "测试下异步方法  "+ThreadUtils.getThreadStytle().toString(), true,  new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                dismiss();
                            }
                        });
                        ViewUtils.introduceView(v, "异步方法  "+ThreadUtils.getThreadStytle().toString(), false,null);
                    }
                }.start();







            }
        });
        setContentView(layout);
//        startService(new Intent(this, PopupTaskControlService.class));



    }

}
