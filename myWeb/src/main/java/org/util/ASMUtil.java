package org.util;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by rzy on 2020/1/16.
 */
public class ASMUtil {
    public static void main(String[] args) throws Exception {
//        System.out.println(ASMUtil.class.getDeclaredMethod("main",String[].class).get);
//        System.out.println(getMethodParamNames(ASMUtil.class.getDeclaredMethod("main",String[].class))[0]);
    }
    private static boolean sameType(org.objectweb.asm.Type[] types, Class<?>[] clazzes) {
        if (types.length != clazzes.length) return false;
        for (int i = 0; i < types.length; i++) {
            if (!org.objectweb.asm.Type.getType(clazzes[i]).equals(types[i])) return false;
        }
        return true;
    }
    //说明:获取方法的参数名
    /**{ ylx } 2020/1/14 15:05 */
    public static String[] getMethodParamNames(final Method m) {
        final String[] paramNames = new String[m.getParameterTypes().length];
        final String n = m.getDeclaringClass().getName();
        org.objectweb.asm.ClassReader cr = null;
        try { cr = new ClassReader(n); } catch (IOException e) { throw new RuntimeException(e); }
        cr.accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM4) {
            public org.objectweb.asm.MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                final org.objectweb.asm.Type[] args = org.objectweb.asm.Type.getArgumentTypes(desc);// 方法名相同并且参数个数相同
                if (!name.equals(m.getName()) || !sameType(args, m.getParameterTypes())) return super.visitMethod(access, name, desc, signature,exceptions);
                org.objectweb.asm.MethodVisitor v = super.visitMethod(access, name, desc,signature, exceptions);
                return new org.objectweb.asm.MethodVisitor(org.objectweb.asm.Opcodes.ASM4, v) {
                    public void visitLocalVariable(String name, String desc, String signature, org.objectweb.asm.Label start, org.objectweb.asm.Label end, int index) {
                        int i = index - 1;
                        if (Modifier.isStatic(m.getModifiers())) i = index; // 如果是静态方法，则第一就是参数 如果不是静态方法，则第一个是"this"，然后才是方法的参数
                        if (i >= 0 && i < paramNames.length) paramNames[i] = name;
                        super.visitLocalVariable(name, desc, signature, start,end, index);
                    }
                };
            }
        }, 0);
        return paramNames;
    }
}
