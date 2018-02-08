package com.by122006.modelprojectby122006;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.by122006.modelprojectby122006.databinding.ActivitySvBinding;
import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Functions.SmartRun;
import com.by122006library.Functions.mLog;
import com.by122006library.Interface.NoProguard_All;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;
import com.by122006library.Utils.ReflectionUtils;
import com.by122006library.Utils.ViewUtils;
import com.by122006library.View.PullDownTopbarMenu;
import com.by122006library.View.SlideSpinner;
import com.by122006library.View.mToast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fynn.fluidlayout.FluidLayout;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by admin on 2017/6/20.
 */

public abstract class SV extends FunctionActivity implements NoProguard_All {
    public final static int Mark_Color = 0xff5677fc;
    protected static long durObserverTime = 100;
    public ActivitySvBinding binding;
    ArrayList<View> views = new ArrayList<>();
    View nowV;
    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;
    ArrayList<View> marks = new ArrayList<>();
    ArrayList<SMethod> methods;
    LinearLayout layout_observer;
    ArrayList<DataObserver> observers = new ArrayList<>();
    private PullDownTopbarMenu pullDownTopbarMenu;

    public void countViews(ArrayList<View> views) {
        ViewUtils.countViews(views, getViewGroup());
    }
    public void showViewAtt(){
        ViewGroup group = getViewGroup();
        nowV = putLayoutSpace(group);

        pullDownTopbarMenu = getPullDownTopbarMenu();
        binding.setNowV(nowV);
//        group.removeAllViews();
        countViews(views);
        if (nowV == null) nowV = views.get(0);
        setNowViewData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sv);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.showViewAtt();
        binding.recycer.setLayoutManager(new LinearLayoutManager(this));
        showSpecialMethods();
        binding.name.setLongClickable(true);
        binding.name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String[] strs = new String[views.size()];
                for (int i = 0; i < views.size(); i++) {
                    strs[i] = views.get(i).getClass().getSimpleName() + " " + views.get(i).getId();
                }
                mLog.i(strs);
                SlideSpinner spinner = new SlideSpinner(SV.this).setEveryTime(500).setLinearColor
                        (0xffe65100, 0xfffb8c00).setData(strs).setTextView(binding.name).setItemHeight(binding.name
                        .getHeight())
                        .setUseCardView(false)
                        .setSelectedListener(new SlideSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(SlideSpinner slideSpinner, TextView textView, String[] data,
                                                       String chooseStr, int chooseIndex) {
                                nowV = views.get(chooseIndex);
                                setNowViewData();
                            }

                            @Override
                            public void onNoChoose(SlideSpinner slideSpinner, TextView textView, String[] data) {

                            }
                        });
                spinner.setWidth(-2);
                spinner.showDropDown(view);
                return true;
            }
        });
        getTopBar().setLeftButton("返回", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        functionObject = getFunctionObject();
    }

    public void showSpecialMethods(View v) {
        showSpecialMethods();
    }

    public void showSpecialMethods() {
        methods = specialMethods();
        List<PinnedHeaderEntity<SMethod>> data = new ArrayList<>();
        ArrayList<Class> heads = new ArrayList<>();
        for (SMethod item : methods) {
            Class head = item.object instanceof Class ? (Class) item.object : item.object.getClass();
            String headName = String.format("%s [%s]", head.getSimpleName(), head.getName());
            if (!heads.contains(head)) {
                data.add(new PinnedHeaderEntity<SMethod>(null, BaseHeaderAdapter.TYPE_HEADER, headName));
                heads.add(head);
            }
            data.add(new PinnedHeaderEntity<SMethod>(item, BaseHeaderAdapter.TYPE_DATA, headName));
        }
        showMethods(methods, data);
    }

    public ViewGroup getViewGroup() {
        return binding.space;
    }

    public void changeNowView(View v) {
        countViews(views);
        int index = views.indexOf(nowV);
        if (index == views.size() - 1) index = -1;
        nowV = views.get(index + 1);
        setNowViewData();
    }

    public void markView(View v) {
        if (marks.contains(nowV)) {
            marks.remove(nowV);
        } else
            marks.add(nowV);
        nowV.setBackgroundColor(getMarkStateColor());
        setNowViewData();
    }

    public int getMarkStateColor() {
        return marks.contains(nowV) ? Mark_Color : Color.TRANSPARENT;
    }

    public void clearMarks(View v) {
        for (View vm : marks) {
            marks.remove(vm);
            vm.setBackgroundColor(getMarkStateColor());
        }
        nowV.setBackgroundColor(getMarkStateColor());
        setNowViewData();
    }

    public void setNowViewData() {
        binding.setNowV(nowV);
        binding.name.setText(nowV.getClass().getSimpleName().toString() + " " + nowV.getId());
        setClickPopupString(binding.name, nowV.getClass().getName().toString());
        binding.nowvvisible.setText(getNowViewVisible());
        binding.markNowV.setBackgroundColor(getMarkStateColor());
    }

    public String getNowViewVisible() {
        if (nowV == null) return "-";
        switch (nowV.getVisibility()) {
            case View.VISIBLE:
                return "VISIBLE";
            case View.GONE:
                return "GONE";
            case View.INVISIBLE:
                return "INVISIBLE";
        }
        return "";
    }

    public void changeNowViewVisible(View view) {
        switch (nowV.getVisibility()) {
            case View.VISIBLE:
                nowV.setVisibility(View.GONE);
                break;
            case View.GONE:
                nowV.setVisibility(View.INVISIBLE);
                break;
            case View.INVISIBLE:
                nowV.setVisibility(View.VISIBLE);
                break;
        }
        setNowViewData();
    }

    public void showParent(View view) {
        View viewParent = (View) nowV.getParent();
        if (!views.contains(viewParent)) return;
        nowV = viewParent;
        setNowViewData();
    }

    /**
     * 将需要展示的控件显示于getViewGroup()定义的布局中，如果space子布局数为空，将会将返回的View添加入space中
     *
     * @param space 用于存放控件的ViewGroup，默认为FrameLayout的space布局
     * @return 显示的控件
     */
    public abstract View putLayoutSpace(ViewGroup space);

    public abstract ArrayList<SMethod> specialMethods();

    public void showFields(View v) {
        List<PinnedHeaderEntity<Field>> data = new ArrayList<>();
        ArrayList<Integer> modifiers = new ArrayList<>();
        try {
            Field[] field = ReflectionUtils.getFieldArray(functionObject);
            for (Field item : field) {
                Integer modifier = item.getModifiers();
                if (!modifiers.contains(modifier)) {
                    data.add(new PinnedHeaderEntity<Field>(null, BaseHeaderAdapter.TYPE_HEADER, Modifier.toString
                            (modifier)));
                    modifiers.add(modifier);
                }
                data.add(new PinnedHeaderEntity<Field>(item, BaseHeaderAdapter.TYPE_DATA, Modifier.toString(modifier)));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        FieldsAdapter adapter = new FieldsAdapter(data);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (adapter.getItemViewType(position)) {
                    case BaseHeaderAdapter.TYPE_HEADER:

                        break;
                    case BaseHeaderAdapter.TYPE_DATA:
                        Field field = ((PinnedHeaderEntity<Field>) adapter.getData().get(position)).getData();
                        new FieldObserver(functionObject, field).register();
                        break;
                }
            }
        });
        binding.recycer.addItemDecoration(new PinnedHeaderItemDecoration.Builder(BaseHeaderAdapter.TYPE_HEADER)
                .setHeaderClickListener(null).create());
        binding.recycer.setAdapter(adapter);
    }

    public void showNormalMethods(View v) {
        List<PinnedHeaderEntity<SMethod>> data = new ArrayList<>();
        ArrayList<String> heads = new ArrayList<>();
        try {
            Method[] methodsA = ReflectionUtils.getMethodArray(functionObject);
            methods.clear();
            for (Method method : methodsA) {
                methods.add(SMethod.createByMethod(functionObject, method));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        for (SMethod item : methods) {
            String headName2 = item.method.getName();
            String headName = "";
            for (int i = 0; i < headName2.length(); i++) {
                char c = headName2.charAt(i);
                if (Character.isLowerCase(c)) headName += c;
                if (Character.isUpperCase(c) || c == '_') break;
            }
            headName += "XX()";
            if (!heads.contains(headName)) {
                data.add(new PinnedHeaderEntity<SMethod>(null, BaseHeaderAdapter.TYPE_HEADER, headName));
                heads.add(headName);
            }
            data.add(new PinnedHeaderEntity<SMethod>(item, BaseHeaderAdapter.TYPE_DATA, headName));
        }
        showMethods(methods, data);
    }

    public void showMethods(ArrayList<SMethod> methods, List<PinnedHeaderEntity<SMethod>> data) {

        SpecialMethodsAdapter adapter = new SpecialMethodsAdapter(data);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, final View view, int position) {
                switch (adapter.getItemViewType(position)) {
                    case BaseHeaderAdapter.TYPE_HEADER:

                        break;
                    case BaseHeaderAdapter.TYPE_DATA:
                        SMethod sMethod = ((PinnedHeaderEntity<SMethod>) adapter.getData().get(position)).getData();
                        if (sMethod.getListener() != null) {
                            sMethod.getListener().onClick(view);
                        } else {
                            try {
                                try {
                                    Object re = sMethod.method.invoke(sMethod.object, sMethod
                                            .initParams);
                                    if (sMethod.method.getReturnType() == void.class) break;
                                    TextView tv = (TextView) view.findViewWithTag("result");
                                    if (tv == null) {
                                        tv = new TextView(view.getContext());
                                        tv.setTextColor(0xff5677fc);
                                        tv.setTag("result");
                                        tv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ((ViewGroup) view).removeView(v);
                                            }
                                        });
                                        ((ViewGroup) view).addView(tv);
                                    }
                                    if (re == sMethod.object) tv.setText(" => 链式");
                                    else
                                        tv.setText(" => " + String.valueOf(re));

                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                mToast.getInstance(SV.this).show("你只能调用普通功能里的无参方法，或全部参数为可编辑属性型方法");
                            }
                        }
                        break;
                }
            }
        });
        binding.recycer.addItemDecoration(new PinnedHeaderItemDecoration.Builder(BaseHeaderAdapter.TYPE_HEADER)
                .setHeaderClickListener(null).create());
        binding.recycer.setAdapter(adapter);
    }

    public void saveViewBitmap(View v) {
        Bitmap bitmap;
        try {
            bitmap = ViewUtils.getViewBitmap(v);
        } catch (MyException e) {
            return;
        }
        try {
            Reservoir.put(this.getClass().getSimpleName() + "_VP", bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadFuncitonCachePhoto(final ImageView iv) {
        Reservoir.getAsync(this.getClass().getSimpleName() + "_VP", Bitmap.class, new ReservoirGetCallback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap myObject) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageBitmap(myObject);
            }

            @Override
            public void onFailure(Exception e) {
                iv.setVisibility(View.GONE);
            }
        });
    }


    public PullDownTopbarMenu getPullDownTopbarMenu() {
        layout_observer = new LinearLayout(getAct());
        pullDownTopbarMenu = new PullDownTopbarMenu.Builder(getAct()).setContextLayout
                (layout_observer).setTopView(binding.layoutViewAtt).setBackGround(0xff304ff3).setSliderView(null).build().show();
        layout_observer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                pullDownTopbarMenu.scrollToOpen(300);
            }
        });
        return pullDownTopbarMenu;
    }

    class SpecialMethodsAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<SMethod>> {
        private SparseIntArray mRandomHeights;

        public SpecialMethodsAdapter(List<PinnedHeaderEntity<SMethod>> methods) {
            super(methods);
        }


        @Override
        protected void convert(BaseViewHolder holder, PinnedHeaderEntity<SMethod> item) {
            switch (holder.getItemViewType()) {
                case BaseHeaderAdapter.TYPE_HEADER:
                    holder.setText(R.id.name, item.getPinnedHeaderName().startsWith("TiaoShi") ? "演示程序 调试方法" : item
                            .getPinnedHeaderName());
                    break;
                case BaseHeaderAdapter.TYPE_DATA:

                    int position = holder.getLayoutPosition();

                    if (binding.recycer.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                        // 瀑布流布局记录随机高度，就不会导致Item由于高度变化乱跑，导致画分隔线出现问题
                        // 随机高度, 模拟瀑布效果.

                        if (mRandomHeights == null) {
                            mRandomHeights = new SparseIntArray(getItemCount());
                        }

                        if (mRandomHeights.get(position) == 0) {
                            mRandomHeights.put(position, ViewUtils.dip2px((int) (100 + Math.random() * 100)));
                        }

                        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                        lp.height = mRandomHeights.get(position);
                        holder.itemView.setLayoutParams(lp);

                    }
                    SMethod sMethod = (SMethod) ((PinnedHeaderEntity<SMethod>) getData().get(position)).getData();
                    ;
                    sMethod.setSmallInTextView((FluidLayout) holder.getView(R.id.fluidlayout));
                    break;
            }
        }


        @Override
        protected void addItemTypes() {
            addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_sv_pinnedheader);
            addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_sv_smethod);
        }

    }

    class FieldsAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<Field>> {
        private SparseIntArray mRandomHeights;

        public FieldsAdapter(List<PinnedHeaderEntity<Field>> fields) {
            super(fields);
        }


        @Override
        protected void convert(BaseViewHolder holder, PinnedHeaderEntity<Field> item) {
            switch (holder.getItemViewType()) {
                case BaseHeaderAdapter.TYPE_HEADER:
                    holder.setText(R.id.name, item.getPinnedHeaderName());
                    break;
                case BaseHeaderAdapter.TYPE_DATA:

                    int position = holder.getLayoutPosition();

                    if (binding.recycer.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                        // 瀑布流布局记录随机高度，就不会导致Item由于高度变化乱跑，导致画分隔线出现问题
                        // 随机高度, 模拟瀑布效果.

                        if (mRandomHeights == null) {
                            mRandomHeights = new SparseIntArray(getItemCount());
                        }

                        if (mRandomHeights.get(position) == 0) {
                            mRandomHeights.put(position, ViewUtils.dip2px((int) (100 + Math.random() * 100)));
                        }

                        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                        lp.height = mRandomHeights.get(position);
                        holder.itemView.setLayoutParams(lp);

                    }
                    final Field field = getData().get(position).getData();
                    if (!Modifier.isPublic(field.getModifiers())) return;

                    Object value = null;
                    try {
                        value =ReflectionUtils.getFieldValue(functionObject,field.getName(),Object.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    FluidLayout layout = holder.getView(R.id.fluidlayout);
                    layout.removeAllViews();
                    TextView tv = new TextView(layout.getContext());
                    tv.setLines(1);
                    tv.setMinWidth(ViewUtils.dip2px(200));
                    tv.setText(field.getName());
                    tv.setTypeface(Typeface.defaultFromStyle(Modifier.isStatic(field.getModifiers())?Typeface.BOLD:Typeface.NORMAL));
                    tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                    layout.addView(tv);
                    tv = new TextView(layout.getContext());
                    tv.setLines(1);
                    tv.setText("=");
                    tv.setPadding(ViewUtils.dip2px(5), 0, ViewUtils.dip2px(5), 0);
                    tv.setTypeface(Typeface.defaultFromStyle(Modifier.isStatic(field.getModifiers())?Typeface.BOLD:Typeface.NORMAL));
                    layout.addView(tv);

                    if (field.getType().getName().equals(String.class.getName())) {
                        tv = new TextView(layout.getContext());
                        tv.setText("\"");
                        tv.setPadding(ViewUtils.dip2px(2), 0, 0, 0);
                        tv.setTypeface(Typeface.defaultFromStyle(Modifier.isStatic(field.getModifiers())?Typeface.BOLD:Typeface.NORMAL));
                        layout.addView(tv);
                        EditText et = new EditText(layout.getContext());
                        et.setText(String.format("%s", value));
                        et.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                        et.setTypeface(Typeface.defaultFromStyle(Modifier.isStatic(field.getModifiers())?Typeface.BOLD:Typeface.NORMAL));
                        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        et.setBackgroundColor(Color.TRANSPARENT);
//                    et.setFocusable(false);
//                    et.setFocusableInTouchMode(true);
                        et.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                try {
                                    field.set(functionObject, String.valueOf(editable.toString()));
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        layout.addView(et);
                        tv = new TextView(layout.getContext());
                        tv.setText("\"");
                        tv.setTypeface(Typeface.defaultFromStyle(Modifier.isStatic(field.getModifiers())?Typeface.BOLD:Typeface.NORMAL));
                        tv.setPadding(0, 0, ViewUtils.dip2px(2), 0);
                        layout.addView(tv);
                    } else if (field.getType().getName().equals(boolean.class.getName()) || field.getType().getName()
                            .equals(Boolean.class.getName())) {

                        TextView btv = new TextView(layout.getContext());
                        btv.setText(String.format("%s", value));
                        btv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                        btv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        btv.setBackgroundColor(Color.TRANSPARENT);
                        btv.setTypeface(Typeface.defaultFromStyle(Modifier.isStatic(field.getModifiers())?Typeface.BOLD:Typeface.NORMAL));
                        btv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    field.setBoolean(functionObject, !(Boolean) field.get(functionObject));
                                    ((TextView) view).setText(String.format("%s", (Boolean) field.get(functionObject)));
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        layout.addView(btv);

                    } else if (!field.getType().getName().contains(".")) {
                        final EditText et = new EditText(layout.getContext());
                        et.setText(String.format("%s", value));
                        et.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        et.setBackgroundColor(Color.TRANSPARENT);
                        et.setTypeface(Typeface.defaultFromStyle(Modifier.isStatic(field.getModifiers())?Typeface.BOLD:Typeface.NORMAL));
                        et.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                try {
                                    if (field.getType() == int.class || field.getType() == Integer.class)
                                        try {
                                            field.setInt(functionObject, Integer.valueOf(editable.toString()));
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    if (field.getType() == double.class || field.getType() == Double.class) try {
                                        field.setDouble(functionObject, Double.valueOf(editable.toString()));
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                    if (field.getType() == float.class || field.getType() == Float.class) try {
                                        field.setFloat(functionObject, Float.valueOf(editable.toString()));
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                    if (field.getType() == long.class || field.getType() == Long.class) try {
                                        field.setLong(functionObject, Long.valueOf(editable.toString()));
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                    et.setBackgroundColor(Color.TRANSPARENT);
                                } catch (NumberFormatException e) {
                                    et.setBackgroundColor(Color.RED);
                                }
                            }
                        });
                        layout.addView(et);
                    } else {
                        tv = new TextView(layout.getContext());
                        tv.setText(String.format("%s", field.getType().getName()));
                        tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                        tv.setTypeface(Typeface.defaultFromStyle(Modifier.isStatic(field.getModifiers())?Typeface.BOLD:Typeface.NORMAL));
                        layout.addView(tv);

                    }

                    break;
            }
        }


        @Override
        protected void addItemTypes() {
            addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_sv_pinnedheader);
            addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_sv_smethod);
        }

    }

    public abstract class DataObserver {
        TextView v;
        CycleTask task;
        public DataObserver register() {

            v = new TextView(getAct());
            v.setText("资源监视器\n初始化中");
            v.setTextColor(Color.WHITE);
            v.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            layout_observer.addView(v);
            v.setMaxLines(4);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewUtils.dip2px(90),-1);
            params.setMargins(ViewUtils.dip2px(2),ViewUtils.dip2px(1),ViewUtils.dip2px(2),ViewUtils.dip2px(1));
            v.setLayoutParams(params);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unRegister();
                }
            });
            observers.add(this);
            task=new CycleTask(CycleTask.ImmediatelyRun, durObserverTime, CycleTask.CircleForever) {
                @Override
                public void doCycleAction(int haveCycleCount) throws MyException {
                    cycleTime=durObserverTime;
                    update(observer());
                }
            }.register(getAct());
            return this;
        }

        public void unRegister() {
            layout_observer.removeView(v);
            observers.remove(this);
            task.unRegister();
        }

        abstract String observer();

//        @UIThread
        public void update(String str) {
            if(SmartRun.sPrepare(this,str)) return;
            v.setText(str);
        }
    }

    public class FieldObserver extends DataObserver {
        Object obj;
        Field field;
        public DataObserver register(){
            for (DataObserver observer : (ArrayList<DataObserver>) observers.clone()) {
//                mLog.array(this.obj,((FieldObserver)observer).obj);
//                mLog.array(this.field,((FieldObserver)observer).field);

                if (equals(observer)) {
                    mToast.getInstance(getAct()).show("已存在该属性监听");
                    return observer;
                }
            }
            super.register();
            return this;
        }

        FieldObserver(Object obj, Field field) {
            this.obj = obj;
            this.field = field;
        }

        @Override
        String observer() {
            String value="";
            try {
                Object re=ReflectionUtils.getFieldValue(functionObject,field.getName(),Object.class);
                if(re!=null){
                    if (re instanceof Map) value=re+"\nsize:"+((Map) re).size();else
                    if (re instanceof Object[]) value=re+"\nlength:"+((Object[]) re).length;else
                        value= String.valueOf(re);
                }

            } catch (Exception e) {
                e.printStackTrace();
                value= "属性读取错误";
            }
            return String.format("%s\n%s",field.getName(),value);

        }



        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FieldObserver) {

                if (/*(((FieldObserver) obj).obj == obj||((FieldObserver) obj).obj .equals(obj) ) && */(((FieldObserver) obj).field == field||((FieldObserver) obj).field .equals(field) )) {
                    return true;
                }
            }
            return false;
        }
    }

}
