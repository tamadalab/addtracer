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

import org.apache.bcel.Constants;
import org.apache.bcel.generic.IAND;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.InstructionFactory;
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
public class AppendIncrementInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    @Override
    public boolean isTarget(InstructionHandle i, UpdateData data){
        return i.getInstruction() instanceof IINC;
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.APPEND;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        IINC iinc = (IINC)handle.getInstruction();
        Type type = iinc.getType(d.getConstantPoolGen());
        InstructionList list = new InstructionList();

        int increment = iinc.getIncrement();
        list.append(pushSystemOutAndStringBuffer(d));
        if(increment > 0){
            list.append(getAppendInstructions(d, "+ <" + type + ">\t"));
        }
        else{
            list.append(getAppendInstructions(d, "- <" + type + ">\t"));
        }
        list.append(InstructionFactory.createLoad(Type.INT, iinc.getIndex()));
        list.append(getAppendInstructions(d, Type.INT));
        list.append(getToStringAndPrintln(d, "\toperation\t// line " + d.getLineNumber()));

        list.append(pushSystemOutAndStringBuffer(d, d.getVariableName(iinc.getIndex()) + "\t0x"));
        list.append(InstructionFactory.createLoad(Type.INT, iinc.getIndex()));
        list.append(d.getFactory().createConstant(new Integer(0xffffffff)));
        list.append(new IAND());
        list.append(d.getFactory().createInvoke(
            "java.lang.Integer", "toHexString", Type.STRING,
            new Type[] { Type.INT, }, Constants.INVOKESTATIC
        ));
        list.append(getAppendInstructions(d, Type.STRING));
        list.append(getToStringAndPrintln(d, "\tassignment\t// line " + d.getLineNumber()));

        return list;
    }
}
