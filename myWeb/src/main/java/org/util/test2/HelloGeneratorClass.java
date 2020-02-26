//package org.util.test2;
//
//import org.objectweb.asm.*;
//import org.util.test1.MyProxy;
//import org.util.test1.TestBean;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.lang.reflect.Method;
//
///**
// * Created by rzy on 2020/1/19.
// */
//public class HelloGeneratorClass implements Opcodes{
//    public static void main(String[]args) throws Exception {
//        byte[] dump = TestBean$1Dump.dump();
//        new FileOutputStream("TestBean.class").write(dump);
//        Class<TestBean> clazz = (Class<TestBean>) new MyClassLoader().defineClassForName("org.util.test1.TestBean$1", dump);
//        TestBean testBean = clazz.newInstance();
//        clazz.getField("obj").set(testBean,new TestBean());
//        clazz.getField("myProxy").set(testBean, new MyProxy() {
//            @Override
//            public void before(Object... objs) {
//                System.out.println(objs.length);
//                System.out.println("开始");
//            }
//
//            @Override
//            public Object after(Object... objects) {
//                System.out.println("结束");
//                return null;
//            }
//        });
//        testBean.halloAop("a");
//    }
//    static class MyClassLoader extends ClassLoader {
//        public Class<?> defineClassForName(String name, byte[] data) {
//            return this.defineClass(name, data, 0, data.length);
//        }
//    }
//    static class TestBean$1Dump implements Opcodes {
//
//        public static byte[] dump(Class<?> cls,String suffix, Method method) throws Exception {
//            String className = cls.getName().replace(".","/");
//            ClassWriter cw = new ClassWriter(0);
//            FieldVisitor fv;
//            MethodVisitor mv;
//            AnnotationVisitor av0;
//
//            cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, className+suffix, null,className, null);
//
//            cw.visitSource(className+suffix+".java", null);
//
//            {
//                fv = cw.visitField(ACC_PUBLIC, "obj", "L"+className+";", null, null);
//                fv.visitEnd();
//            }
//            {
//                fv = cw.visitField(ACC_PUBLIC, "myProxy", "Lorg/util/test1/MyProxy;", null, null);
//                fv.visitEnd();
//            }
//            {
//                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
//                mv.visitCode();
//                Label l0 = new Label();
//                mv.visitLabel(l0);
//                mv.visitLineNumber(6, l0);
//                mv.visitVarInsn(ALOAD, 0);
//                mv.visitMethodInsn(INVOKESPECIAL, "org/util/test1/TestBean", "<init>", "()V", false);
//                mv.visitInsn(RETURN);
//                Label l1 = new Label();
//                mv.visitLabel(l1);
//                mv.visitLocalVariable("this", "Lorg/util/test1/TestBean$1;", null, l0, l1, 0);
//                mv.visitMaxs(1, 1);
//                mv.visitEnd();
//            }
//            {
//                mv = cw.visitMethod(ACC_PUBLIC, "halloAop", "(Ljava/lang/String;)V", null, null);
//                mv.visitCode();
//                Label l0 = new Label();
//                mv.visitLabel(l0);
//                mv.visitLineNumber(11, l0);
//                mv.visitVarInsn(ALOAD, 0);
//                mv.visitFieldInsn(GETFIELD, "org/util/test1/TestBean$1", "myProxy", "Lorg/util/test1/MyProxy;");
//                mv.visitInsn(ICONST_1);
//                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
//                mv.visitInsn(DUP);
//                mv.visitInsn(ICONST_0);
//                mv.visitVarInsn(ALOAD, 1);
//                mv.visitInsn(AASTORE);
//                mv.visitMethodInsn(INVOKEINTERFACE, "org/util/test1/MyProxy", "before", "([Ljava/lang/Object;)V", true);
//                Label l1 = new Label();
//                mv.visitLabel(l1);
//                mv.visitLineNumber(12, l1);
//                mv.visitVarInsn(ALOAD, 0);
//                mv.visitFieldInsn(GETFIELD, "org/util/test1/TestBean$1", "obj", "Lorg/util/test1/TestBean;");
//                mv.visitVarInsn(ALOAD, 1);
//                mv.visitMethodInsn(INVOKEVIRTUAL, "org/util/test1/TestBean", "halloAop", "(Ljava/lang/String;)V", false);
//                Label l2 = new Label();
//                mv.visitLabel(l2);
//                mv.visitLineNumber(13, l2);
//                mv.visitVarInsn(ALOAD, 0);
//                mv.visitFieldInsn(GETFIELD, "org/util/test1/TestBean$1", "myProxy", "Lorg/util/test1/MyProxy;");
//                mv.visitInsn(ICONST_1);
//                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
//                mv.visitInsn(DUP);
//                mv.visitInsn(ICONST_0);
//                mv.visitVarInsn(ALOAD, 1);
//                mv.visitInsn(AASTORE);
//                mv.visitMethodInsn(INVOKEINTERFACE, "org/util/test1/MyProxy", "after", "([Ljava/lang/Object;)Ljava/lang/Object;", true);
//                mv.visitInsn(POP);
//                Label l3 = new Label();
//                mv.visitLabel(l3);
//                mv.visitLineNumber(14, l3);
//                mv.visitInsn(RETURN);
//                Label l4 = new Label();
//                mv.visitLabel(l4);
//                mv.visitLocalVariable("this", "Lorg/util/test1/TestBean$1;", null, l0, l4, 0);
//                mv.visitLocalVariable("a", "Ljava/lang/String;", null, l0, l4, 1);
//                mv.visitMaxs(5, 2);
//                mv.visitEnd();
//            }
//            cw.visitEnd();
//
//            return cw.toByteArray();
//        }
//    }
//}
