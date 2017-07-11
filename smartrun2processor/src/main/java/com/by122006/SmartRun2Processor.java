package com.by122006;

import com.by122006library.Interface.AsyncThread;
import com.by122006library.Interface.BGThread;
import com.by122006library.Interface.UIThread;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

@AutoService(Processor.class)
public class SmartRun2Processor extends javax.annotation.processing.AbstractProcessor {
    static String cUIThread, cBGThread, cAsyncThread, cThreadManager, cHook;

    static {
        cUIThread = UIThread.class.getName();
        cBGThread = BGThread.class.getName();
        cAsyncThread = AsyncThread.class.getName();
        cThreadManager = "com.by122006library.ThreadManager";
        cHook = "com.me.weishu.epic.Hook";

    }

    ArrayList<ClassName> all = new ArrayList<>();
    private Filer mFiler; //文件相关工具类
    private Elements mElementUtils; //元素相关的工具类
    private Messager mMessager; //日志相关的工具类

    //    }
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * 处理器的初始化方法，可以获取相关的工具类
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    private void doElement(TypeSpec.Builder builder, EMethod eMethod) {
        if (eMethod.element.getKind() != ElementKind.METHOD) return;
        note(eMethod.oClassName + "   -  " + eMethod.oMethodName);
        Element element = eMethod.element;

        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(eMethod.nMethodName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("原始所在类名：$N<br>原始方法名：$N", eMethod.oClassName, eMethod.oMethodName)
                .returns(void.class);
        methodSpec.addStatement("final Object obj = this");
        List<? extends VariableElement> elements = (List<? extends VariableElement>) ((ExecutableElement) element).getParameters();
        String ps = "";
        for (int i = 0; i < elements.size(); i++) {
            VariableElement p = elements.get(i);
            ps += " , p" + i;
            Set set = p.getModifiers();
            Modifier[] array0=(Modifier[]) set.toArray(new Modifier[]{});
            Modifier[] array1=new Modifier[array0.length+1];
            if (!p.getModifiers().contains(Modifier.FINAL)) {
                for(int j=0;j<array0.length;j++){
                    array1[j]=array0[j];
                }
                array1[array0.length]=Modifier.FINAL;
                methodSpec.addParameter(TypeName.get(p.asType()), "p" + i, array1);
            }else

            methodSpec.addParameter(TypeName.get(p.asType()), "p" + i,array0);

        }
        methodSpec.returns(TypeName.get(((ExecutableElement) element).getReturnType()));

        boolean returnVoid = ((ExecutableElement) element).getReturnType().toString().equals("void");

        methodSpec.addStatement("$N manager = $N.getInstance()", cThreadManager, cThreadManager)
                .addStatement("$N.i($S,$S)", "android.util.Log", getClass().getSimpleName(), element.getSimpleName()
                        + "() 方法运行成功！")
                .beginControlFlow("if ( !manager.check( $S ) )", eMethod.threadStyle)
                .addStatement("$N.callOrigin( this$N )", cHook, ps)
                .addStatement("return" + (returnVoid ? "" : " null"))
                .endControlFlow("")
                .beginControlFlow("$T runnable = new $N() ", Runnable.class, Runnable.class.getSimpleName())
                .beginControlFlow("public void run() ")
                .addStatement("$N.callOrigin_Out( obj,$S,$S$N)", cHook,getClass().getSimpleName(),eMethod.nMethodName, ps)
                .endControlFlow("")
                .endControlFlow("");
        if (eMethod.threadStyle.equals(cUIThread)) {
            methodSpec.addStatement("manager.postUIThread( runnable )");

        }
        if (eMethod.threadStyle.equals(cBGThread)) {
            methodSpec.addStatement("manager.postBGThread( runnable )");
        }
        if (eMethod.threadStyle.equals(cAsyncThread)) {
            methodSpec.addStatement("manager.postBGThread( runnable )");
        }
        methodSpec.addStatement("return" + (returnVoid ? "" : " null"));
        builder.addMethod(methodSpec.build());
    }

    /**
     * 处理器的主方法，用于扫描处理注解，生成java文件
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        note("process");
        TypeSpec.Builder builder = TypeSpec.classBuilder("SmartRun2Methods");
        builder.addModifiers(Modifier.PUBLIC);
        builder.addSuperinterface(getClassName("com.by122006library.Interface.NoProguard_All"));
        note("准备根据注解生成自动构建类");

        //处理被注解的元素
        ArrayList<EMethod> eMethods = new ArrayList<>();

        for (Object element : roundEnv.getElementsAnnotatedWith(UIThread.class)) {
            EMethod eMethod = new EMethod();
            eMethod.element = (Element) element;
            eMethod.threadStyle = cUIThread;
            eMethods.add(eMethod);
        }
        for (Object element : roundEnv.getElementsAnnotatedWith(BGThread.class)) {
            EMethod eMethod = new EMethod();
            eMethod.element = (Element) element;
            eMethod.threadStyle = cBGThread;
            eMethods.add(eMethod);
        }
        for (Object element : roundEnv.getElementsAnnotatedWith(AsyncThread.class)) {
            EMethod eMethod = new EMethod();
            eMethod.element = (Element) element;
            eMethod.threadStyle = cAsyncThread;
            eMethods.add(eMethod);
        }
        if (eMethods.size() == 0)
            return true;
        for (int i = 0; i < eMethods.size(); i++) {
            EMethod eMethod = (EMethod) eMethods.get(i);
            eMethod.oMethodName = eMethod.element.getSimpleName().toString();
            eMethod.oClassName = eMethod.element.getEnclosingElement().getSimpleName().toString();
            eMethod.index = i;
            eMethod.generateNMethodName();
            doElement(builder, eMethod);
        }

        TypeSpec java = builder.build();

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile("SmartRun2Methods");
            Writer writer = source.openWriter();
            String fileName = mElementUtils.getPackageOf(eMethods.get(0).element).getQualifiedName().toString();
            JavaFile javaFile = JavaFile.builder(fileName, java)
                    .build();
            javaFile.writeTo(writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            note(e.getMessage());
        }

        note("准备生成映射类");
        TypeSpec.Builder mappingJava = TypeSpec.classBuilder("SmartRun2Mapping")
                .addModifiers(Modifier.PUBLIC);
        mappingJava.addJavadoc("方法的对应映射类");
        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("replace").addModifiers(Modifier.PRIVATE, Modifier
                .STATIC, Modifier.FINAL);
        methodSpec.addStatement("Class dClass = SmartRun2Methods.class");
        methodSpec.addParameter(String.class, "className").addParameter(String.class, "oMethodName").addParameter
                (String.class, "nMethodName").varargs().addParameter
                (Class[].class, "needParametersClass")
                .addStatement("$T oClass = null", Class.class)
                .beginControlFlow("try")
                .addStatement("android.util.Log.i($S, $S + className)", "test", "className =")
                .addStatement("android.util.Log.i($S, $S + oMethodName)", "test", "oMethodName =")
                .addStatement("oClass = Class.forName(className)")
                .endControlFlow()
                .beginControlFlow("catch( $T e)", Exception.class)
                .addStatement("e.printStackTrace()")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T oMethod = null", Method.class)
                .beginControlFlow("try")
                .addStatement("oMethod = oClass.getMethod(oMethodName,needParametersClass)")
                .endControlFlow()
                .beginControlFlow("catch( $T e)", Exception.class)
                .addStatement("e.printStackTrace()")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T nMethod = null", Method.class)
                .beginControlFlow("try")
                .addStatement("nMethod = dClass.getMethod(nMethodName,needParametersClass)")
                .endControlFlow()
                .beginControlFlow("catch( $T e)", Exception.class)
                .addStatement("e.printStackTrace()")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$N.hook(oMethod,nMethod)", cHook);
        mappingJava.addMethod(methodSpec.build());
        methodSpec = MethodSpec.methodBuilder("doMapping").addModifiers(Modifier.PUBLIC).addModifiers(Modifier.STATIC);
        methodSpec.addStatement("Class dClass = SmartRun2Methods.class");


        for (EMethod eMethod : eMethods) {
            String needClass = "";
            List<? extends VariableElement> parameters = (List<? extends VariableElement>) ((ExecutableElement) eMethod.element).getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                VariableElement p = parameters.get(i);
                needClass += ", " + p.asType() + ".class";
            }
            String oClassName = eMethod.element.getEnclosingElement().toString();
            note(eMethod.element.getEnclosingElement().toString());
            methodSpec.addStatement("replace($S,$S,$S$N)", oClassName, eMethod.oMethodName, eMethod
                    .nMethodName, needClass);
        }
        mappingJava.addMethod(methodSpec.build());

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile("SmartRun2Mapping");
            Writer writer = source.openWriter();
            String fileName = mElementUtils.getPackageOf(eMethods.get(0).element).getQualifiedName().toString();
            JavaFile javaFile = JavaFile.builder(fileName, mappingJava.build())
                    .build();
            javaFile.writeTo(writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            note(e.getMessage());
        }

        return true;
    }

    public ClassName getClassName(String type) {
        String packageStr = type.contains(".") ? type.substring(0, type.lastIndexOf(".")) : "";
        String simpleStr = type.contains(".") ? type.substring(type.lastIndexOf(".") + 1) : type;
        return ClassName.get(packageStr, simpleStr);
    }

    /**
     * 指定哪些注解应该被注解处理器注册
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(cUIThread);
        types.add(cBGThread);
        types.add(cAsyncThread);
        return types;
    }

    /**
     * 用来指定你使用的 java 版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    private boolean isValid(Class<? extends Annotation> annotationClass, String targetThing, Element element) {
        boolean isVaild = true;

        //获取变量的所在的父元素，肯能是类、接口、枚举
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        //父元素的全限定名
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        // 所在的类不能是private或static修饰
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isVaild = false;
        }

        // 父元素必须是类，而不能是接口或枚举
        if (enclosingElement.getKind() != ElementKind.CLASS) {
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isVaild = false;
        }

        //不能在Android框架层注解
        if (qualifiedName.startsWith("android.")) {
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }
        //不能在java框架层注解
        if (qualifiedName.startsWith("java.")) {
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }

        return isVaild;
    }

    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, getClass().getName() + "   " + String.format(msg, args), e);
//        mMessager.printMessage(Diagnostic.Kind.NOTE, getClass().getName()+"   "+String.format(msg, args));
    }

    private void note(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, getClass().getName() + "   " + String.format(msg, args));
    }

    private class EMethod {
        /**
         * 新方法所在整体标号id
         */
        int index;
        /**
         * 原方法名
         */
        String oMethodName;
        /**
         * 替换后的方法名
         */
        String nMethodName;

        /**
         * 运行线程类型
         */
        String threadStyle;

        Element element;

        /**
         * 原方法所在的类名
         */
        String oClassName;


        public String generateNMethodName() {
            nMethodName = "m" + index;
            return nMethodName;
        }
    }
}

