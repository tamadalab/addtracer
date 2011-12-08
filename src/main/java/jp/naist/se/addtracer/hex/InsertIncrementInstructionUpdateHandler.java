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

/*
 * $Id: InsertIncrementInstructionUpdateHandler.java 89 2006-09-15 06:01:55Z harua-t $
 */

import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.IAND;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
 * @version $Revision: 89 $ $Date: 2006-09-15 15:01:55 +0900 (Fri, 15 Sep 2006) $
 */
public class InsertIncrementInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    @Override
    public boolean isTarget(InstructionHandle i, UpdateData data){
        return i.getInstruction() instanceof IINC;
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.INSERT;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        IINC iinc = (IINC)handle.getInstruction();

        InstructionList list = new InstructionList();

        list.append(pushSystemOutAndStringBuffer(d, d.getVariableName(iinc.getIndex()) + "\t0x"));
        list.append(InstructionFactory.createLoad(Type.INT, iinc.getIndex()));
        list.append(d.getFactory().createConstant(new Integer(0xff)));
        list.append(new IAND());
        list.append(d.getFactory().createInvoke(
            "java.lang.Integer", "toHexString", Type.STRING,
            new Type[] { Type.INT, }, Constants.INVOKESTATIC
        ));
        list.append(getAppendInstructions(d, Type.STRING));
        list.append(getToStringAndPrintln(d, "\treference\t// line " + d.getLineNumber()));

        return list;
    }
}
