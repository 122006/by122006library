package com.by122006.modelprojectby122006;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.by122006.modelprojectby122006.databinding.ActivityMainBinding;
import com.by122006library.Activity.BaseActivity;
import com.by122006library.Functions.mLog;
import com.by122006library.Utils.ViewUtils;

/**
 * by122006<p> </>
 */
public class MainActivity extends BaseActivity {
    static public Class<? extends FunctionActivity>[] aClass = new Class[]{MainActivity.class};
    boolean FLAG_ACT_FULLSCREEN = true;
    boolean FLAG_ACT_NO_TITLE = true;
//    ActivityMainBinding binding = null;
    String title = "by122006库 Test";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        binding.setAct(this);
//        binding.recycer.setLayoutManager(new LinearLayoutManager(this));
//        binding.recycer.setAdapter(new MyAdapter());
//        binding.recycer.setItemAnimator(new DefaultItemAnimator());
        LinearLayout layout=new LinearLayout(this);
        final TextView textView=new TextView(this);
        textView.setTextColor(Color.BLUE);
        textView.setText("显示的第一个TextView");
        textView.setTextSize(20);
        final TextView textView2=new TextView(this);
        textView2.setText("渐隐覆盖替换对象");
        textView2.setTextColor(Color.RED);
        textView2.setTextSize(20);
        layout.addView(textView);
        setContentView(layout);
        layout.post(new Runnable() {
            @Override
            public void run() {
                ViewUtils.smoothReplace(textView,textView2,200);
            }
        });
        layout.setClickable(true);
        layout.setBackgroundColor(0xff114332);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLog.i("textView2.getParent()="+textView2.getParent());
                mLog.i("textView1.getParent()="+textView.getParent());
                ViewUtils.smoothReplace_Smart(textView,textView2,200);
            }
        });

    }

    class MyAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder=null;
            switch (viewType){
                case 0:
                    View view= ViewUtils.getLayout(optContext(),R.layout.activity_main);
                    return new TextVH(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return aClass.length;
        }

        class TextVH extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView title;

            public TextVH(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
            }

        }

        class BitmapVH extends TextVH {
            ImageView iv;

            public BitmapVH(View view) {
                super(view);
                iv = (ImageView) view.findViewById(R.id.imageView);
            }

        }
    }

}


