package jp.naist.se.addtracer.asm;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ReadingMethodPropertiesMethodVisitor extends MethodVisitor{
    private Map<Label, Integer> lineMap;
    private Map<Integer, String> variableMap;

    public ReadingMethodPropertiesMethodVisitor(MethodVisitor visitor, Map<Label, Integer> lineMap, Map<Integer, String> variableMap){
        super(Opcodes.ASM4);
        this.lineMap = lineMap;
        this.variableMap = variableMap;
    }

    @Override
    public void visitLineNumber(int line, Label start){
        super.visitLineNumber(line, start);
        lineMap.put(start, line);
        System.out.printf("visitLineNumber(%s, %d)%n", start, line);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index){
        variableMap.put(index, name);
    }
}
