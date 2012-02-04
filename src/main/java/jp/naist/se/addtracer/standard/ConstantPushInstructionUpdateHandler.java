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

/*
 * $Id: ConstantPushInstructionUpdateHandler.java,v 1.2 2005/07/25 07:33:06 harua-t Exp $
 */

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
 * @version $Revision: 1.2 $ $Date: 2005/07/25 07:33:06 $
 */
public class ConstantPushInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    public boolean isTarget(InstructionHandle i, UpdateData data){
        return i.getInstruction() instanceof ConstantPushInstruction;
    }

    public UpdateType getUpdateType(InstructionHandle handle){
        return UpdateType.APPEND;
    }

    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        ConstantPushInstruction i = (ConstantPushInstruction)handle.getInstruction();

        Type type = i.getType(d.getConstantPoolGen());
        Number number = i.getValue();

        InstructionList list = new InstructionList();

        String numberString = number.toString();
        list.append(pushSystemOutAndStringBuffer(
            d, "-\t" + numberString + "{" + type + "}\tconstant\t// line " + d.getLineNumber()
        ));
        list.append(getToStringAndPrintln(d));

        return list;
    }
}
