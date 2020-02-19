package com.by122006library.Utils;

import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;

/**
 * Created by admin on 2017/4/7.
 */

public class AdapterUtils {
    public static void updateAllShowRows(AbsListView listView) {
        if (listView != null) {
            Adapter adapter = listView.getAdapter();
            if (adapter == null) return;
            for (int i = listView.getFirstVisiblePosition(); i <= listView.getLastVisiblePosition(); i++) {
                updateSingleRow(listView, i);
            }
        }
    }

    public static void updateSingleRow(AbsListView listView, long id) {
        if (listView != null) {
            Adapter adapter = listView.getAdapter();
            if (adapter == null) return;
            int start = listView.getFirstVisiblePosition();
            for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++)
                if (id == i) {
                    View view = listView.getChildAt(i - start);
                    View newView = adapter.getView(i, view, listView);
                    if (newView == view) break;
                    listView.removeView(view);
                    listView.addView(newView, i - start);
                    break;
                }
        }
    }


}
