package jp.naist.se.addtracer.asm;

import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AddTracerMethodVisitor extends MethodVisitor{
    private static final String SYSTEM_CLASS = "java.lang.System";
    private static final String STRING_BUILDER_CLASS = "java.lang.StringBuilder";
    private static final String PRINTSTREAM_CLASS = "java.io.PrintStream";
    private static final Type PRINTSTREAM_TYPE = Type.getObjectType("Ljava/io/PrintStream;");
    private static final Type STRING_BUILDER_TYPE = Type.getObjectType("Ljava/lang/StringBuilder;");
    private static final Type STRING_TYPE = Type.getObjectType("Ljava/lang/String;");

    private Map<Label, Integer> lineNumberTable;
    private Map<Integer, String> variableTable;
    private int currentNumber = -1;

    public AddTracerMethodVisitor(MethodVisitor visitor, Map<Label, Integer> lineNumberTable, Map<Integer, String> variableTable){
        super(Opcodes.ASM4, visitor);
        this.lineNumberTable = lineNumberTable;
        this.variableTable = variableTable;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc){
        super.visitFieldInsn(opcode, owner, name, desc);

        String fieldName = name; 
        if(opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC){
            fieldName = fieldName + "<s>";
        }

        super.visitFieldInsn(Opcodes.GETSTATIC, SYSTEM_CLASS, "out", PRINTSTREAM_CLASS);
        super.visitTypeInsn(Opcodes.NEW, STRING_BUILDER_CLASS);
        super.visitInsn(Opcodes.DUP);
        super.visitMethodInsn(
            Opcodes.INVOKESPECIAL, STRING_BUILDER_CLASS, "<init>",
            Type.getMethodDescriptor(Type.VOID_TYPE)
        );
        super.visitLdcInsn(owner + "#" + fieldName + "\t");
        super.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL, STRING_BUILDER_CLASS, "append",
            Type.getMethodDescriptor(STRING_BUILDER_TYPE, STRING_TYPE)
        );
        if(opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC){
            super.visitFieldInsn(Opcodes.GETSTATIC, owner, name, desc);
        }
        else{
            super.visitFieldInsn(Opcodes.GETFIELD, owner, name, desc);
        }
        if(desc.length() == 1){
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, STRING_BUILDER_CLASS, "append",
                Type.getMethodDescriptor(STRING_BUILDER_TYPE, Type.getArgumentTypes(desc))
            );
        }
        else{
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC, SYSTEM_CLASS, "identityHashCode",
                Type.getMethodDescriptor(STRING_BUILDER_TYPE, Type.getObjectType("Ljava/lang/Object;"))
            );
        }

        super.visitLdcInsn("\treference\t// line " + currentNumber);
        super.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL, STRING_BUILDER_CLASS, "append",
            Type.getMethodDescriptor(STRING_BUILDER_TYPE, STRING_TYPE)
        );
        super.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL, STRING_BUILDER_CLASS, "toString",
            Type.getMethodDescriptor(STRING_TYPE)
        );
        super.visitMethodInsn(
             Opcodes.INVOKEVIRTUAL, PRINTSTREAM_CLASS, "println",
             Type.getMethodDescriptor(Type.VOID_TYPE, STRING_TYPE)
        );
    }

    @Override
    public void visitIincInsn(int var, int increment){
        super.visitIincInsn(var, increment);
        String name = variableTable.get(var);
        if(name == null){
            name = "local" + var;
        }
        super.visitFieldInsn(Opcodes.GETSTATIC, SYSTEM_CLASS, "out", PRINTSTREAM_CLASS);

    }

    @Override
    public void visitInsn(int arg0){
        // TODO Auto-generated method stub
        super.visitInsn(arg0);
    }

    @Override
    public void visitIntInsn(int arg0, int arg1){
        // TODO Auto-generated method stub
        super.visitIntInsn(arg0, arg1);
    }

    @Override
    public void visitInvokeDynamicInsn(String arg0, String arg1, Handle arg2,
            Object... arg3){
        // TODO Auto-generated method stub
        super.visitInvokeDynamicInsn(arg0, arg1, arg2, arg3);
    }

    @Override
    public void visitJumpInsn(int arg0, Label arg1){
        // TODO Auto-generated method stub
        super.visitJumpInsn(arg0, arg1);
    }

    @Override
    public void visitLabel(Label label){
        super.visitLabel(label);
        System.out.printf("visitLabel(%s)%n", label);
        Integer lineNumber = lineNumberTable.get(label);
        if(lineNumber != null){
            currentNumber = lineNumber;
        }
    }

    @Override
    public void visitLdcInsn(Object arg0){
        // TODO Auto-generated method stub
        super.visitLdcInsn(arg0);
    }

    @Override
    public void visitLineNumber(int line, Label start){
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitLocalVariable(String arg0, String arg1, String arg2,
            Label arg3, Label arg4, int arg5){
        // TODO Auto-generated method stub
        super.visitLocalVariable(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2){
        // TODO Auto-generated method stub
        super.visitLookupSwitchInsn(arg0, arg1, arg2);
    }

    @Override
    public void visitMaxs(int arg0, int arg1){
        // TODO Auto-generated method stub
        super.visitMaxs(arg0, arg1);
    }

    @Override
    public void visitMethodInsn(int opcodes, String owner, String name, String desc){
        // TODO Auto-generated method stub
        super.visitMethodInsn(opcodes, owner, name, desc);
    }

    @Override
    public void visitMultiANewArrayInsn(String arg0, int arg1){
        // TODO Auto-generated method stub
        super.visitMultiANewArrayInsn(arg0, arg1);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1,
            boolean arg2){
        // TODO Auto-generated method stub
        return super.visitParameterAnnotation(arg0, arg1, arg2);
    }

    @Override
    public void visitTableSwitchInsn(int arg0, int arg1, Label arg2,
            Label... arg3){
        // TODO Auto-generated method stub
        super.visitTableSwitchInsn(arg0, arg1, arg2, arg3);
    }

    @Override
    public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2,
            String arg3){
        // TODO Auto-generated method stub
        super.visitTryCatchBlock(arg0, arg1, arg2, arg3);
    }

    @Override
    public void visitTypeInsn(int arg0, String arg1){
        // TODO Auto-generated method stub
        super.visitTypeInsn(arg0, arg1);
    }

    @Override
    public void visitVarInsn(int arg0, int arg1){
        // TODO Auto-generated method stub
        super.visitVarInsn(arg0, arg1);
    }

    
}
