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
package jp.naist.se.addtracer.std;

/*
 * $Id: ArrayLoadInstructionUpdateHandler.java 89 2006-09-15 06:01:55Z harua-t $
 */

import jp.cafebabe.commons.bcul.SWAP2;
import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.DUP2;
import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.SWAP;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
 * @version $Revision: 89 $ $Date: 2006-09-15 15:01:55 +0900 (Fri, 15 Sep 2006) $
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

        Type type = getStringBufferArgumentType(i.getType(d.getConstantPoolGen()));

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
        list.append(getAppendInstructions(d, "]\t"));
        list.append(new SWAP2());
        list.append(new DUP2_X2());
        list.append(i.copy());
        list.append(getAppendInstructions(d, type));
        list.append(getToStringAndPrintln(d, "\treference\t// line " + d.getLineNumber()));

        return list;
    }
}
