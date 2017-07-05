package com.by122006;

import com.by122006.annotation.Subclass;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

@AutoService(Processor.class)
public class SubclassAttributeProcessor extends javax.annotation.processing.AbstractProcessor {
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


    /**
     * 处理器的主方法，用于扫描处理注解，生成java文件
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        note("process");
        ArrayList<ClassName> all = new ArrayList<>();
        //处理被注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(Subclass.class)) {
            String className = element.getSimpleName().toString() + "_Attribute";
            ClassName attClassName = getClassName(element.getEnclosingElement().toString() + "." + element
                    .getSimpleName().toString() + "_Attribute");
            ClassName oriClassName = getClassName(element.getEnclosingElement().toString() + "." + element
                    .getSimpleName().toString());
            note(className);
            ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();

//            if (!isValid(Subclass.class, "fields", element)) {
//                continue;
//            }
            if (element.getEnclosingElement().getKind() == ElementKind.CLASS && className == null)
                className = element.getEnclosingElement().getSimpleName().toString();
            all.add(attClassName);
            List<? extends AnnotationMirror> list = (List<? extends AnnotationMirror>) element.getAnnotationMirrors();

            for (AnnotationMirror mirror : list) {
                if (!mirror.getAnnotationType().asElement().getSimpleName().toString().equals(Subclass.class
                        .getSimpleName()))
                    continue;

                for (ExecutableElement attEle : mirror.getElementValues().keySet()) {
                    if (attEle.getSimpleName().toString().equals("att")) {
                        AnnotationValue att = mirror.getElementValues().get(attEle);
                        String data = att.getValue().toString();
                        String[] d = data.split(",@");
                        for (String f : d) {
                            int namep = f.indexOf("name");
                            String name = f.substring(namep + 6, f.indexOf("\"", namep + 7));
                            int typep = f.indexOf("type");
                            String type = f.substring(typep + 5, f.indexOf(",", typep + 5));
                            type = type.substring(0, type.lastIndexOf("."));
                            if (type.equals(int.class.toString())) type = Integer.class.toString();
                            if (type.equals(double.class.toString())) type = Double.class.toString();
                            if (type.equals(long.class.toString())) type = Long.class.toString();
                            if (type.equals(float.class.toString())) type = Float.class.toString();
                            if (type.equals(boolean.class.toString())) type = Boolean.class.toString();
                            if (type.equals(byte.class.toString())) type = Byte.class.toString();
                            if (type.equals(char.class.toString())) type = Character.class.toString();
                            if (type.startsWith("class ")) type = type.substring(6);
                            int defaultValuep = f.indexOf("defaultValue");
                            String defaultValue = f.substring(defaultValuep + 14, f.indexOf("\"", defaultValuep + 15));
                            HashMap<String, Object> m = new HashMap<String, Object>();
                            m.put("name", name);
                            m.put("type", type);
                            m.put("defaultValue", defaultValue);
                            arrayList.add(m);
                        }

                    }
                }
            }

            TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC).addSuperinterface(getClassName("com.by122006library.Interface" +
                            ".NoProguard_All"));
            MethodSpec.Builder methodGZ = MethodSpec.constructorBuilder()
                    .addParameter(oriClassName, "obj")
                    .addStatement("this.obj=obj");
            methodGZ.addModifiers(Modifier.PUBLIC);
            builder.addMethod(methodGZ.build());
            FieldSpec.Builder fieldSpec = FieldSpec.builder(oriClassName,
                    "obj")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            builder.addField(fieldSpec.build());
            for (HashMap map : arrayList) {
                try {
                    fieldSpec = FieldSpec.builder(String.class,
                            String.valueOf(map.get("name")))
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                            .initializer(String.format("\"%s\"", String.valueOf(map.get("name"))));
                    builder.addField(fieldSpec.build());

                    MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("set" + toUpperCaseFirstOne(String
                            .valueOf(map.get("name"))))
                            .addParameter(oriClassName, "obj")
                            .addParameter(getClassName(String.valueOf(map.get("type"))), "value")
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .addStatement("Class clazz = obj.getClass()")
                            .addStatement("$T field = null", Field.class)
                            .beginControlFlow("try")
                            .addStatement("field = clazz.getDeclaredField($S)", String.valueOf(map.get("name")))
                            .addStatement("field.setAccessible(true)")
                            .addStatement("field.set(obj, value )")
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addJavadoc("设置继承子类中的$T $N常量", getClassName(String.valueOf(map.get("type"))), String
                                    .valueOf(map.get("name")))
                            .returns(void.class);
                    builder.addMethod(methodSpec.build());
                    methodSpec = MethodSpec.methodBuilder("set" + toUpperCaseFirstOne(String
                            .valueOf(map.get("name"))))
                            .addParameter(getClassName(String.valueOf(map.get("type"))), "value")
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addStatement("Class clazz = obj.getClass()")
                            .addStatement("$T field = null", Field.class)
                            .beginControlFlow("try")
                            .addStatement("field = clazz.getDeclaredField($S)", String.valueOf(map.get("name")))
                            .addStatement("field.setAccessible(true)")
                            .addStatement("field.set(obj, value )")
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addJavadoc("设置继承子类中的$T $N常量", getClassName(String.valueOf(map.get("type"))), String
                                    .valueOf(map.get("name")))
                            .returns(void.class);
                    builder.addMethod(methodSpec.build());
                    methodSpec = MethodSpec.methodBuilder("init" + toUpperCaseFirstOne(String
                            .valueOf(map.get("name"))))
                            .addParameter(oriClassName, "obj")
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .addStatement("Class clazz = obj.getClass()")
                            .addStatement("$T field = null", Field.class)
                            .beginControlFlow("try")
                            .addStatement("field = clazz.getDeclaredField($S)", String.valueOf(map.get("name")))
                            .addStatement("field.setAccessible(true)")
                            .addStatement("field.set( obj , " + (String.valueOf(map.get("type")).equals(String.class
                                    .getName())
                                    ? "$S" : "$N") + " )", String.valueOf(map.get
                                    ("defaultValue")))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addJavadoc("继承子类中的$T $N常量设置为默认值", getClassName(String.valueOf(map.get("type"))), String
                                    .valueOf(map.get("name")))
                            .returns(void.class);
                    builder.addMethod(methodSpec.build());
                    methodSpec = MethodSpec.methodBuilder("init" + toUpperCaseFirstOne(String
                            .valueOf(map.get("name"))))
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addStatement("Class clazz = obj.getClass()")
                            .addStatement("$T field = null", Field.class)
                            .beginControlFlow("try")
                            .addStatement("field = clazz.getDeclaredField($S)", String.valueOf(map.get("name")))
                            .addStatement("field.setAccessible(true)")
                            .addStatement("field.set( obj , " + (String.valueOf(map.get("type")).equals(String.class
                                    .getName())
                                    ? "$S" : "$N") + " )", String.valueOf(map.get
                                    ("defaultValue")))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addJavadoc("继承子类中的$T $N常量设置为默认值", getClassName(String.valueOf(map.get("type"))), String
                                    .valueOf(map.get("name")))
                            .returns(void.class);
                    builder.addMethod(methodSpec.build());
                    methodSpec = MethodSpec.methodBuilder("get" + toUpperCaseFirstOne(String
                            .valueOf(map.get("name"))))
                            .addParameter(oriClassName, "obj")
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .addStatement("Class clazz = obj.getClass()")
                            .addStatement("$T field = null", Field.class)
                            .beginControlFlow("try")
                            .addStatement("field = clazz.getDeclaredField($S)", String.valueOf(map.get("name")))
                            .addStatement("field.setAccessible(true)")
                            .addStatement("return ($T) field.get(obj)", getClassName(String.valueOf(map.get("type"))))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")

                            .endControlFlow()
                            .addStatement("return " + (String.valueOf(map.get("type")).equals(String.class.getName())
                                    ? "$S" : "$N"), String.valueOf(map.get("defaultValue")))
                            .addJavadoc("获得继承子类中的$T $N常量", getClassName(String.valueOf(map.get("type"))), String
                                    .valueOf(map.get("name")))
                            .returns(getClassName(String.valueOf(map.get("type"))));
                    builder.addMethod(methodSpec.build());
                    methodSpec = MethodSpec.methodBuilder("get" + toUpperCaseFirstOne(String
                            .valueOf(map.get("name"))))
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addStatement("Class clazz = obj.getClass()")
                            .addStatement("$T field = null", Field.class)
                            .beginControlFlow("try")
                            .addStatement("field = clazz.getDeclaredField($S)", String.valueOf(map.get("name")))
                            .addStatement("field.setAccessible(true)")
                            .addStatement("return ($T) field.get(obj)", getClassName(String.valueOf(map.get("type"))))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addStatement("return " + (String.valueOf(map.get("type")).equals(String.class.getName())
                                    ? "$S" : "$N"), String.valueOf(map.get("defaultValue")))
                            .addJavadoc("获得继承子类中的$T $N常量", getClassName(String.valueOf(map.get("type"))), String
                                    .valueOf(map.get("name")))
                            .returns(getClassName(String.valueOf(map.get("type"))));
                    builder.addMethod(methodSpec.build());
                } catch (Exception e) {
                    error(element, map.get("type") + e.getStackTrace()[0].toString());
                }
            }
            TypeSpec java = builder.build();

            try {
                JavaFileObject source = processingEnv.getFiler().createSourceFile(element.getEnclosingElement()
                        .toString() + "." + className);

                Writer writer = source.openWriter();
                JavaFile javaFile = JavaFile.builder(element.getEnclosingElement().toString(), java)
                        .build();
                javaFile.writeTo(writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
//                note(e.getMessage());
            }
        }
//        HashMap<String,ArrayList<ClassName>> listHashMap=new HashMap<String,ArrayList<ClassName>>();
//        for(ClassName clazzName :all){
//            ArrayList<ClassName> list=listHashMap.get(clazzName.packageName());
//        }
        note("准备封装 SubclassAttribute");
        TypeSpec.Builder builder = TypeSpec.classBuilder("SubclassAttribute")
                .addModifiers(Modifier.PUBLIC);
//        builder.addJavadoc("update time : $N",new Date(System.currentTimeMillis()).toString());
        for (ClassName closingName : all) {note("封装 %s",closingName.toString());
            try {
                MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("with")
                        .addParameter(getClassName(closingName.toString().substring(0, closingName.toString().length
                                () - "_Arrtribute".length() + 1)), "obj")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                        .addStatement("$T attClass=new $T(obj);", closingName, closingName)
                        .addStatement("return attClass")
                        .addJavadoc("获得 $S 基于 obj 的实例", closingName.simpleName())
                        .returns(closingName);
                builder.addMethod(methodSpec.build());
            } catch (Exception e) {
                note(e.getMessage() + e.getStackTrace()[0].toString());
            }
        }
        if (all.size() != 0) {
            TypeSpec java = builder.build();
            try {
                String packageName=all.get(0).toString();
                try {
                    int p=all.get(0).toString().indexOf(".");
                    p=all.get(0).toString().indexOf(".",p+1);
                    packageName=all.get(0).toString().substring(0,p);
                } catch (Exception e) {
                    packageName=all.get(0).toString();
                }
                JavaFileObject source = processingEnv.getFiler().createSourceFile( packageName +
                        ".SubclassAttribute");
                Writer writer = source.openWriter();
                JavaFile.Builder javaFile = JavaFile.builder(packageName, java);

                javaFile.build().writeTo(writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
//                note(e.getMessage());
            }
        }
//
//
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
        types.add(Subclass.class.getName());
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
        mMessager.printMessage(Diagnostic.Kind.ERROR, getClass().getName()+"   "+String.format(msg, args), e);
//        mMessager.printMessage(Diagnostic.Kind.NOTE, getClass().getName()+"   "+String.format(msg, args));
    }

    private void note(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE,getClass().getName()+"   "+ String.format(msg, args));
    }
}

