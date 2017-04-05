package com.by122006library.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.by122006library.MyException;
import com.by122006library.R;
import com.by122006library.Utils.ViewUtils;

/**
 * Created by admin on 2017/4/5.
 */

public class InputDialog extends AlertDialog{
    public InputDialog(Context context) {
        super(context);
    }

    public InputDialog(Context context, int theme) {
        super(context, theme);
    }
    public static class Builder {
        private Context context;
        private String title;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private String strPositiveButton;
        private String strNegativeButton;

        public Builder(Context context) {
            this.context = context;
        }



        public String getText(String show)throws MyException{
            if(contentView==null) throw new MyException("InputDialog数据未初始化");
            View v=contentView.findViewWithTag(show);
            if(v==null) throw new MyException("InputDialog中未找到"+show+"的内容项，请检查拼写是否有误");
            return ((EditText)v).getText().toString();
        }
        public EditText getEditView(String show)throws MyException{
            if(contentView==null) throw new MyException("InputDialog数据未初始化");
            View v=contentView.findViewWithTag(show);
            if(v==null) throw new MyException("InputDialog中未找到"+show+"的内容项，请检查拼写是否有误");
            return (EditText)v;
        }
        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public InputDialog.Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public InputDialog.Builder addInputItem(String show,String hint){
            if(contentView==null){
                contentView=new LinearLayout(context);
                ((LinearLayout)contentView).setOrientation(LinearLayout.VERTICAL);
            }
            View view=((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_inputdialog,null);
            ((TextView)view.findViewById(R.id.show)).setText(show);
            ((EditText)view.findViewById(R.id.text)).setHint(hint);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(-2,-2);
            params.gravity= Gravity.CENTER;
            params.setMargins(ViewUtils.dip2px(10),ViewUtils.dip2px(3),ViewUtils.dip2px(10),ViewUtils.dip2px(3));
            view.setLayoutParams(params);
            view.findViewById(R.id.text).setTag(show);
            ((LinearLayout)contentView).addView(view);
            return this;
        }
        public InputDialog.Builder addInputItem(String show){
            return addInputItem(show,"");
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public InputDialog.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public InputDialog.Builder setPositiveButton(String str,
                OnClickListener listener) {
            this.strPositiveButton=str;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public InputDialog.Builder setNegativeButton(String str,
                OnClickListener listener) {
            this.strNegativeButton=str;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public AlertDialog show(){
            if(context==null) return null;
            if(((Activity)context).isFinishing()) return null;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(Looper.myLooper() != Looper.getMainLooper()) Looper.prepare();
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setView(contentView);
            dialog.setTitle(title);

            if (positiveButtonClickListener != null) {
                dialog.setPositiveButton(strPositiveButton,positiveButtonClickListener);
            }
            // set the cancel button
            if (negativeButtonClickListener != null) { dialog.setNegativeButton(strNegativeButton,negativeButtonClickListener);
            }


            if(Looper.myLooper() != Looper.getMainLooper()) Looper.loop();
            return dialog.show();
        }

    }
}
