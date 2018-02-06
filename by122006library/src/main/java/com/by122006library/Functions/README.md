# Functions 功能类





## [mLog Log日志类](mLog.java)

* 快捷的自定义Log类
* 来源网上

     > 单参数的Log日志，并会给出该log所在方法和在代码中的行数，方便Log数据过多时准确定位

1. 只有在DeBug模式下才会进行打印


## [CycleTask 异步循环任务类](CycleTask/CycleTask.java)
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

* 其他方法
    `endCycleAction` 结束运行方法。会在解绑时被调用（因此循环结束也会生效）



## [PeriodConstant 周期变量类](PeriodConstant.java)

你需要在初始化阶段通过构造器新建一个PeriodConstant对象

     (new PeriodConstant.Builder_Add(周期开始值,周期结束值,周期时间)).builder();

`Builder_Add`为递增周期函数构造器，你也可以继承`PeriodConstant.Builder`类、并重写`formula(int constant, long thisTime)`方法以新建适用的适配器

在循环部分你可以通过`getDouble()`或者`getInt()`以获取对应格式的值

1. 该类不依赖于线程，不会造成额外的系统开销


## [SmartRun 智能运行线程类](SmartRun.java)
> * 已经废弃，请使用 [ASM_SmartRunPluginImp 线程调度插件](../ASMSmartRunPlugin/src/main/groovy/com/by122006/buildsrc/ASM_SmartRunPluginImp.groovy)

> * 甚至比`AsyncTask`更小的学习成本，更简洁直观的代码结构，0回调，需要关心的只有逻辑

> * 请使用ASMSmartRunPlugin

* 基础使用方法

    >这里调用一个当前线程的任务作为示例

    1. 新建一个实例，在`action()`方法中进行主体任务
    2. 外部调用`start()`开始任务
* 规定线程运行类型

    >这里为上一个实例规定所运行的线程

    1. 为`action()`方法增加注释。例如`@ThreadStyle(style = ThreadStyle.Style.Default)`
    2. 可选类型：UI,BG,Default
    3. 你也可以使用`UIThread`、`BGThread`、`DefaultThread`代替

        > 你也可以使用同名的注释，但是如果确定这样做，你需要避免混淆该注释

* 规定后台线程运行是否同步

    >这里为上一个实例规定所运行的线程同步与否

    1. 为`action()`方法增加注释。例如`@Async`
    2. 默认为同步
    3. 异步为新线程运行任务
    4. 只有前后都是后台线程才需要设置是否同步，UI线程请进行逻辑处理

* 线程多任务规划

    >多个复杂结构的运行任务，需要运行在不同的线程

    1. 你可以在新建的实例类中任意新建方法作为新的任务(**方法中若含有参数，必须在prepare()参数传入！**)
    2. 新建的方法也需要注释运行所在的线程。例如`@ThreadStyle(style = ThreadStyle.Style.Default)` `@Async`
    3. 新建的方法必须在方法体**最前**增加一行代码

            if(prepare()) return;
    4. 你可以在类中任何地方循序或者嵌套调用该新建方法，会自动根据注释运行在对应的线程

    >同理，你也可以不使用`start()`开始任务，而是使用自定义的开始方法，但是你需要为该方法增加`if(prepare()) return;`


* 自动代码修复

    >减少调试时不必要的退出，增加代码的调试效率

    1. 对于"**在子线程更新UI**(**CalledFromWrongThreadException**)"、"**在UI线程网络连接()**(**NetworkOnMainThreadException**)"，SmartRun可以有效地检测并切换到正确的线程中，在log中有对应的错误消息
    2. 如果进行错误修复，方法中错误之前的代码会被重复运行
    3. 如果当前方法中，同时存在两种错误，SmartRun会在一次尝试之后出错停止
    4. 错误会被记录，在该次SmartRun不会重复出现
    5. 该方法只是调试用方法，请尽量避免出现该情况

* **外部线程快捷调用**

    >**实验中功能，不保证稳定性**。任何地方确保某方法运行在指定的线程

          @UIThread
            public void 任意方法名(参数) {
                if(SmartRun.sPrepare(this(,参数))) return;
            }

    1. 在任意实体类的任意方法的方法开头调用`if(SmartRun.sPrepare(this)) return;`
    2. 为该方法增加注释定义所运行的线程。例如`@ThreadStyle(style = ThreadStyle.Style.Default)`



* 注意
    1. 使用到反射及堆栈，请不要混淆该类
    2. 只有`action()`这一默认主任务不需要指定`if(prepare()) return;`
    3. 如果未调用`start()`或者自定开始任务，任务不会被运行
    4. 如果方法中需要含有参数，请在`prepare()`传入该方法所有的参数
    5. 任务反射消耗时间：1ms-2ms。请考虑性能因素
    6. 暂时不支持静态方法
    7. 暂时不支持返回数据
        >* 不同线程不应该使用同步的返回，应该线性进行相关方法调用；
       >* 相同线程不需要使用线程控制，可以直接调用方法得到返回。
    8. 基础类型和其封装器视为相同参数，注意方法重复问题
    9. 由于无法单独调用父类同名方法，不支持重写同名方法，即所使用的方法**一定为 final**


## [AttBinder 关联型属性变量类](AttBinder/AttBinder.java)

### 使用步骤：
1. 初始化一个`AttBinder`对象
2. 新建多个`Att`类（或直接初始化其已提供子类），完成其必须方法
3. 通过`bind(Att)`方法为`AttBinder`对象增加`Att`参数

### 细节：
1. 所有属性的联动通过百分比进行设置，属性均为同一百分比，你可以通过重写`transform(double per)`方法进行特征性改变，
但是，如果这么做你不应该对其进行监听。
2. 属性的变化在同一时间仅会受到一个监听器的约束，防止循环监听
3. 你可以直接通过`Binder`的`fluctuation(double per)`进行全局变量修改，但是除了初始化之类，不建议这么做
4. 如果含有特殊对象，你需要重写`remove()`方法释放。同样，在属性变化结束之后，建议调用`AttBinder`对象的`destroy()`方法进行资源的回收

### 常用子类
1. [ViewAtt 控件属性类](AttBinder/ViewAtt.java) 方便对控件的属性进行定义
2. [TimeAtt 定时控件修改类](AttBinder/TimeAtt.java) 以60帧左右的速度刷新制定时间的控件




## <font color=#8888ee size=5 face=“宋体”>SubclassAttribute 虚拟属性</font>

   > Java在子类继承时只能使用继承，子类中设定父类所用属性只能通过赋值或赋值方法。

   > SubclassAttribute可以让你直接在子类中定义属性，并且在父类中可以获得子类值。

   ### 使用步骤：
   1. 为父类增加`@Subclass`注释（`att`参数为`@Attribute[]`类型，注册需要在内容使用的子类虚拟属性）

           @Subclass(att = {@Attribute(name = "FLAG_ACT_FULLSCREEN", type = boolean.class, defaultValue = "false"),
                 @Attribute(name = "FLAG_ACT_NO_TITLE", type = boolean.class, defaultValue = "true")})
   2. 你可以在子类中声明对应的属性量
   3. 你可以在父类中使用如下方法调用属性
        1. 静态调用

            `BaseActivity_Attribute.getFLAG_ACT_FULLSCREEN(this,a);`

            静态类类名规则：父类名+"-_Attribute"
        2. 动态调用

            `SubclassAttribute.with(this).getFLAG_ACT_NO_TITLE();`

        #### 方法名规则：get+首字母大写的属性名
        #### 更多方法：`setXXXX(xxx)`、`initXXXX()`

   ### 细节：
   1. 该功能不支持混淆、如果必须要使用混淆，请为继承序列中任意类添加`NoConfusion_Fields`接口声明