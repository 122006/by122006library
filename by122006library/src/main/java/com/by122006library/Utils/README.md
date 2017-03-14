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
    适用于大批量不同任务定时循环的工具类
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







##[SmartRun 智能运行线程类](SmartRun.java)
> * 流传甚广的链式调用是真的优美还是呆板僵硬？
> * 业务逻辑千千万，是交给一个个第三方黑盒还是程序员自己的手中？
> * 比AsyncTask更小的学习成本，更简洁直观的代码结构，需要关心的只有逻辑

* 基础使用方法

    >这里调用一个当前线程的任务作为示例

    1. 新建一个实例，在`action()`方法中进行主体任务
    2. 外部调用`start()`开始任务
* 规定线程运行类型

    >这里为上一个实例规定所运行的线程

    1. 为`action()`方法增加注释。例如`@ThreadStyle(style = ThreadStyle.Style.Default)`
    2. 可选类型：UI,BG,Default
    3. 你也可以使用`UIThread`、`BGThread`、`DefaultThread`代替

* 规定后台线程运行是否同步

    >这里为上一个实例规定所运行的线程同步与否

    1. 为`action()`方法增加注释。例如`@Async`
    2. 默认为同步
    3. 异步为新线程运行任务
    4. 只有前后都是后台线程才需要设置是否同步，UI线程请进行逻辑处理

* 线程多任务规划

    >多个复杂结构的运行任务，需要运行在不同的线程

    1. 你可以在新建的实例类中任意新建方法作为新的任务(**方法中不能含有参数！**)
    2. 新建的方法也需要注释运行所在的线程。例如`@ThreadStyle(style = ThreadStyle.Style.Default)` `@Async`
    3. 新建的方法必须在方法体**最前**增加一行代码

            if(prepare()) return;
    4. 你可以在类中任何地方循序或者嵌套调用该新建方法，会自动根据注释运行在对应的线程

    >同理，你也可以不使用`start()`开始任务，而是使用自定义的开始方法，但是你需要为该方法增加`if(prepare()) return;`
* 注意
    1. 使用到反射及堆栈，请不要混淆该类
    2. 只有`action()`这一默认主任务不需要指定`if(prepare()) return;`
    3. 如果未调用`start()`或者自定开始任务，任务不会被运行
    4. 如果方法中需要含有参数，请在`prepare()`传入该方法所有的参数
