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

import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.POP2;

import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

/**
 * 
 * @author Haruaki TAMADA
 */
public abstract class ArrayStoreInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    @Override
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        Instruction i = ih.getInstruction();
        String name = i.getName().toLowerCase();
        return i instanceof ArrayInstruction && name.indexOf("store") > 0;
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.INSERT;
    }

    protected InstructionList createCommonUpdateInstructions(UpdateData data, Type type){
        InstructionList list = new InstructionList();

        list.append(pushSystemOutAndStringBuffer(data));
        list.append(new DUP2_X1());
        list.append(new POP2());
        list.append(getAppendInstructions(data, Type.OBJECT));
        list.append(getAppendInstructions(data, "["));
        list.append(new DUP2_X1());
        list.append(new POP2());
        list.append(getAppendInstructions(data, Type.INT));
        if(!type.equals(Type.DOUBLE) && !type.equals(Type.FLOAT) && !type.equals(Type.BOOLEAN)){
            list.append(getAppendInstructions(data, "]\t0x"));
        }
        else{
            list.append(getAppendInstructions(data, "]\t"));
        }

        return list;
    }
}
