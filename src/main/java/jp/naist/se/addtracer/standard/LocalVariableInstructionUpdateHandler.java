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
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
 */
public class LocalVariableInstructionUpdateHandler extends TracerInstructionUpdateHandler{

    @Override
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        Instruction i = ih.getInstruction();
        return i instanceof LocalVariableInstruction && !(i instanceof IINC);
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.APPEND;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        LocalVariableInstruction i = (LocalVariableInstruction)handle.getInstruction();
        int index = i.getIndex();
        String name = d.getVariableName(index);
        Type realType = i.getType(d.getConstantPoolGen());
        Type type = getStringBufferArgumentType(realType);
        boolean catchException = isCatchException(d.getExceptions(), handle.getPosition());

        InstructionList list = new InstructionList();
        String value = name + "\t";
        if(catchException){
            type = Type.STRING;
            value = "<!-- begin Exception catch ";
        }

        // System.out.printf("type: %s (%d:%d): %d%n", type, type.getSize(), type.getType(), index);
        // System.out.printf("real type: %s (%d:%d): %d%n", realType, realType.getSize(), realType.getType(), index);
        // if(type.getSize() == 2){
        //     index = (index * 2) - 1;
        // }

        list.append(pushSystemOutAndStringBuffer(d, value));
        list.append(getAppendInstructions(d, realType.toString()));
        
        list.append(getAppendInstructions(d, "@"));
        list.append(InstructionFactory.createLoad(realType, index));

        if(!(realType instanceof BasicType)){
            list.append(d.getFactory().createInvoke(
                "java.lang.System", "identityHashCode", Type.INT,
                new Type[] { Type.OBJECT, }, Constants.INVOKESTATIC
            ));
            list.append(d.getFactory().createInvoke(
                "java.lang.Integer", "toHexString", Type.STRING,
                new Type[] { Type.INT, }, Constants.INVOKESTATIC
            ));
            type = Type.STRING;
        }

        if(catchException){
            value = "\t// line " + d.getLineNumber() + "-->";
        }
        else{
            if(i instanceof LoadInstruction){
                value = "\treference\t// line " + d.getLineNumber();
            }
            else{
                value = "\tassignment\t// line " + d.getLineNumber();
            }
        }
        list.append(getAppendInstructions(d, type));
        list.append(getToStringAndPrintln(d, value));

        return list;
    }

    public boolean isCatchException(CodeException[] exceptions, int index){
        boolean flag = false;
        for(int i = 0; i < exceptions.length; i++){
            if(exceptions[i].getHandlerPC() == index){
                flag = true;
                break;
            }
        }

        return flag;
    }
}
