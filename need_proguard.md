
## 混淆声明

    #不混淆NoConfusion标记
    -keep class * implements com.by122006library.Interface.NoConfusion_Fields {
        <fields>;
     }
    -keep class * implements com.by122006library.Interface.NoConfusion_Methods {
        <methods>;
     }