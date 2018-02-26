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
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
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


    /**
     * @param oPackageClassName
     * @param signature
     * @param threadStyle
     * @param isStatic
     * @param oObjType
     * @param doMethodName
     * @param newThread
     * @param outTime
     * @param result            强制返回
     * @return
     */
    public String createInnerClass(String oPackageClassName, String signature, String threadStyle, boolean isStatic, String oObjType, String doMethodName, boolean newThread, long outTime, boolean result) {

        System.out.println(file.getAbsolutePath());
        String allClassPath = file.getAbsolutePath().replace(".class", "") + "$Thread_" + doMethodName.replace("$", "_") + "_" + index + ".class";
        String className = allClassPath.substring(allClassPath.lastIndexOf("\\") + 1, allClassPath.lastIndexOf("."));
        String returnStyle = signature.substring(signature.lastIndexOf(")") + 1);
        boolean needReturn = !returnStyle.equals("V");
        String oObjClassName = oObjType.substring(1, oObjType.length() - 1);
        String packageClassName = oPackageClassName.substring(0, oPackageClassName.lastIndexOf("/") + 1) + className;


        System.out.println("returnStyle : " + returnStyle);
        System.out.println("packageClassName : " + packageClassName);

        String arg = signature.substring(1, signature.lastIndexOf(")"));
        if (arg.endsWith(";")) arg = arg.substring(0, arg.length());
        String[] args = arg.split(";");
        if (args.length == 1 && args[0].length() == 0) args = new String[]{};
        for (int i = 0; i < args.length; i++) {
            args[i] += args[i].length() == 1 ? "" : ";";
            System.out.println(i + " : " + args[i]);
        }
        System.out.println("args.length : " + args.length);
        index++;

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(V1_7, ACC_PROTECTED + ACC_SUPER, packageClassName, null, "java/lang/Object", new String[]{"java/lang/Runnable"});

        cw.visitOuterClass(oPackageClassName, doMethodName, signature);

        if (result) {
            fv = cw.visitField(ACC_PRIVATE, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;", null, null);
            fv.visitEnd();
        }
        if (needReturn) {
            fv = cw.visitField(ACC_PRIVATE, "result", returnStyle, null, null);
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
            if (needReturn) {
                mv.visitVarInsn(ALOAD, 0);
            }
            if (isStatic) {
                for (int i = 0; i < args.length; i++) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "o" + i, args[i]);
                }
                mv.visitMethodInsn(INVOKESTATIC, oObjClassName, doMethodName, signature, false);
            } else {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, packageClassName, "obj", oObjType);
                for (int i = 0; i < args.length; i++) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "o" + i, args[i]);
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, oObjClassName, doMethodName, signature, false);
            }
            if (needReturn) {
                mv.visitFieldInsn(PUTFIELD, packageClassName, "result", returnStyle);
            }
            if (result) {
                Label l0 = new Label();
                if (threadStyle.equals("BG")) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;");
                    mv.visitJumpInsn(Opcodes.IFNULL, l0);
                }
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, packageClassName, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/CountDownLatch", "countDown", "()V", false);

                mv.visitLabel(l0);
            }
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "action", "(" + oObjType + signature.substring(1), null, null);
            mv.visitCode();

            //设置对象
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, packageClassName, "obj", oObjType);

            for (int i = 0; i < args.length; i++) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, i + 2);
                mv.visitFieldInsn(PUTFIELD, packageClassName, "o" + i, args[i]);
            }


            if (result) {
                Label l0 = new Label();
                if (threadStyle.equals("BG")) {
                    mv.visitMethodInsn(INVOKESTATIC, "com/by122006library/Utils/ThreadUtils", "isUIThread", "()Z", false);
                    mv.visitJumpInsn(Opcodes.IFEQ, l0);
                }
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, "java/util/concurrent/CountDownLatch");
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_1);
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/concurrent/CountDownLatch", "<init>", "(I)V", false);
                mv.visitFieldInsn(PUTFIELD, packageClassName, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;");
                if (threadStyle.equals("BG")) {
                    mv.visitLabel(l0);
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
            }
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "com/by122006library/ThreadManager", "post" + threadStyle.toUpperCase() + "Thread", "(Ljava/lang/Runnable;)V", false);

            if (result) {
                Label start = new Label();
                Label end = new Label();
                Label throw0 = new Label();
                Label l2 = new Label();

                Label l3 = new Label();
                if (threadStyle.equals("BG")) {
//                    mv.visitMethodInsn(INVOKESTATIC, "com/by122006library/Utils/ThreadUtils", "isUIThread", "()Z", false);
//                    mv.visitJumpInsn(Opcodes.IFEQ, l3);
                }


                mv.visitTryCatchBlock(start, end, throw0, "java/lang/InterruptedException");
                mv.visitLabel(start);
                {
                    mv.visitVarInsn(ALOAD, 0);
                    System.out.println("packageClassName : " + packageClassName);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;");
                    mv.visitLdcInsn(outTime);
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/util/concurrent/TimeUnit", "MILLISECONDS", "Ljava/util/concurrent/TimeUnit;");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/CountDownLatch", "await", "(JLjava/util/concurrent/TimeUnit;)Z", false);
                    mv.visitInsn(Opcodes.POP);
                }
                mv.visitLabel(end);
                mv.visitJumpInsn(Opcodes.GOTO, l2);

                mv.visitLabel(throw0);
                {
                    mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/InterruptedException"});
                    mv.visitVarInsn(Opcodes.ASTORE, args.length + 2);
                    mv.visitVarInsn(Opcodes.ALOAD, args.length + 2);
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/InterruptedException", "printStackTrace", "()V", false);
                }
                mv.visitLabel(l2);
//                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

                if (threadStyle.equals("BG")) {
//                    mv.visitLabel(l3);
//                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
            }
            {

                if (returnStyle.equals("V")) {
                    mv.visitInsn(RETURN);
                } else {
                    if (result) {
                        {
                            int load = ALOAD;
                            if (checkReturnStyle(returnStyle, "Z")) load = Opcodes.ILOAD;
                            else if (checkReturnStyle(returnStyle, "C", "S", "I", "B"))
                                load = Opcodes.ILOAD;
                            else if (checkReturnStyle(returnStyle, "J"))
                                load = Opcodes.LLOAD;
                            else if (checkReturnStyle(returnStyle, "F"))
                                load = Opcodes.FLOAD;
                            else if (checkReturnStyle(returnStyle, "D"))
                                load = Opcodes.DLOAD;
                            else load = Opcodes.ALOAD;
                            mv.visitVarInsn(load, 0);
                            mv.visitFieldInsn(GETFIELD, packageClassName, "result", returnStyle);
                            if (checkReturnStyle(returnStyle, "Z")) mv.visitInsn(Opcodes.IRETURN);
                            else if (checkReturnStyle(returnStyle, "C", "S", "I", "B"))
                                mv.visitInsn(Opcodes.IRETURN);
                            else if (checkReturnStyle(returnStyle, "J"))
                                mv.visitInsn(Opcodes.LRETURN);
                            else if (checkReturnStyle(returnStyle, "F"))
                                mv.visitInsn(Opcodes.FRETURN);
                            else if (checkReturnStyle(returnStyle, "D"))
                                mv.visitInsn(Opcodes.DRETURN);
                            else mv.visitInsn(ARETURN);
                        }
                    } else {
                        if (checkReturnStyle(returnStyle, "Z")) {
                            mv.visitInsn(Opcodes.ICONST_0);
                            mv.visitInsn(Opcodes.IRETURN);
                        } else if (checkReturnStyle(returnStyle, "C", "S", "I", "B")) {
                            mv.visitInsn(Opcodes.ICONST_0);
                            mv.visitInsn(Opcodes.IRETURN);
                        } else if (checkReturnStyle(returnStyle, "J")) {
                            mv.visitInsn(Opcodes.LCONST_0);
                            mv.visitInsn(Opcodes.LRETURN);
                        } else if (checkReturnStyle(returnStyle, "D")) {
                            mv.visitInsn(Opcodes.DCONST_0);
                            mv.visitInsn(Opcodes.DRETURN);
                        } else if (checkReturnStyle(returnStyle, "F")) {
                            mv.visitInsn(Opcodes.FCONST_0);
                            mv.visitInsn(Opcodes.FRETURN);
                        } else {
                            mv.visitInsn(Opcodes.ACONST_NULL);
                            mv.visitInsn(ARETURN);
                        }
                    }
                }
            }
            mv.visitMaxs(4, 2);
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

    private boolean checkReturnStyle(String returnStyle, String... matchs) {
        for (String match : matchs) {
            if (returnStyle.endsWith(match)) return true;
        }
        return false;
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

        /**
         * Style Async/Sync
         */
        boolean NewThread = false;
        /**
         * OutTime long
         */
        long OutTime = 2000l;
        /**
         * Result Skip/Wait
         */
        boolean Result = true;

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

            return new SmartRunAnnotationVisitor(ASM5, super.visitAnnotation(s, b));
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
                if (args.length == 1 && args[0].length() == 0) args = new String[]{};
                for (int i = 0; i < args.length; i++) {
                    args[i] += args[i].length() == 1 ? "" : ";";
                    System.out.println(i + " : " + args[i]);
                }
                System.out.println("args.length : " + args.length);

                int num = args.length;
                System.out.println(packageClassName);
                System.out.println("Flag_Static:" + Flag_Static);
                ov.visitCode();


                Label l1 = new Label();
                if (!NewThread) {
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
                                if (args[i].equals("Z")) pa = Opcodes.ILOAD;
                                else if (args[i].equals("B")) pa = Opcodes.ILOAD;
                                else if (args[i].equals("C")) pa = Opcodes.ILOAD;
                                else if (args[i].equals("S")) pa = Opcodes.ILOAD;
                                else if (args[i].equals("I")) pa = Opcodes.ILOAD;
                                else if (args[i].equals("J")) pa = Opcodes.LLOAD;
                                else if (args[i].equals("F")) pa = Opcodes.FLOAD;
                                else if (args[i].equals("D")) pa = Opcodes.DLOAD;
                            }
                            ov.visitVarInsn(pa, Flag_Static ? i : i + 1);
                        }
                    }
                    if (Flag_Static) {
                        ov.visitMethodInsn(INVOKESTATIC, packageClassName, name + "$SmartRun_" + style, desc, false);
                    } else
                        ov.visitMethodInsn(INVOKEVIRTUAL, packageClassName, name + "$SmartRun_" + style, desc, false);
                    ov.visitJumpInsn(GOTO, l1);
                    ov.visitLabel(l0);
                    ov.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
                String newClassName = packageClassName.substring(0, packageClassName.lastIndexOf("/") + 1) + createInnerClass(packageClassName, desc, style, Flag_Static, "L" + packageClassName + ";", name + "$SmartRun_" + style, NewThread, OutTime, Result);
                visitInnerClass(newClassName, null, null, 0);
                System.out.println("newClassName:" + newClassName);

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
                            if (args[i].equals("Z")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("B")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("C")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("S")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("I")) pa = Opcodes.ILOAD;
                            else if (args[i].equals("J")) pa = Opcodes.LLOAD;
                            else if (args[i].equals("F")) pa = Opcodes.FLOAD;
                            else if (args[i].equals("D")) pa = Opcodes.DLOAD;
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


                if (!NewThread) {
                    ov.visitLabel(l1);
                    ov.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }

                String returnStyle = desc.substring(desc.lastIndexOf(")") + 1);
                if (returnStyle.equals("V")) {
                    ov.visitInsn(RETURN);
                } else {
                    if (checkReturnStyle(returnStyle, "Z")) ov.visitInsn(Opcodes.IRETURN);
                    else if (checkReturnStyle(returnStyle, "C", "S", "I", "B"))
                        ov.visitInsn(Opcodes.IRETURN);
                    else if (checkReturnStyle(returnStyle, "J"))
                        ov.visitInsn(Opcodes.LRETURN);
                    else if (checkReturnStyle(returnStyle, "F"))
                        ov.visitInsn(Opcodes.FRETURN);
                    else if (checkReturnStyle(returnStyle, "D"))
                        ov.visitInsn(Opcodes.DRETURN);
                    else ov.visitInsn(ARETURN);
                }

                ov.visitMaxs(20 + num, 20 + num);
                ov.visitEnd();
                return;
            }
            super.visitEnd();
        }

        private class SmartRunAnnotationVisitor extends AnnotationVisitor {

            public SmartRunAnnotationVisitor(int i) {
                super(i);
            }

            public SmartRunAnnotationVisitor(int i, AnnotationVisitor annotationVisitor) {
                super(i, annotationVisitor);
            }

            @Override
            public void visit(String s, Object o) {
                System.out.println("Annotation " + s + " = " + String.valueOf(o));
                if (s.equals("Style")) {
                    if (String.valueOf(o).equals("Async")) NewThread = true;
                    if (String.valueOf(o).equals("Sync")) NewThread = false;
                }
                if (s.equals("OutTime")) {
                    OutTime = Long.parseLong(String.valueOf(o));
                }
                if (s.equals("Result")) {
                    if (String.valueOf(o).equals("Skip")) Result = false;
                    if (String.valueOf(o).equals("Wait")) Result = true;
                }
                super.visit(s, o);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String s, String s1) {
                System.out.println("s=" + s + ";s1=" + s1);
                return super.visitAnnotation(s, s1);
            }
        }
    }

}