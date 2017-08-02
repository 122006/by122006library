# by122006library

>* 这是一个普通开发者在开发过程中记录的功能&工具的库
>* 新的想法、新的技术、新的魅力
>* 大量原创内容~


* [by122006library](/by122006library/src/main/java/com/by122006library/README.md) 主体库
* [app](/app) 库的展示程序

#### 就是这样~~

 <font color=#6495ED size=6 face=“宋体”>[更新日志.md](/update.md)</font>


### 值得一试的核心原创功能
******
  > 标题指向代码文件，具体使用方法见Md文件


  仅列举少量重要独立功能，更多工具类、细节功能请自行尝试或查阅相关MD文件

1. [SmartRun 智能线程](/by122006library/src/main/java/com/by122006library/Functions/SmartRun.java) （推荐级数：★★★★★）    [ReadMe.md](/by122006library/src/main/java/com/by122006library/Functions/README.md)

    依赖注入框架，可以便捷地指定方法所运行的线程(后台，UI)，优化代码层次节省开发时间使开发者更加专注于逻辑层次

    ps:与AndroidAnnotations的no magic特性不同，利用堆栈进行方法退栈调用以达到转化线程类型的原理。
    
    ps2:现在推荐使用AspectThread进行线程切换

2. [CycleTask 循环任务](/by122006library/src/main/java/com/by122006library/Functions/CycleTask/CycleTask.java) &
   [DelayTask 延迟任务](/by122006library/src/main/java/com/by122006library/Functions/CycleTask/DelayTask.java)（推荐级数：★★★★★）
    [ReadMe.md](/by122006library/src/main/java/com/by122006library/Functions/README.md)


   以单个线程进行循环事件维护，告别画面卡顿和多线程频繁注册带来的混乱，事件支持注解设定线程

   tag标志批量管理事件生命周期，及时释放资源，管理多任务

3. [mLog 精准日志](/by122006library/src/main/java/com/by122006library/Functions/mLog.java) （推荐级数：★★★★★）
 [ReadMe.md](/by122006library/src/main/java/com/by122006library/Functions/README.md)

   拒绝TAG，拒绝logcat上密密麻麻不知所措的条目，从使用mLog开始

   同系统Log的使用，自动标记所在类名方法位置、Release自动隐藏、格式化显示格式
   
   可以一行代码替换所有的系统Log使用


4. <font color=#6495ED size=3 face=“宋体”>SubclassAttribute 虚拟属性</font> （推荐级数：★★★★☆）
 [ReadMe.md](/by122006library/src/main/java/com/by122006library/Functions/README.md)

   Java规定了在子类继承时，子类中设定父类所用属性只能通过赋值或赋值方法。

   SubclassAttribute可以让你直接在子类中定义属性，父类中可以获得子类值。


5. [Web 网络连接](/by122006library/src/main/java/com/by122006library/web/Web.java) （推荐级数：★★☆☆☆）
 [ReadMe.md](/by122006library/src/main/java/com/by122006library/web/README.md)

    拥有自定义规则拦截派发，自定义解析，进度View绑定，多种Json自解析等功能。同时创建代码清晰易读易维护


#### 更多便捷原创内容欢迎大家发掘~



## 如何在项目中使用
##### 第一步：在你的根 build.gradle 中添加:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
##### 第二步：增加依赖库

	dependencies {
	        compile 'com.github.122006:by122006library:最新版本'
	}


### Aspectj支持配置
##### 第一步：请在项目的主 build.gradle 中增加以下依赖:

	buildscript{
	    dependencies{
	        ...
            classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:1.0.10'
	    }
	}
##### 第二步：请在项目的app模块 build.gradle 的起始部位增加以下代码:

	apply plugin: 'com.hujiang.android-aspectjx'



>“最新版本”请替换为最新的版本号，最新版本号参见下方标牌绿色部分，如"v1.8.9"

[![](https://jitpack.io/v/122006/by122006library.svg)](https://jitpack.io/#122006/by122006library)


<font color=#6495ED size=5 face=“宋体”>[混淆配置文件](/need_proguard.md)</font>