#Utils 工具类





##[mLog Log日志类](mLog.java)

* 快捷的自定义Log类
* 来源网上

     > 单参数的Log日志，并会给出该log所在方法和在代码中的行数，方便Log数据过多时准确定位

1. 只有在DeBug模式下才会进行打印

##[DebugUtils 调试用工具类](DebugUtils.java)
* `runningDurtime()` 方法性能检测埋点

     > 在代码中需要测试性能的地方调用该方法，可以检测出每个埋点之间的运行时间

 1. 可以在多个方法中建立埋点，但是会单独进行统计，每个方法埋点数据互相不通用
 2. 可以识别循环语句，并给出循环报告
 3. 只有在DeBug模式下才会进行统计以节约性能

##[CycleTask 异步循环任务类](CycleTask.java)

* 创建新任务
   >实例化该类并重写`doCycleAction()`方法以创建一个任务

         CycleTask(long daleyTime, long cycleTime, int cycleNum)
   1. `daleyTime` 首次运行前延迟时间 单位ms
   2. `cycleTime` 单次间隔事件
   3. `cycleNum` 循环次数
      1. 如果需要无限循环 设置为<=0的值
   4. `doCycleAction(int haveCycleCount)` 循环事件
      1. `haveCycleCount` 已经循环的次数，不包括该次
      2. 你需要为该方法写上注释以设置其运行所在的线程
            1.  `@ThreadStyle(style=ThreadStyle.Style.UI)` UI线程
            2.  `@ThreadStyle(style=ThreadStyle.Style.BG)` 后台线程

* 注册
   >两种注册方法：注册在内置线程或者外部线程

        register(Object tag)
        registerOwnThreadTask(Object tag)

   1.`tag`为该任务标识，用于批量移除，如不需要可以设为`null`
* 取消注册

        unRegister(Object tag)
        unregister()

   1.`tag`为该任务标识，用于批量移除

* 外部线程接入

   >* 共用外部循环线程以节约线程量
   >* 外部线程如果循环间隔过长，可能会使任务运行间隔误差增大

   1. 线程while()内需要引用`CycleTask.threadWhileRun()`方法
   2. 根据运行情况可以增加`sleep()`保证性能


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