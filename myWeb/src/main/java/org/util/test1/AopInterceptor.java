package org.util.test1;

import com.lx.util.LX;
import com.lx.web.core.Test;
import org.objectweb.asm.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class AopInterceptor implements Opcodes{
    public static void beforeInvoke() {
        System.out.println("before");
    }

    public static void afterInvoke() {
        System.out.println("after");
    }

    public static void main(String[] args) throws Exception {
        Class<?> cls = TestBean.class;
        final String className = "$"+ LX.uuid32(5);
        String fieldType = "L"+cls.getName().replace(".","/")+";";//声明变量的类型
        ClassReader classReader = new ClassReader(cls.getName());
        ClassWriter cw = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv = cw.visitField(ACC_PUBLIC, "chain", "Lcom/lx/core/aop/AOPChain;", null, null);
        fv.visitEnd();

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5, cw){
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                //更改类名，并使新类继承原有的类。
                super.visit(version, access, name + className, signature, name, interfaces);
                {
                    MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                    mv.visitCode();
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKESPECIAL, name, "<init>", "()V");
                    mv.visitInsn(RETURN);
                    mv.visitMaxs(1, 1);
                    mv.visitEnd();
                }
            }

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (!name.equals("halloAop")) return null;
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodVisitor(this.api, mv){
                    public void visitCode() {
                        this.visitMethodInsn(INVOKESTATIC, "org/util/test1/AopInterceptor", "beforeInvoke", "()V");
                        super.visitCode();
                    }

                    public void visitInsn(int opcode) {
                        if (opcode == RETURN) {
                            this.visitMethodInsn(INVOKESTATIC, "org/util/test1/AopInterceptor", "afterInvoke", "()V");
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
        };

        classReader.accept(visitor, ClassReader.SKIP_DEBUG);
        new FileOutputStream("TestBean.class").write(cw.toByteArray());
//        Class<TestBean> clazz = (Class<TestBean>) new MyClassLoader().defineClassForName("org.util.test1.TestBean"+className, cw.toByteArray());
//        clazz.newInstance().halloAop();
    }
    static class MyClassLoader extends ClassLoader {
        public Class<?> defineClassForName(String name, byte[] data) {
            return this.defineClass(name, data, 0, data.length);
        }
    }
}