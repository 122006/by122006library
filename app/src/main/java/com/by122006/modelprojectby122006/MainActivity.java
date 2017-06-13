package com.by122006.modelprojectby122006;


import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.by122006.modelprojectby122006.databinding.ActivityMainBinding;
import com.by122006library.Activity.BaseActivity;
import com.by122006library.Functions.mLog;
import com.by122006library.Utils.ReflectionUtils;
import com.by122006library.Utils.ViewUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * by122006<p> </>
 */
public class MainActivity extends BaseActivity {
    static public Class<?>[] aClass = new Class[]{ViewsReplace.class};
    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = true;
    ActivityMainBinding binding = null;

    public String name = "by122006库 Test";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding.setAct(this);
        binding.recycer.setLayoutManager(new LinearLayoutManager(this));
        binding.recycer.setAdapter(new MyAdapter());
        binding.recycer.setItemAnimator(new DefaultItemAnimator());


    }

    class MyAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder=null;
            switch (viewType){
                case 0:
                    View view= ViewUtils.getLayout(optContext(),R.layout.item_main_activity_list_newactivity_style);
                    holder=new NewActivityVH(view);
                    return holder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Class clazz=aClass[position];
            try {
                final FunctionActivity functionActivity= (FunctionActivity) ReflectionUtils.newInstance(clazz);
                switch (holder.getItemViewType()){
                    case 0:
                        mLog.i(functionActivity.getClass().getName());
                        ((NewActivityVH)holder).title.setText(new FunctionActivity_Attribute(functionActivity).getTitle());
                        ((NewActivityVH)holder).title.setTextColor(new FunctionActivity_Attribute(functionActivity).getBgColor());
                        ((NewActivityVH)holder).title.setClickable(true);
                        ((NewActivityVH)holder).title.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                functionActivity.startThisActivity(MainActivity.this);
                            }
                        });
                        break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return;
        }

        @Override
        public int getItemViewType(int position) {
            if(aClass[position].isAssignableFrom(FunctionActivity.class))    return 0;
            if(aClass[position].isAssignableFrom(View.class))    return 1;
            if(aClass[position].isAssignableFrom(FunctionActivity.class))    return 0;
            return -1;
        }

        @Override
        public int getItemCount() {
            return aClass.length;
        }

        class NewActivityVH extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView title;

            public NewActivityVH(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
            }

        }

        class BitmapVH extends NewActivityVH {
            ImageView iv;
//            View v;

            public BitmapVH(View view) {
                super(view);
                iv = (ImageView) view.findViewById(R.id.imageView);
            }

        }
    }

}


