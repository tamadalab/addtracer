package jp.cafebabe.commons.bcul.updater;

import java.util.HashMap;
import java.util.Map;

import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.MethodGen;

/**
 * 
 *
 * @author Haruaki TAMADA
 */
public class UpdateData implements java.io.Serializable{
    private static final long serialVersionUID = 51235728234238725L;
    private ClassGen classGen;
    private ConstantPoolGen poolGen;
    private InstructionFactory factory;

    private MethodGen methodGen;
    private LineNumberTable lineNumber;
    private LocalVariableTable localVariables;
    private CodeException[] exceptions;

    private Map<Integer, String> variableNames = new HashMap<Integer, String>();
    private int byteOffset = -1;
    private int targetIndex = 0;
    private boolean before;

    public UpdateData(ClassGen classGen){
        setClassGen(classGen);
    }

    public void clear(){
        classGen = null;
        poolGen = null;
        factory = null;
        methodGen = null;
        lineNumber = null;
        localVariables = null;
        exceptions = null;
        variableNames = new HashMap<Integer, String>();
        byteOffset = -1;
        targetIndex = 0;
        before = true;
    }

    public void setBefore(boolean before){
        this.before = before;
    }

    public boolean isBefore(){
        return before;
    }

    public boolean isAfter(){
        return !before;
    }

    public String getClassName(){
        return getClassGen().getClassName();
    }

    public ClassGen getClassGen() {
        return classGen;
    }

    public InstructionFactory getFactory() {
        return factory;
    }


    public ConstantPoolGen getConstantPoolGen(){
        return poolGen;
    }

    public void setClassGen(ClassGen classGen) {
        this.classGen = classGen;
        this.poolGen = classGen.getConstantPool();
        this.factory = new InstructionFactory(classGen);
    }


    public void setMethod(MethodGen methodGen){
        this.methodGen = methodGen;
        this.lineNumber = methodGen.getLineNumberTable(getConstantPoolGen());
        this.localVariables = methodGen.getLocalVariableTable(getConstantPoolGen());
        this.exceptions = methodGen.getMethod().getCode().getExceptionTable();

        if(localVariables != null){
            initVariableNames(localVariables.getLocalVariableTable());
        }
    }

    public MethodGen getMethod(){
        return methodGen;
    }

    public LocalVariableTable getLocalVariableTable(){
        return localVariables;
    }

    public CodeException[] getExceptions() {
        return exceptions;
    }


    public LineNumberTable getLineNumberTable(){
        return lineNumber;
    }

    public String getMethodName(){
        return getMethod().getName();
    }

    public void incrementTargetIndex(){
        this.targetIndex++;
    }

    public void setTargetIndex(int index) {
        this.targetIndex = index;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public void setByteOffset(int byteOffset) {
        this.byteOffset = byteOffset;
    }

    public int getByteOffset() {
        return byteOffset;
    }


    public int getLineNumber(int byteOffset){
        return getLineNumberTable().getSourceLine(byteOffset);
    }

    public int getLineNumber() {
        return getLineNumber(getByteOffset());
    }

    public int getLocalVariableCount(){
        return variableNames.size();
    }

    public String getVariableName(int index){
        String name = variableNames.get(new Integer(index));

        if(name == null){
            name = "l" + index;
            variableNames.put(new Integer(index), name);
        }

        return name;
    }

    private void initVariableNames(LocalVariable[] variables){
        for(int i = 0; i < variables.length; i++){
            if(variables[i] != null){
                variableNames.put(new Integer(variables[i].getIndex()),
                                  variables[i].getName());
            }
        }
    }
}
