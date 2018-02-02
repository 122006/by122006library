package com.by122006.buildsrc;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;

public class SmartRunClassVisitor extends ClassVisitor {
    ArrayList<NeedCreateMethod> needCreateMethods = new ArrayList<>();
    private String className;
    private String packageClassName;


    public SmartRunClassVisitor(String className, ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, final String desc, String signature,
                                     String[] exceptions) {
        try {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            MyAdviceAdapter av;
            av = new MyAdviceAdapter(Opcodes.ASM5, mv, access, name, desc, signature, exceptions);
            mv = av;
            return mv;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);

    }

    @Override
    public void visitEnd() {
//        for(NeedCreateMethod needCreateMethod:needCreateMethods){
//            needCreateMethod.createMethod();
//        }
        super.visitEnd();
    }

    public SmartRunClassVisitor setPackageClassName(String packageClassName) {
        this.packageClassName = packageClassName;
        return this;
    }

    protected class NeedCreateMethod {
        int access;
        String name;
        String desc;
        String signature;
        String[] exceptions;
        String style;

        NeedCreateMethod(int access, String name, String desc, String signature,
                         String[] exceptions, String style) {
            this.name = name;
            this.desc = desc;
            this.access = access;
            this.signature = signature;
            this.exceptions = exceptions;
            this.style = style;
        }

    }

    public class MyAdviceAdapter extends AdviceAdapter {
        String annotation = "";
        int access;
        String name;
        String desc;
        String signature;
        String[] exceptions;
        MethodVisitor ov;
        boolean Flag_Static = false;

        protected MyAdviceAdapter(int i, MethodVisitor methodVisitor, int access, String name, String desc, String signature,
                                  String[] exceptions) {
            super(i, methodVisitor, access, name, desc);
            this.name = name;
            this.desc = desc;
            this.access = access;
            this.signature = signature;
            this.exceptions = exceptions;
            Flag_Static = (access & ACC_STATIC) != 0;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String s, boolean b) {
            annotation = s == null ? "" : s;
            if (name.equals("doCycleAction")) return null;
            if (!name.contains("$SmartRun_") && (annotation.toLowerCase().contains("uithread") || annotation.toLowerCase().contains("bgthread"))) {
                System.out.println("!!!!" + annotation);
                System.out.println(String.format("access=%d,name=%s,desc=%s,signature=%s", access, name, desc, signature));
                String style = annotation.toLowerCase().contains("uithread") ? "UI" : "BG";
//                needCreateMethods.add(new NeedCreateMethod(access,name,desc,signature,exceptions,style));

                ov = mv;
                mv = cv.visitMethod(access, name + "$SmartRun_" + style, desc, signature, exceptions);
            }

            return super.visitAnnotation(s, b);
        }


        @Override
        public void visitEnd() {
            if (ov != null) {
                String style = annotation.toLowerCase().contains("uithread") ? "UI" : "BG";
                int num = desc.split(";").length - 1;
                System.out.println(packageClassName);
                System.out.println("Flag_Static:" + Flag_Static);
                ov.visitCode();
                ov.visitMethodInsn(Opcodes.INVOKESTATIC, "com/by122006library/Utils/ThreadUtils", "is" + style + "Thread", "()Z", false);
                Label l0 = new Label();
                ov.visitJumpInsn(Opcodes.IFEQ, l0);
                if (!Flag_Static)
                    ov.visitVarInsn(Opcodes.ALOAD, 0);
                for (int i = 0; i < num; i++) {
                    ov.visitVarInsn(Opcodes.ALOAD, Flag_Static ? i : i + 1);
                }
                if (Flag_Static) {
                    ov.visitMethodInsn(Opcodes.INVOKESTATIC, packageClassName, name + "$SmartRun_" + style, desc, false);
                } else
                    ov.visitMethodInsn(Opcodes.INVOKEVIRTUAL, packageClassName, name + "$SmartRun_" + style, desc, false);
                Label l1 = new Label();
                ov.visitJumpInsn(Opcodes.GOTO, l1);
                ov.visitLabel(l0);
                ov.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                ov.visitMethodInsn(Opcodes.INVOKESTATIC, "com/by122006library/ThreadManager", "getInstance", "()Lcom/by122006library/ThreadManager;", false);

                if (Flag_Static) {
                    ov.visitLdcInsn(Type.getType("L" + packageClassName + ";"));
                } else
                    ov.visitVarInsn(Opcodes.ALOAD, 0);
                ov.visitLdcInsn(name);
                ov.visitIntInsn(Opcodes.SIPUSH, num);
                ov.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
                if (num > 0) {
                    ov.visitInsn(Opcodes.DUP);
                    for (int i = 0; i < num; i++) {
                        ov.visitIntInsn(Opcodes.SIPUSH, i);
                        ov.visitVarInsn(Opcodes.ALOAD, Flag_Static ? i : i + 1);
                        ov.visitInsn(Opcodes.AASTORE);
                        if (i != num - 1) ov.visitInsn(Opcodes.DUP);
                    }
                }
                ov.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/by122006library/ThreadManager", "post" + style + "Thread", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V", false);


                ov.visitLabel(l1);
                ov.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                ov.visitInsn(Opcodes.RETURN);
                ov.visitMaxs(1 + num, 7 + num);
                ov.visitEnd();
                return;
            }
            super.visitEnd();
        }
    }
}