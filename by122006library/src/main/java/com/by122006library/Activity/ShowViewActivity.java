package com.by122006library.Activity;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Activity->View所必须要继承的类
 * Created by zwh on 2017/4/28.
 */

public abstract class ShowViewActivity extends BaseActivity{
    private LocalActivityManager mLocalActivityManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalActivityManager = new LocalActivityManager( this, true);
        mLocalActivityManager.dispatchCreate(savedInstanceState);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocalActivityManager.dispatchPause(isFinishing());
    }
    @Override
    protected void onResume() {
        super.onResume();
        mLocalActivityManager.dispatchResume();
    }
    public View activityToView(Context parent, Intent intent) {
        final Window w = mLocalActivityManager.startActivity(parent.getClass().getName(), intent);
        final View wd = w != null ? w.getDecorView() : null;
        if (wd != null) {
            wd.setVisibility(View.VISIBLE);
            wd.setFocusableInTouchMode(true);
            ((ViewGroup) wd).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);}
        return wd;
    }
}
