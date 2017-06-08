package com.by122006;

import com.by122006.annotation.Subclass;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
    private Filer mFiler; //�ļ���ع�����
    private Elements mElementUtils; //Ԫ����صĹ�����
    private Messager mMessager; //��־��صĹ�����

    //    }
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    //����ĸת��д
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * �������ĳ�ʼ�����������Ի�ȡ��صĹ�����
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }


    /**
     * ��������������������ɨ�账��ע�⣬����java�ļ�
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        note("process");
        ArrayList<ClassName> all = new ArrayList<>();
        //����ע���Ԫ��
        for (Element element : roundEnv.getElementsAnnotatedWith(Subclass.class)) {
            String className = element.getSimpleName().toString() + "_Attribute";
            ClassName attClassName=getClassName(element.getEnclosingElement().toString()+"."+element.getSimpleName().toString() + "_Attribute");
            ClassName oriClassName=getClassName(element.getEnclosingElement().toString()+"."+element.getSimpleName().toString());
            note(className);
            ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();

//            if (!isValid(Subclass.class, "fields", element)) {
//                continue;
//            }
            if (element.getEnclosingElement().getKind() == ElementKind.CLASS && className == null)
                className = element.getEnclosingElement().getSimpleName().toString();
            all.add(attClassName);
            List<AnnotationMirror> list = (List<AnnotationMirror>) element.getAnnotationMirrors();

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
                    .addModifiers(Modifier.PUBLIC).addSuperinterface(getClassName("com.by122006library.Interface.NoConfusion_All"));
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
                            .addStatement("field.set($S, value )", String.valueOf(map.get("name")))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addJavadoc("���ü̳������е�$T $N����",getClassName(String.valueOf(map.get("type"))), String
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
                            .addStatement("field.set($S, value )", String.valueOf(map.get("name")))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addJavadoc("���ü̳������е�$T $N����",getClassName(String.valueOf(map.get("type"))), String
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
                            .addStatement("field.set( $S , " + (String.valueOf(map.get("type")) .equals(String.class.getName())
                                    ? "$S" : "$N") + " )", String.valueOf(map.get("name")), String.valueOf(map.get
                                    ("defaultValue")))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addJavadoc("�̳������е�$T $N��������ΪĬ��ֵ",getClassName(String.valueOf(map.get("type"))), String
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
                            .addStatement("field.set( $S , " + (String.valueOf(map.get("type")) .equals(String.class.getName())
                                    ? "$S" : "$N") + " )", String.valueOf(map.get("name")), String.valueOf(map.get
                                    ("defaultValue")))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addJavadoc("�̳������е�$T $N��������ΪĬ��ֵ",getClassName(String.valueOf(map.get("type"))), String
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
                            .addStatement("return ($T) field.get(obj)",getClassName(String.valueOf(map.get("type"))))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addStatement("return " + (String.valueOf(map.get("type")) .equals(String.class.getName())
                                    ? "$S" : "$N"), String.valueOf(map.get("defaultValue")))
                            .addJavadoc("��ü̳������е�$T $N����",getClassName(String.valueOf(map.get("type"))), String
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
                            .addStatement("return ($T) field.get(obj)",getClassName(String.valueOf(map.get("type"))))
                            .endControlFlow()
                            .beginControlFlow(" catch (Exception e)")
                            .addStatement("e.printStackTrace()")
                            .endControlFlow()
                            .addStatement("return " + (String.valueOf(map.get("type")) .equals(String.class.getName()) 
                                    ? "$S" : "$N"), String.valueOf(map.get("defaultValue")))
                            .addJavadoc("��ü̳������е�$T $N����",getClassName(String.valueOf(map.get("type"))), String
                                    .valueOf(map.get("name")))
                            .returns(getClassName(String.valueOf(map.get("type"))));
                    builder.addMethod(methodSpec.build());
                } catch (Exception e) {
                    error(element, map.get("type")  + e.getStackTrace()[0].toString());
                }
            }
            TypeSpec java = builder.build();

            try {
                JavaFileObject source = processingEnv.getFiler().createSourceFile(element.getEnclosingElement().toString()+"."+className);

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
        TypeSpec.Builder builder = TypeSpec.classBuilder("SubclassAttribute")
                .addModifiers(Modifier.PUBLIC);
        for (ClassName closingName : all) {

            try {
                MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("with")
                        .addParameter(getClassName(closingName.toString().substring(0,closingName.toString().length()-"_Arrtribute".length()+1)), "obj")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                        .addStatement("$T attClass=new $T(obj);", closingName,closingName)
                        .addStatement("return attClass")
                        .addJavadoc("��� $S ���� obj ��ʵ��", closingName.simpleName())
                        .returns(closingName);
                builder.addMethod(methodSpec.build());
            } catch (Exception e) {
                note(e.getMessage() + e.getStackTrace()[0].toString());
            }
        }
        TypeSpec java = builder.build();

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.by122006library.Function.SubclassAttribute");
            Writer writer = source.openWriter();
            JavaFile.Builder javaFile = JavaFile.builder("com.by122006library.Function", java);

            javaFile.build().writeTo(writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
//                note(e.getMessage());
        }
//
//
        return true;
    }
    
    public ClassName getClassName(String type){
        String packageStr=type.contains(".")?type.substring(0,type.lastIndexOf(".")):"";
        String simpleStr=type.contains(".")?type.substring(type.lastIndexOf(".")+1):type;
        return ClassName.get(packageStr,simpleStr);
    }



    /**
     * ָ����Щע��Ӧ�ñ�ע�⴦����ע��
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Subclass.class.getName());
        return types;
    }

    /**
     * ����ָ����ʹ�õ� java �汾
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean isValid(Class<? extends Annotation> annotationClass, String targetThing, Element element) {
        boolean isVaild = true;

        //��ȡ���������ڵĸ�Ԫ�أ��������ࡢ�ӿڡ�ö��
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        //��Ԫ�ص�ȫ�޶���
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        // ���ڵ��಻����private��static����
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isVaild = false;
        }

        // ��Ԫ�ر������࣬�������ǽӿڻ�ö��
        if (enclosingElement.getKind() != ElementKind.CLASS) {
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isVaild = false;
        }

        //������Android��ܲ�ע��
        if (qualifiedName.startsWith("android.")) {
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }
        //������java��ܲ�ע��
        if (qualifiedName.startsWith("java.")) {
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }

        return isVaild;
    }

    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void note(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}

