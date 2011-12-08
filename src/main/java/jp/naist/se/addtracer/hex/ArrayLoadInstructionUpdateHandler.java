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
package jp.naist.se.addtracer.hex;

import jp.cafebabe.commons.bcul.SWAP2;
import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.DUP2;
import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.IAND;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.SWAP;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
 */
public class ArrayLoadInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    @Override
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        Instruction i = ih.getInstruction();
        String name = i.getName().toLowerCase();

        return i instanceof ArrayInstruction && name.indexOf("load") >= 0;
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.INSERT;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        ArrayInstruction i = (ArrayInstruction)handle.getInstruction();

        Type origType = i.getType(d.getConstantPoolGen());
        Type type = getStringBufferArgumentType(origType);

        InstructionList list = new InstructionList();
        list.append(new DUP2());
        list.append(new SWAP());

        list.append(pushSystemOutAndStringBuffer(d));
        list.append(new DUP2_X1());
        list.append(new POP());
        list.append(new POP());
        list.append(getAppendInstructions(d, Type.OBJECT));
        list.append(getAppendInstructions(d, "["));
        list.append(new DUP2_X1());
        list.append(new POP());
        list.append(new POP());
        list.append(getAppendInstructions(d, Type.INT));
        if(origType.equals(Type.INT) || origType.equals(Type.BYTE) || origType.equals(Type.SHORT) ||
           origType.equals(Type.CHAR) || origType.equals(Type.LONG)){
            list.append(getAppendInstructions(d, "]\t0x"));
        }
        else{
            list.append(getAppendInstructions(d, "]\t"));
        }
        list.append(new SWAP2());
        list.append(new DUP2_X2());
        list.append(i.copy());
        if(origType.equals(Type.INT) || origType.equals(Type.BYTE) || origType.equals(Type.SHORT) || origType.equals(Type.CHAR)){
            if(origType.equals(Type.BYTE)){
                list.append(d.getFactory().createConstant(new Integer(0xff)));
                list.append(new IAND());
            }
            list.append(d.getFactory().createInvoke(
                "java.lang.Integer", "toHexString", Type.STRING, new Type[] {Type.INT, }, Constants.INVOKESTATIC
            ));
            list.append(getAppendInstructions(d, Type.STRING));
        }
        else if(origType.equals(Type.LONG)){
            list.append(d.getFactory().createInvoke(
                "java.lang.Long", "toHexString", Type.STRING, new Type[] {Type.LONG, }, Constants.INVOKESTATIC
            ));
            list.append(getAppendInstructions(d, Type.STRING));
        }
        else{
            list.append(getAppendInstructions(d, type));
        }
        list.append(getToStringAndPrintln(d, "\treference\t// line " + d.getLineNumber()));

        return list;
    }
}
