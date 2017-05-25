#Web相关类

* 同步通信方法
    > * 请勿在主线程使用该方法
    > * 会返回获得的JSON,你也可以不使用回调而是使用JSON解析方式进行操作

        doSynchroHttp(RequestBuilder requster, @Nullable WEBBaseCallBack callback, @Nullable ViewShow vs)

* [Web](Web.java) 网络任务主体
    1. JSONObject：正常情况下会返回解析出的JSON数据，如果错误，会返回`null`
    2. 解析规则：解析 页面中\<body\>\<\body\>标签中、页面本身的 JSON数据
* [RequestBuilder](RequestBuilder.java) 请求主体
    1. 设置请求的内容
        1. `setToken()` 连接令牌，只需设置一次
        2. `setUrl()` 设置地址，只需要设置一次
        3. `addAtt()` 增加键值对参数
        4. `setAction()` 设置请求action
        5. `get()` / `post()` 设置get或者post发送(默认为post)
* [WEBBaseCallBack](CallBack/WEBBaseCallBack.java) 结果回调
    1. 你必须重写 `onSuccess()`,`onFail()`,`onError()` 方法处理对应事件
        1. `onSuccess()`：成功
        2. `onFail()`：失败（服务器返回）
        3. `onError()`：网络错误
    2. 你可以选择重写：
        1. `init()` 新增或覆盖自定义的拦截规则
* [ViewShow](ViewShow/ViewShow.java) 事件提示回调
    1. 用来显示网络连接过程中的提示信息
    2. 通常你可以设为空，或者创建[NoClickablePopup](ViewShow/NoClickablePopup.java)
* 异步通信方法
    > 封装了网络线程，通常用于不需要返回数据的网络请求

        doAsnyHttp(RequestBuilder requster, @Nullable WEBBaseCallBack callback, @Nullable ViewShow vs)
* default全局默认值设定
    大部分属性都支持设定全局默认值。你可以在软件初始化的时候设定它们（方法名中含有default）。当一个网络连接被调用时，如果没有设置该属性，会使用开始设定的默认值
    * 支持的方法: `setDefaultUrl()`、`setDefaultEncode()`、`setDefaultHttpStyle()`、`addDefaultHead(String,String)`、