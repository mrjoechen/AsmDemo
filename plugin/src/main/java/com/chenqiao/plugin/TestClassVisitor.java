package com.chenqiao.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by chenqiao on 2019-12-16.
 * e-mail : mrjctech@gmail.com
 */
public class TestClassVisitor extends ClassVisitor implements Opcodes {

    public TestClassVisitor(final ClassVisitor cv) {
        super(ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if ("printTest".equals(name)) {
            //处理onDestroy
            System.out.println("TestClassVisitor : change method ----> " + name);
            return new TestMethodVisitor(mv);
        }
        return mv;
    }

}
