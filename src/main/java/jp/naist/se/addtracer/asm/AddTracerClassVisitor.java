package jp.naist.se.addtracer.asm;

import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddTracerClassVisitor extends ClassVisitor{
    private String className;
    private Map<String, Map<Label, Integer>> lineNumberTable;
    private Map<String, Map<Integer, String>> localVariableTable;

    public AddTracerClassVisitor(ClassVisitor visitor, 
            Map<String, Map<Label, Integer>> lineNumberTable,
            Map<String, Map<Integer, String>> localVariableTable){
        super(Opcodes.ASM4, visitor);
        this.lineNumberTable = lineNumberTable;
        this.localVariableTable = localVariableTable;
    }

    public String getClassName(){
        return className;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces){
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String arg0, boolean arg1){
        return super.visitAnnotation(arg0, arg1);
    }

    @Override
    public void visitAttribute(Attribute arg0){
        super.visitAttribute(arg0);
    }

    @Override
    public void visitEnd(){
        super.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int arg0, String arg1, String arg2,
            String arg3, Object arg4){
        return super.visitField(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public void visitInnerClass(String arg0, String arg1, String arg2, int arg3){
        super.visitInnerClass(arg0, arg1, arg2, arg3);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions){
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        String key = name + "#" + desc;
        return new AddTracerMethodVisitor(mv, lineNumberTable.get(key), localVariableTable.get(key));
    }

    @Override
    public void visitOuterClass(String arg0, String arg1, String arg2){
        super.visitOuterClass(arg0, arg1, arg2);
    }

    @Override
    public void visitSource(String source, String debug){
        super.visitSource(source, debug);
    }
}
