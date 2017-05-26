# Activity 页面类

## [BaseActivity 根页面类](BaseActivity.java)
   > * 自定义的根页面类。提供常用方法，提供工具类所需元素，建议**所有界面**继承该类
   > * 如果项目不继承该类，且需要使用该类方法及相关功能，请在Application内容类中注册`registerActivityResultCallBack(ActivityResultCallBack)`方法 (如果这么做，你不能使用部分子类用方法)


#### 需要在`super.onCreate()`前设置内容

   * `boolean canRotate` 页面是否可以旋转
   * `FLAG_ACT_NO_TITLE` 页面是否移除title
   * `FLAG_ACT_FULLSCREEN` 页面是否全屏

#### 常用方法及常量
   * `getTopActivity()` 获得顶层界面对象(优先获得外置界面数据)

   * `getContext()` 获得顶层activity的上下文
   * `showStringPopup_BottomView()` 在某控件下显示文本
   * `isOnline()` 是否有网络连接
   * `isWifiConnected()` 是否是WIFI连接状态
   * `haveSDCard()` 是否有SD卡
   * `goHome()` 返回桌面
   * `setClickPopupString()`设置点击某控件显示文字
   * `getDecorView()`获得顶层`DecorView`
   * `getViewBitmapById()` 根据resid获得布局截图
   * `registerActivityResultCallBack()` 注册一个界面反馈回调器
   * `exit()` 安全退出整个程序


#### 需要继承方法
   * `onUpdateUi()` UI界面的更新
      1. 这个方法会在`onResume()`、`onAttachedToWindow()`后自动调用一次
      2. 可以正确获得的控件的Params以进行设置大小


#### DataBinding相关
> 如果你还没有学习DataBinding相关的知识，你可能需要了解一下[DataBinding](http://blog.csdn.net/afanyusong/article/details/51495743)


   本基类已支持DataBinding框架。

   你可以在自己的实体类中，初始化一个继承于`ViewDataBinding`、类名基于layoutID（该类在DataBinding格式的xml定义时编辑器已自动生成）的变量（变量名不限）。
   你不需要为该变量赋值，当定义完成后，在调用`setContentView(int)`方法时会自动为该变量赋值。

> 其实就是省略了一句 `DataBindingUtil.setContentView(this, layoutres);`





