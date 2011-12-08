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

import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.DUP_X2;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.POP2;
import org.apache.bcel.generic.Type;

import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

/**
 * 
 * @author Haruaki TAMADA
 */
public class AthrowInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    @Override
    public boolean isTarget(InstructionHandle i, UpdateData data){
        return i.getInstruction() instanceof ATHROW;
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.INSERT;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        InstructionList list = new InstructionList();
        list.append(pushSystemOutAndStringBuffer(d, "exception\tthrow (line " + d.getLineNumber() + "):\t"));
        list.append(new DUP2_X1());
        list.append(new POP2());
        list.append(new DUP_X2());
        list.append(getAppendInstructions(d, Type.OBJECT));
        list.append(getToStringAndPrintln(d));

        return list;
    }
}
