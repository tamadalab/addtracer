/*                          Apache License
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package jp.naist.se.addtracer.standard;

import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.IAND;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
 */
public class StartOfMethodInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    private boolean firstStaticInitializer = true;

    public void reset(){
        super.reset();
        firstStaticInitializer = true;
    }

    public boolean isTarget(InstructionHandle ih, UpdateData data){
        return data.getTargetIndex() == 0;
    }

    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.INSERT;
    }

    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        InstructionList list = new InstructionList();

        list.append(d.getFactory().createPrintln(
            "<!-- begin Method " + d.getClassName() + "#" + d.getMethodName() +
            "\t// line " + d.getLineNumber() + " defined in - -->")
        );

        if(d.getMethodName().equals("<clinit>")){
            // 今まで static イニシャライザがなければ
            if(firstStaticInitializer){
                list.append(getDeclarationTracer(d, true));
                firstStaticInitializer = false;
            }
        }
        else if(d.getMethodName().equals("<init>")){
            list.append(getDeclarationTracer(d, false));
        }
        list.append(getArgumentTracer(d));

        return list;
    }

    private InstructionList getDeclarationTracer(UpdateData data, boolean staticValue){
        ClassGen c = data.getClassGen();
        Field[] fields = c.getFields();

        InstructionList list = new InstructionList();

        for(int i = 0; i < fields.length; i++){
            if(fields[i].isStatic() == staticValue){
                list.append(getVariableDeclarationTracer(data, fields[i]));
            }
        }

        return list;
    }

    private InstructionList getVariableDeclarationTracer(UpdateData d, Field f){
        String className = d.getClassName();
        InstructionList list = new InstructionList();
        String refType = "";
        if(f.isStatic()){
            refType = "<s>";
        }

        list.append(d.getFactory().createPrintln(
            className + "#" + f.getName() + refType + "\t*\tdeclaration\t// line -")
        );
        if(f.getConstantValue() != null){
            int index = f.getConstantValue().getConstantValueIndex();
            Constant constant = d.getConstantPoolGen().getConstant(index);
            String value = null;
            if(constant.getTag() == Constants.CONSTANT_String){
                value = ((ConstantString)constant).getBytes(d.getConstantPoolGen().getConstantPool());
            }
            else if(constant.getTag() == Constants.CONSTANT_Double){
                value = String.valueOf(((ConstantDouble)constant).getBytes());
            }
            else if(constant.getTag() == Constants.CONSTANT_Float){
                value = String.valueOf(((ConstantFloat)constant).getBytes());
            }
            else if(constant.getTag() == Constants.CONSTANT_Integer){
                value = String.valueOf(((ConstantInteger)constant).getBytes());
            }
            else if(constant.getTag() == Constants.CONSTANT_Long){
                value = String.valueOf(((ConstantLong)constant).getBytes());
            }
            else{
                value = null;
            }
            list.append(d.getFactory().createPrintln(
                className + "#" + f.getName() + "\t" + value + "\tassignment\t// line -")
            );
        }

        return list;
    }

    private InstructionList getArgumentTracer(UpdateData data){
        InstructionList list = new InstructionList();
        MethodGen method = data.getMethod();

        Type[] types = method.getArgumentTypes();
        int line = data.getLineNumber(0);
        int variableIndex = 1;
        if(method.isStatic()) variableIndex = 0;

        for(int i = 0; i < types.length; i++){
            if(types[i] instanceof ArrayType){
                list.append(getArrayTypeArgumentTracer(data, types[i], variableIndex));
            }
            else{
                Type type = getStringBufferArgumentType(types[i]);
                Type appendType = type;

                list.append(pushSystemOutAndStringBuffer(
                    data, data.getVariableName(variableIndex) + "\t"
                ));

                list.append(InstructionFactory.createLoad(types[i], variableIndex));
                if(!(type.equals(Type.BOOLEAN) || type.equals(Type.INT) ||
                     type.equals(Type.BYTE)    || type.equals(Type.SHORT) ||
                     type.equals(Type.FLOAT)   || type.equals(Type.CHAR)  ||
                     type.equals(Type.DOUBLE)  || type.equals(Type.LONG))){

                    list.append(data.getFactory().createInvoke(
                        "java.lang.System", "identityHashCode", Type.INT,
                        new Type[] { Type.OBJECT, }, Constants.INVOKESTATIC
                    ));
                    list.append(data.getFactory().createInvoke(
                        "java.lang.Integer", "toHexString", Type.STRING,
                        new Type[] { Type.INT, }, Constants.INVOKESTATIC
                    ));
                    appendType = Type.STRING;
                }
                else{
                }

                list.append(data.getFactory().createInvoke(
                    STRINGBUFFER, "append", Type.STRINGBUFFER,
                    new Type[] { appendType, }, Constants.INVOKEVIRTUAL
                ));
                list.append(getToStringAndPrintln(data, "\tassignment\t// line " + line));
            }
            variableIndex += types[i].getSize();
        }

        return list;
    }

    private InstructionList getArrayTypeArgumentTracer(UpdateData d, Type type, int index){
        InstructionList list = new InstructionList();
        String value = d.getVariableName(index);
        int line = d.getLineNumber(0);
        String tracer = value + "[0-";

        list.append(pushSystemOutAndStringBuffer(d, tracer));
        list.append(new ALOAD(index));
        list.append(new ARRAYLENGTH());
        list.append(d.getFactory().createInvoke(STRINGBUFFER, "append", Type.STRINGBUFFER,
                                                new Type[] { Type.INT, }, Constants.INVOKEVIRTUAL));
        list.append(getToStringAndPrintln(d, "]\t*\tassignment\t// line " + line));

        list.append(pushSystemOutAndStringBuffer(d, value + ".length\t"));
        list.append(new ALOAD(index));
        list.append(new ARRAYLENGTH());
        list.append(d.getFactory().createInvoke(STRINGBUFFER, "append", Type.STRINGBUFFER,
                                                new Type[] { Type.INT, }, Constants.INVOKEVIRTUAL));
        list.append(getToStringAndPrintln(d, "\tassignment\t// line " + line));

        return list;
    }
}
