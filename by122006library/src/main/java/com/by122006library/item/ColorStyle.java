package com.by122006library.item;

/**
 * Created by admin on 2016/12/20.
 */


public class ColorStyle {
    public static ColorStyle initData;
    private int bgColor = 0xffdd8c00;
    private int textColor = 0xffffffff;
    private int topBarTextColor = 0xffffffff;

    public int getTopBarBgColor() {
        return topBarBgColor;
    }

    public void setTopBarBgColor(int topBarBgColor) {
        this.topBarBgColor = topBarBgColor;
    }

    public int getTopBarTextColor() {
        return topBarTextColor;
    }

    public void setTopBarTextColor(int topBarTextColor) {
        this.topBarTextColor = topBarTextColor;
    }

    private int topBarBgColor = 0xff000000;

    public static ColorStyle getInitData() {
        if(initData==null) initData=new ColorStyle();
        return initData;
    }

    public static void setInitData(ColorStyle initData) {
        ColorStyle.initData = initData;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

}