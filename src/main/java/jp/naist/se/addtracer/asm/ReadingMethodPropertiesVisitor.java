package jp.naist.se.addtracer.asm;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class ReadingMethodPropertiesVisitor extends ClassVisitor{
    private Map<String, Map<Label, Integer>> lineNumberTable = new HashMap<String, Map<Label, Integer>>();
    private Map<String, Map<Integer, String>> localVariableTable = new HashMap<String, Map<Integer, String>>();

    public ReadingMethodPropertiesVisitor(){
        super(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions){
        MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);

        Map<Label, Integer> lineTable = lineNumberTable.get(name + "#" + desc);
        if(lineTable == null){
            lineTable = new HashMap<Label, Integer>();
            lineNumberTable.put(name + "#" + desc, lineTable);
        }
        Map<Integer, String> variableMap = localVariableTable.get(name + "#" + desc);
        if(variableMap == null){
            variableMap = new HashMap<Integer, String>();
            localVariableTable.put(name + "#" + desc, variableMap);
        }

        return new ReadingMethodPropertiesMethodVisitor(visitor, lineTable, variableMap);
    }

    public Map<String, Map<Label, Integer>> getLineNumberTable(){
        return lineNumberTable;
    }

    public Map<String, Map<Integer, String>> getLocalVariableTable(){
        return localVariableTable;
    }
}
