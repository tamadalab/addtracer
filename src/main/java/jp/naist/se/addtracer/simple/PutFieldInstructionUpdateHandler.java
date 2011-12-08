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
 * $Id: PutFieldInstructionUpdateHandler.java 117 2006-10-02 06:21:58Z harua-t $
 */

import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.DUP_X2;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.PopInstruction;
import org.apache.bcel.generic.Type;

import jp.cafebabe.commons.bcul.SWAP2;
import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

/**
 * 
 * @author Haruaki TAMADA
 * @version $Revision: 117 $ $Date: 2006-10-02 15:21:58 +0900 (Mon, 02 Oct 2006) $
 */
public class PutFieldInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    @Override
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        Instruction i = ih.getInstruction();
        return i instanceof FieldInstruction && i instanceof PopInstruction;
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.INSERT;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        FieldInstruction i = (FieldInstruction)handle.getInstruction();
        String fieldName = i.getFieldName(d.getConstantPoolGen());
        String refType = "";
        if(i.getClass().getName().toLowerCase().indexOf("static") > 0){
            refType = "<s>";
        }

        Type type = getStringBufferArgumentType(i.getType(d.getConstantPoolGen()));
        Type fieldType = i.getFieldType(d.getConstantPoolGen());

        InstructionList list = new InstructionList();

        list.append(pushSystemOutAndStringBuffer(
            d, getTargetClassName(i, d.getConstantPoolGen()) + "#" + fieldName + refType +
            "\tassignment (" + d.getLineNumber() + "):\t")
        );
        if(fieldType.getSize() == 2){
            list.append(new SWAP2());
            list.append(new DUP2_X2());
        }
        else{
            list.append(new DUP2_X1());
            list.append(new POP());
            list.append(new POP());
            list.append(new DUP_X2());
        }
        list.append(getAppendInstructions(d, type));
        list.append(getToStringAndPrintln(d));

        return list;
    }
}
