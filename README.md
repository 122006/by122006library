# by122006library

>* 这是一个普通开发者在开发过程中记录的常用功能&工具的库
>* 学造轮子学一遍就够了，拒绝反复造轮子~
>* 大量原创内容，并不是网上随意的copy~


* [by122006library](/by122006library/src/main/java/com/by122006library/README.md) 主体库
* [app](/app) 测试用的库(没啥用)

#### 就是这样~~

### 值得一试的核心原创功能
******
  >标题指向代码文件，具体使用方法见相关文件夹内Md文件

1. [SmartRun 智能线程](Utils/SmartRun.java) （推荐级数：★★★★★）

    依赖注入框架，可以便捷地指定方法所运行的线程(后台，UI)，优化代码层次节省开发时间使开发者更加专注于逻辑层次

    ps:与AndroidAnnotations的no magic特性不同，利用堆栈进行方法退栈调用以达到转化线程类型的原理。

2. [CycleTask 循环任务](Utils/CycleTask.java)


   [DelayTask 延迟任务](Utils/DelayTask.java)（推荐级数：★★★★★）


   以单个线程进行循环事件维护，告别画面卡顿和多线程频繁注册带来的混乱，事件支持注解设定线程

   tag标志批量管理事件生命周期，及时释放资源，管理多任务

3. [Web 网络连接](web/Web.java) （推荐级数：★★☆☆☆）

    拥有自定义规则拦截派发，自定义解析，进度View绑定，多种Json自解析等功能。同时创建代码清晰易读易维护


#### 更多便捷原创内容欢迎大家发掘~



## 如何在项目中使用
##### 第一步：在你的根 build.gradle 中添加:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}Copy
##### 第二步：增加依赖库

	dependencies {
	        compile 'com.github.122006:by122006library:最新版本'
	}

>“最新版本”请替换为最新的版本号，最新版本号参见下方标牌绿色部分，如"v0.8"

[![](https://jitpack.io/v/122006/by122006library.svg)](https://jitpack.io/#122006/by122006library)