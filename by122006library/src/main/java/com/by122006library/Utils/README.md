#Utils 工具类




##[DebugUtils 调试用工具类](DebugUtils.java)
* `runningDurtime()` 方法性能检测埋点

     > 在代码中需要测试性能的地方调用该方法，可以检测出每个埋点之间的运行时间

 1. 可以在多个方法中建立埋点，但是会单独进行统计，每个方法埋点数据互相不通用
 2. 可以识别循环语句，并给出循环报告
 3. 只有在DeBug模式下才会进行统计以节约性能


##[ViewUtils 视图工具类](ViewUtils.java)
* `introduceView()` 控件介绍显示方法

     introduceView(View v, String str, boolean roundRect, PopupWindow.OnDismissListener onDismissLinstener)

     > 黑色遮罩屏幕其余部分，并高亮显示一个控件的介绍界面

    1. `v` 所需要介绍的控件对象
    2. `str` 介绍文字内容
    3. `roundRect` 是否是圆形高亮区域显示 `true`时为圆形，反之为矩形
    4. `onDissmissLinstener()` dismiss`事件的回调，可以在这里显示下一个提示栏或者其他更多操作
    5. 文字显示位置会自动根据控件所在位置进行调整

* `surroundViewGroup()` 包裹控件方法

      * surroundViewGroup(View v, T viewGroup)
      * surroundViewGroup(View v, Class<T> viewGroupClassName)
     > 使ViewGroup对象或者其class名生成一个viewGroup，并对其进行包裹

     1. `v` 所需要包裹的控件对象
     2. 新的控件会继承：原有控件的布局配置，父布局的继承关系。使前后在外观及使用上保持一致性

