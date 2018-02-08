package com.by122006.buildsrc;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

public class SmartRunClassVisitor extends ClassVisitor {
    ArrayList<NeedCreateMethod> needCreateMethods = new ArrayList<>();
    File file;
    int index = 0;
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

    public SmartRunClassVisitor setFile(File file) {
        this.file = file;
        return this;
    }


    public String createInnerClass(String oPackageClassName,String signature, String threadStyle, boolean isStatic, long outTime, String oObjType, String doMethodName) {

        System.out.println(file.getAbsolutePath());
        String allClassPath = file.getAbsolutePath().replace(".class", "") + "$Thread_" + doMethodName.replace("$","_") + "_" + index + ".class";
        String className = allClassPath.substring(allClassPath.lastIndexOf("\\") + 1, allClassPath.lastIndexOf("."));
        String returnStyle = signature.substring(signature.lastIndexOf(")") + 1);
        boolean needReturn=!returnStyle.equals("V");
        String oObjClassName = oObjType.substring(1, oObjType.length() - 1);
        String packageClassName=oPackageClassName.substring(0, oPackageClassName.lastIndexOf("/") + 1)+className;


        String arg = signature.substring(1, signature.lastIndexOf(")"));
        if (arg.endsWith(";")) arg = arg.substring(0, arg.length());
        String[] args = arg.split(";");
        if (args.length==1&&args[0].length()==0) args=new String[]{};
        for (int i = 0; i < args.length; i++) {
            args[i] += args[i].length() == 1 ? "" : ";";
            System.out.println(i+" : "+args[i]);
        }
        System.out.println("args.length : "+args.length);
        index++;

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(V1_7, ACC_PROTECTED + ACC_SUPER,packageClassName , null, "java/lang/Object", new String[]{"java/lang/Runnable"});

        cw.visitOuterClass(oPackageClassName, doMethodName, signature);

        fv = cw.visitField(0, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;", null, null);
        fv.visitEnd();
        if (needReturn) {
            fv = cw.visitField(0, "result", returnStyle, null, null);
            fv.visitEnd();
        }

        fv = cw.visitField(ACC_PRIVATE, "obj", oObjType, null, null);
        fv.visitEnd();


        for (int i = 0; i < args.length; i++) {
            fv = cw.visitField(ACC_PRIVATE, "o" + i, args[i], null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
            mv.visitCode();
            if (needReturn)
                mv.visitVarInsn(ALOAD, 0);
            if (isStatic) {
                for (int i = 0; i < args.length; i++) {
                    mv.visitVarInsn(ALOAD, 0);
                    System.out.println(args[i]);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "o" + i, args[i]);
                }
                mv.visitMethodInsn(INVOKESTATIC, oObjClassName, doMethodName, signature, false);
            } else {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, packageClassName, "obj", oObjType);
                for (int i = 0; i < args.length; i++) {
                    mv.visitVarInsn(ALOAD, 0);
                    System.out.println(args[i]);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "o" + i, args[i]);
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, oObjClassName, doMethodName, signature, false);
            }
            if (needReturn) {
                mv.visitFieldInsn(PUTFIELD, packageClassName, "result", returnStyle);
            }
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, packageClassName, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/CountDownLatch", "countDown", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "action", "(" + oObjType + signature.substring(1), null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/InterruptedException");

            //设置对象
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, packageClassName, "obj", oObjType);

            for (int i = 0; i < args.length; i++) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, i + 2);
                mv.visitFieldInsn(PUTFIELD, packageClassName, "o" + i, args[i]);
            }


            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/concurrent/CountDownLatch");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/concurrent/CountDownLatch", "<init>", "(I)V", false);
            mv.visitFieldInsn(PUTFIELD, packageClassName, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "com/by122006library/ThreadManager", "post" + threadStyle.toUpperCase() + "Thread", "(Ljava/lang/Runnable;)V", false);
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, packageClassName, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;");
            mv.visitLdcInsn(new Long(outTime));
            mv.visitFieldInsn(GETSTATIC, "java/util/concurrent/TimeUnit", "MILLISECONDS", "Ljava/util/concurrent/TimeUnit;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/CountDownLatch", "await", "(JLjava/util/concurrent/TimeUnit;)Z", false);
            mv.visitInsn(POP);
            mv.visitLabel(l1);
            Label l3 = new Label();
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/InterruptedException"});
            mv.visitVarInsn(ASTORE, args.length + 2 + 1);
            mv.visitVarInsn(ALOAD, args.length + 2 + 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/InterruptedException", "printStackTrace", "()V", false);
            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            if (!needReturn) {
                mv.visitInsn(RETURN);
            } else {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, packageClassName, "result", returnStyle);
                if (returnStyle.endsWith("Z")) mv.visitInsn(Opcodes.DRETURN);
                else if (returnStyle.endsWith("B")) mv.visitInsn(Opcodes.IRETURN);
                else if (returnStyle.endsWith("C")) mv.visitInsn(Opcodes.IRETURN);
                else if (returnStyle.endsWith("S")) mv.visitInsn(Opcodes.IRETURN);
                else if (returnStyle.endsWith("I")) mv.visitInsn(Opcodes.IRETURN);
                else if (returnStyle.endsWith("J")) mv.visitInsn(Opcodes.LRETURN);
                else if (returnStyle.endsWith("S")) mv.visitInsn(Opcodes.IRETURN);
                else if (returnStyle.endsWith("F")) mv.visitInsn(Opcodes.FRETURN);
                else if (returnStyle.endsWith("D")) mv.visitInsn(Opcodes.DRETURN);
                else mv.visitInsn(ARETURN);
            }
//            mv.visitInsn(RETURN);
            mv.visitMaxs(2 + args.length, 9 + args.length);
            mv.visitEnd();
        }
        cw.visitEnd();
        try {
            FileOutputStream fos = new FileOutputStream(allClassPath);
            fos.write(cw.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return className;
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
        public void visitMethodInsn(int i, String s, String s1, String s2, boolean b) {
            if (!name.contains("$SmartRun_") && i == INVOKESTATIC && s.equals("com/by122006library/Utils/ThreadUtils") && (s1.equals("toUiThread") || s1.equals("toBgThread")) && !annotation.toLowerCase().contains("thread")) {
                System.out.println("!!!!method code : " + s1);
                System.out.println(String.format("access=%d,name=%s,desc=%s,signature=%s", access, name, desc, signature));

                String style = s1.toLowerCase().contains("ui") ? "UI" : "BG";
                ov = mv;
                mv = cv.visitMethod(access, name + "$SmartRun_" + style, desc, signature, exceptions);
            }
            super.visitMethodInsn(i, s, s1, s2, b);


        }

        @Override
        public void visitEnd() {
            if (ov != null) {
                String style = annotation.toLowerCase().contains("uithread") ? "UI" : "BG";

                String arg = desc.substring(1, desc.lastIndexOf(")"));
                if (arg.endsWith(";")) arg = arg.substring(0, arg.length());
                String[] args = arg.split(";");
                if (args.length==1&&args[0].length()==0) args=new String[]{};
                for (int i = 0; i < args.length; i++) {
                    args[i] += args[i].length() == 1 ? "" : ";";
                    System.out.println(i+" : "+args[i]);
                }
                System.out.println("args.length : "+args.length);

                int num = args.length;
                System.out.println(packageClassName);
                System.out.println("Flag_Static:" + Flag_Static);
                ov.visitCode();
                ov.visitMethodInsn(INVOKESTATIC, "com/by122006library/Utils/ThreadUtils", "is" + style + "Thread", "()Z", false);
                Label l0 = new Label();
                ov.visitJumpInsn(Opcodes.IFEQ, l0);
                if (!Flag_Static)
                    ov.visitVarInsn(ALOAD, 0);
                if (num > 0) {
                    for (int i = 0; i < num; i++) {
                        int pa = ALOAD;
                        if (args[i].length() > 1) {
                            pa = ALOAD;
                        } else {
                            if (args[i].equals("Z")) pa = Opcodes.DLOAD;
                            else if (args[i].equals("B")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("C")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("S")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("I")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("J")) pa = Opcodes.LLOAD;
                            else if (args[i].equals("S")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("F")) pa = Opcodes.FLOAD;
                            else if (args[i].equals("D")) pa = Opcodes.FLOAD;
                        }
                        ov.visitVarInsn(pa, Flag_Static ? i : i + 1);
                    }
                }
                if (Flag_Static) {
                    ov.visitMethodInsn(INVOKESTATIC, packageClassName, name + "$SmartRun_" + style, desc, false);
                } else
                    ov.visitMethodInsn(INVOKEVIRTUAL, packageClassName, name + "$SmartRun_" + style, desc, false);
                Label l1 = new Label();
                ov.visitJumpInsn(GOTO, l1);
                ov.visitLabel(l0);
                ov.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                String newClassName = packageClassName.substring(0, packageClassName.lastIndexOf("/") + 1) + createInnerClass(packageClassName,desc, style, Flag_Static, 2000, "L" + packageClassName + ";", name + "$SmartRun_" + style);
                visitInnerClass(newClassName, null, null, 0);
                System.out.println("newClassName:"+newClassName);

                ov.visitTypeInsn(NEW, newClassName);
                ov.visitInsn(Opcodes.DUP);
                ov.visitMethodInsn(INVOKESPECIAL, newClassName, "<init>", "()V", false);

                if (!Flag_Static) {
                    ov.visitVarInsn(ALOAD, 0);
                } else {
                    ov.visitInsn(Opcodes.ACONST_NULL);
                }
                if (num > 0) {
                    for (int i = 0; i < num; i++) {
                        int pa = ALOAD;
                        System.out.println(args[i]);
                        if (args[i].length() > 1) {
                            pa = ALOAD;
                        } else {
                            if (args[i].equals("Z")) pa = Opcodes.DLOAD;
                            else if (args[i].equals("B")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("C")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("S")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("I")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("J")) pa = Opcodes.LLOAD;
                            else if (args[i].equals("S")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("F")) pa = Opcodes.FLOAD;
                            else if (args[i].equals("D")) pa = Opcodes.FLOAD;
                        }
                        ov.visitVarInsn(pa, Flag_Static ? i : i + 1);
                    }
                }
                ov.visitMethodInsn(INVOKEVIRTUAL, newClassName, "action", "(" + "L" + packageClassName + ";" + desc.substring(1), false);


//                if (Flag_Static) {
//                    ov.visitLdcInsn(Type.getType("L" + packageClassName + ";"));
//                } else
//                    ov.visitVarInsn(ALOAD, 0);
//                ov.visitLdcInsn(name);
//                ov.visitIntInsn(Opcodes.SIPUSH, num);
//                ov.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
//                if (num > 0) {
//                    ov.visitInsn(DUP);
//                    for (int i = 0; i < num; i++) {
//                        ov.visitIntInsn(Opcodes.SIPUSH, i);
//                        ov.visitVarInsn(ALOAD, Flag_Static ? i : i + 1);
//                        ov.visitInsn(Opcodes.AASTORE);
//                        if (i != num - 1) ov.visitInsn(DUP);
//                    }
//                }


                ov.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                ov.visitLabel(l1);

                if (desc.endsWith("V"))
                    ov.visitInsn(RETURN);
                else if (desc.endsWith("Z")) ov.visitInsn(Opcodes.DRETURN);
                else if (desc.endsWith("B")) ov.visitInsn(Opcodes.IRETURN);
                else if (desc.endsWith("C")) ov.visitInsn(Opcodes.IRETURN);
                else if (desc.endsWith("S")) ov.visitInsn(Opcodes.IRETURN);
                else if (desc.endsWith("I")) ov.visitInsn(Opcodes.IRETURN);
                else if (desc.endsWith("J")) ov.visitInsn(Opcodes.LRETURN);
                else if (desc.endsWith("S")) ov.visitInsn(Opcodes.IRETURN);
                else if (desc.endsWith("F")) ov.visitInsn(Opcodes.FRETURN);
                else if (desc.endsWith("D")) ov.visitInsn(Opcodes.FRETURN);
                else ov.visitInsn(ARETURN);
//                ov.visitInsn(RETURN);
                ov.visitMaxs(20+num, 20+num);
                ov.visitEnd();
                return;
            }
            super.visitEnd();
        }
    }

}