package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.by122006library.Utils.ReflectionUtils;
import com.by122006library.Utils.ViewUtils;
import com.fynn.fluidlayout.FluidLayout;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by admin on 2017/6/21.
 */

public class SMethod {
    public String name;
    public Class[] needParams;
    public Object[] initParams;
    Method method;
    Object object;
    private View.OnClickListener listener;

    private String descrite = "";

    private SMethod() {

    }

    public static SMethod debug() {
        SMethod m = new SMethod();
        m.object = TiaoShi.class;
        m.name = "";
        m.needParams = new Class[]{};
        m.initParams = new Object[]{};

        return m;
    }

    public static SMethod createByMethod(Object obj, Method method) {
        SMethod m = new SMethod();
        m.object = obj;
        m.name = method.getName();
        m.needParams = method.getParameterTypes();
        m.initParams = new Object[m.needParams.length];
        for (int i = 0; i < m.initParams.length; i++) {
            Class type = m.needParams[i];
            if (type == int.class || type == Integer.class) m.initParams[i] = 0;
            if (type == boolean.class || type == Boolean.class) m.initParams[i] = true;
            if (type == long.class || type == Long.class) m.initParams[i] = 0l;
            if (type == float.class || type == Float.class) m.initParams[i] = 0f;
            if (type == double.class || type == Double.class) m.initParams[i] = 0d;
            if (type == short.class || type == Short.class) m.initParams[i] = 0;
            if (type == String.class ) m.initParams[i] = "str";
        }
        m.method = method;
        m.descrite = null;
        return m;
    }

    public static SMethod create(Object obj, String name, Class... parameterTypes) {
        SMethod m = new SMethod();
        m.object = obj;
        m.name = name;
        m.needParams = parameterTypes;
        m.initParams = new Object[parameterTypes.length];
        try {
            Method method = ReflectionUtils.getMethod(obj, name, parameterTypes);

            m.method = method;
            return m;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return m;
    }

    public SMethod setInitParams(Object... initParams) {
        this.initParams = initParams;
        return this;
    }

    public void setSmallInTextView(FluidLayout layout) {
        layout.removeAllViews();
        layout.setPadding(ViewUtils.dip2px(5), ViewUtils.dip2px(1), ViewUtils.dip2px(5), ViewUtils.dip2px(1));
        TextView tv = null;
        if (descrite != null) {
            tv = new TextView(layout.getContext());
            tv.setText("[");
            layout.addView(tv);
            tv = new TextView(layout.getContext());
            tv.setLines(1);
            tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv.setMinWidth(ViewUtils.dip2px(130));
            tv.setText(descrite);
            tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
            layout.addView(tv);
            tv = new TextView(layout.getContext());
            tv.setPadding(0, 0, ViewUtils.dip2px(5), 0);
            tv.setText("]");
            layout.addView(tv);
        }
        if (object == TiaoShi.class) return;

        final ImageView iv = new ImageView(layout.getContext());
        iv.setImageResource(R.drawable.more);
        iv.setLayoutParams(new FluidLayout.LayoutParams(50, -1));
        layout.addView(iv);
        final LinearLayout moreLayout = new LinearLayout(layout.getContext());
        moreLayout.setLayoutParams(new FluidLayout.LayoutParams(-2, -1));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.smoothReplace_Smart(iv, moreLayout, 200);
            }
        };
        iv.setOnClickListener(onClickListener);
        moreLayout.setOnClickListener(onClickListener);

        String METHOD_MODIFIERS = Modifier.toString(method.getModifiers());
        tv = new TextView(layout.getContext());
        tv.setText(String.format("%s", METHOD_MODIFIERS));
        tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
        moreLayout.addView(tv);

        String returnName = method.getReturnType().getSimpleName();
        tv = new TextView(layout.getContext());
        tv.setText(String.format("%s", returnName));
        tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
        moreLayout.addView(tv);

        String name = method.getName();
        tv = new TextView(layout.getContext());
        tv.setText(String.format("%s", name));
        tv.setTypeface(Typeface.defaultFromStyle(Modifier.isStatic(method.getModifiers())?Typeface.BOLD:Typeface.NORMAL));
        tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
        layout.addView(tv);

        LinearLayout layoutParams = new LinearLayout(layout.getContext());
        tv = new TextView(layout.getContext());
        tv.setText("(");
        tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
        layoutParams.addView(tv);
        final Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (types[i] == null) {
                tv = new TextView(layout.getContext());
                tv.setText(String.format("%s", types[i].getSimpleName()));
                tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                layoutParams.addView(tv);
            } else {
                if (types[i] == String.class) {
                    tv = new TextView(layout.getContext());
                    tv.setText("\"");
                    tv.setPadding(ViewUtils.dip2px(2), 0, 0, 0);
                    layoutParams.addView(tv);
                    EditText et = new EditText(layout.getContext());
                    et.setText(String.format("%s", initParams[i]));
                    et.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                    et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    et.setBackgroundColor(Color.TRANSPARENT);
//                    et.setFocusable(false);
//                    et.setFocusableInTouchMode(true);
                    final int finalI = i;
                    et.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            initParams[finalI] = editable.toString();
                        }
                    });
                    layoutParams.addView(et);
                    tv = new TextView(layout.getContext());
                    tv.setText("\"");
                    tv.setPadding(0, 0, ViewUtils.dip2px(2), 0);
                    layoutParams.addView(tv);
                } else if (types[i] == boolean.class || types[i] == Boolean.class) {

                    TextView btv = new TextView(layout.getContext());
                    btv.setText(String.format("%s", initParams[i]));
                    btv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                    btv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    btv.setBackgroundColor(Color.TRANSPARENT);
                    final int finalI = i;
                    btv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initParams[finalI] = !(Boolean) initParams[finalI];
                            ((TextView) view).setText(String.format("%s", initParams[finalI]));
                        }
                    });
                    layoutParams.addView(btv);

                } else if (!types[i].getName().contains(".")) {
                    final EditText et = new EditText(layout.getContext());
                    et.setText(String.format("%s", initParams[i]));
                    et.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                    et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    et.setBackgroundColor(Color.TRANSPARENT);
//                    et.setFocusable(false);
//                    et.setFocusableInTouchMode(true);
                    final int finalI = i;
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
                                if (types[finalI] == int.class || types[finalI] == Integer.class)
                                    initParams[finalI] = Integer.valueOf(editable.toString());
                                if (types[finalI] == double.class || types[finalI] == Double.class)
                                    initParams[finalI] = Double.valueOf(editable.toString());
                                if (types[finalI] == float.class || types[finalI] == Float.class)
                                    initParams[finalI] = Float.valueOf(editable.toString());
                                if (types[finalI] == long.class || types[finalI] == Long.class)
                                    initParams[finalI] = Long.valueOf(editable.toString());
                                et.setBackgroundColor(Color.TRANSPARENT);
                            } catch (NumberFormatException e) {
                                et.setBackgroundColor(Color.RED);
                            }
                        }
                    });
                    layoutParams.addView(et);
                } else {
                    tv = new TextView(layout.getContext());
                    tv.setText(String.format("%s", types[i].getSimpleName()));
                    tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                    layoutParams.addView(tv);
                }
            }

            if (i != types.length - 1) {
                tv = new TextView(layout.getContext());
                tv.setText(" , ");
                tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
                layoutParams.addView(tv);
            }

        }

        tv = new TextView(layout.getContext());
        tv.setText(")");
        tv.setPadding(ViewUtils.dip2px(2), 0, ViewUtils.dip2px(2), 0);
        layoutParams.addView(tv);

        layout.addView(layoutParams);
//        tv.setText(String.format("[%s] %s %s %s%s(%s);",objName,METHOD_MODIFIERS,returnName,name,params.length()
// >20?"\n":"", params));
//        textView.setText(method.toGenericString());

    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public String getDescrite() {
        return descrite;
    }

    public void setDescrite(String descrite) {
        this.descrite = descrite;
    }
}
