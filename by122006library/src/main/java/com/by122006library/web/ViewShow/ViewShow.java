package com.by122006library.web.ViewShow;

import com.by122006library.item.ColorStyle;

/**
 * Created by admin on 2016/12/20.
 */

public interface ViewShow {
    public void showLoading(String str, ColorStyle style);

    public void showError(String error, ColorStyle style);

    public void dismiss();
}
