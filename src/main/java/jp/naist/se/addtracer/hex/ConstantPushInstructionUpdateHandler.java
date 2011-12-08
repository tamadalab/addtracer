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

import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

/**
 * 
 * @author Haruaki TAMADA
 */
public class ConstantPushInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    @Override
    public boolean isTarget(InstructionHandle i, UpdateData data){
        return i.getInstruction() instanceof ConstantPushInstruction;
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle handle){
        return UpdateType.APPEND;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        ConstantPushInstruction i = (ConstantPushInstruction)handle.getInstruction();

        Type type = i.getType(d.getConstantPoolGen());
        Number number = i.getValue();

        InstructionList list = new InstructionList();

        String numberString;
        if(type.equals(Type.BYTE) || type.equals(Type.SHORT) || type.equals(Type.INT) || type.equals(Type.LONG)){
            numberString = "0x" + Integer.toHexString(number.intValue() & 0xffffffff);
        }
        else if(type.equals(Type.LONG)){
            numberString = "0x" + Long.toHexString(number.longValue());
        }
        else{
            numberString = number.toString();
        }

        list.append(pushSystemOutAndStringBuffer(d, "-\t" + numberString + "{" + type + "}\tconstant\t// line " + d.getLineNumber()));
        list.append(getToStringAndPrintln(d));

        return list;
    }
}
