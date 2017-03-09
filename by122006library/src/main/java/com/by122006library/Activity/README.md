#Activity 页面类

##[BaseActivity 根页面类](BaseActivity.java)
   > 自定义的根页面类。提供常用方法，提供工具类所需元素，建议**所有界面**继承该类

   ####需要在`super.onCreate()`前设置内容

   * `boolean canRotate` 页面是否可以旋转
   * `FLAG_ACT_NO_TITLE` 页面是否移除title
   * `FLAG_ACT_FULLSCREEN` 页面是否全屏

   ####常用方法及常量
   * `getTopActivity()` 获得顶层界面对象

   * `getContext()` 获得顶层activity的上下文
   * `showStringPopup_BottomView()` 在某控件下显示文本
   * `isOnline()` 是否有网络连接
   * `isWifiConnected()` 是否是WIFI连接状态
   * `haveSDCard()` 是否有SD卡
   * `goHome()` 返回桌面
   * `getDecorView()`获得顶层`DecorView`
   * `getViewBitmapById()` 根据resid获得布局截图
   * `registerActivityResultCallBack()` 注册一个界面反馈回调器
   * `exit()` 安全退出整个程序

   ####需要继承方法
   * `onUpdateUi()` UI界面的更新
      1. 这个方法会在`onResume()`、`onAttachedToWindow()`后自动调用一次
      2. 可以正确获得的控件的Params以进行设置大小



