#Utils 工具类













##[CycleTask 异步循环任务类](CycleTask.java)

* 创建新任务
   >实例化该类并重写`doCycleAction()`方法以创建一个任务

         CycleTask(long daleyTime, long cycleTime, int cycleNum)
   1. `daleyTime` 首次运行前延迟时间 单位ms
   2. `cycleTime` 单次间隔事件
   3. 循环次数
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
   >两种注册方法：注册在内置线程或者外部线程

        unRegister(Object tag)
        registerOwnThreadTask(Object tag)

   1.`tag`为该任务标识，用于批量移除，如不需要可以设为`null`

* 外部线程接入
   >* 共用外部循环线程以节约线程量
   >* 外部线程如果循环间隔过长，可能会使任务运行间隔误差增大
   1. 线程while()内需要引用`CycleTask.threadWhileRun()`方法
   2. 根据运行情况可以增加`sleep()`保证性能
