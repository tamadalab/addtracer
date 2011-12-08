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
package jp.naist.se.addtracer.simple;

/*
 * $Id: LocalVariableInstructionUpdateHandler.java 89 2006-09-15 06:01:55Z harua-t $
 */

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

/**
 * 
 * @author Haruaki TAMADA
 * @version $Revision: 89 $ $Date: 2006-09-15 15:01:55 +0900 (Fri, 15 Sep 2006) $
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
            value = "exception\tcatch (line " + d.getLineNumber() + "):\t";
        }
        else{
            if(i instanceof LoadInstruction){
                value = name + "\treference (line " + d.getLineNumber() + "):\t";
            }
            else{
                value = name + "\tassignment (line " + d.getLineNumber() + ":\t";
            }
        }

        // System.out.println("type: " + type + "(" + type.getSize() + "): " + index);
        // System.out.println("real type: " + realType + "(" + realType.getSize() + "): " + index);
        // if(type.getSize() == 2){
        //     index = (index * 2) - 1;
        // }

        list.append(pushSystemOutAndStringBuffer(d, value));
        list.append(InstructionFactory.createLoad(realType, index));
        if(catchException){
            list.append(d.getFactory().createInvoke(
                "java/lang/Object", "getClass", new ObjectType(CLASS), Type.NO_ARGS, Constants.INVOKEVIRTUAL)
            );
            list.append(d.getFactory().createInvoke(
                CLASS, "getName", Type.STRING, Type.NO_ARGS, Constants.INVOKEVIRTUAL)
            );
        }
        list.append(getAppendInstructions(d, type));
        list.append(getToStringAndPrintln(d));

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
