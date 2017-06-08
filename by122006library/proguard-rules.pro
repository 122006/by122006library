# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\admin\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepclassmembers class * extends android.app.Activity {#不混淆Activity中参数类型为View的所有方法
   public void *(android.view.View);
}
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {#不混淆Enum类型的指定方法
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#不混淆Parcelable和它的子类，还有Creator成员变量
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#不混淆R类里及其所有内部static类中的所有static变量字段
-keepclassmembers class **.R$* {
    public static <fields>;
}
#该库不会参与混淆
-keep class com.by122006library.** { *;}
#不混淆NoProguard_Fields标记
-keep class * implements com.by122006library.Interface.NoProguard_Fields {
  <fields>;
}
-keep class * implements com.by122006library.Interface.NoProguard_Methods {
  <methods>;
}