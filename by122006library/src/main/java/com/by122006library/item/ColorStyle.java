package com.by122006library.item;

/**
 * Created by admin on 2016/12/20.
 */


public class ColorStyle {
    public static ColorStyle initData;
    private int bgColor = 0xffdd8c00;
    private int tvColor = 0xffffffff;

    public static ColorStyle getInitData() {
        if(initData==null) initData=new ColorStyle();
        return initData;
    }

    public static void setInitData(ColorStyle initData) {
        ColorStyle.initData = initData;
    }

    public int getTvColor() {
        return tvColor;
    }

    public void setTvColor(int tvColor) {
        this.tvColor = tvColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

}