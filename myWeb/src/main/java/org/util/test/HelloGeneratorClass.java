package org.util.test;

import java.io.FileOutputStream;

import org.objectweb.asm.*;



/**
 * 通过ASM生成类的字节码
 *
 * @author Administrator
 *
 */
public class HelloGeneratorClass implements Opcodes {
    public static void main(String[] args)throws Exception{
        ClassReader cr = new ClassReader("test.Operation");
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cr.accept(new ClassVisitor(Opcodes.ASM7,cw){
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if ("oper".equals(name)) {
                    return new MethodVisitor(Opcodes.ASM7,super.visitMethod(access, name, desc, signature, exceptions)){
                        public void visitCode() {//方法执行之前植入代码
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/util/test/Log", "startLog", "()V", false);
                            super.visitCode();
                        }

                        public void visitInsn(int opcode) {
                            if (opcode == Opcodes.RETURN) {//方法return之前，植入代码
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/util/test/Log", "endLog", "()V", false);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        }, ClassReader.SKIP_DEBUG);
        byte[] bytes = cw.toByteArray();
        new FileOutputStream("a.class").write(bytes);
        Class<?> clazz = new MyClassLoader().defineClassForName("test.Operation", cw.toByteArray());
        new Operation().oper();
//        clazz.getMethods()[0].invoke(clazz.newInstance());
    }
}

class MyClassLoader extends ClassLoader {
    public Class<?> defineClassForName(String name, byte[] data) {
        return this.defineClass(name, data, 0, data.length);
    }
}

