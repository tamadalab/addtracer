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

import jp.cafebabe.commons.bcul.SWAP2;
import jp.cafebabe.commons.bcul.updater.UpdateData;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.DASTORE;
import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LASTORE;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.POP2;
import org.apache.bcel.generic.SWAP;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
 */
public class ArrayStoreWideInstructionUpdateHandler extends ArrayStoreInstructionUpdateHandler{
    @Override
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        Instruction i = ih.getInstruction();
        return super.isTarget(ih, data) && (i instanceof DASTORE || i instanceof LASTORE);
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        ArrayInstruction i = (ArrayInstruction)handle.getInstruction();
        Type origType = i.getType(d.getConstantPoolGen());
        Type type = getStringBufferArgumentType(origType);

        InstructionList list = new InstructionList();
        list.append(new DUP2_X2());
        list.append(new SWAP2());
        list.append(new DUP2_X2());
        list.append(new SWAP());

        list.append(createCommonUpdateInstructions(d, origType));

        list.append(new DUP2_X2());
        list.append(new POP2());

        if(origType.equals(Type.LONG) || origType.equals(Type.DOUBLE)){
            list.append(getAppendInstructions(d, type));
        }
        else{
            list.append(new DUP2_X2());
            list.append(d.getFactory().createInvoke(
                "java.lang.Object", "getClass", new ObjectType(CLASS),
                Type.NO_ARGS, Constants.INVOKEVIRTUAL
            ));
            list.append(d.getFactory().createInvoke(
                "java.lang.Class", "getName", Type.STRING,
                Type.NO_ARGS, Constants.INVOKEVIRTUAL
            ));
            list.append(new DUP2_X2());
            list.append(new POP2());
            list.append(d.getFactory().createInvoke(
                "java.lang.System", "identityHashCode", Type.INT,
                new Type[] { Type.OBJECT, }, Constants.INVOKESTATIC
            ));
            list.append(d.getFactory().createInvoke(
                "java.lang.Integer", "toHexString", Type.STRING,
                new Type[] { Type.INT, }, Constants.INVOKESTATIC
            ));
            list.append(getAppendInstructions(d, Type.STRING));
        }
        list.append(getToStringAndPrintln(d, "\tassignment\t// line " + d.getLineNumber()));

        list.append(new SWAP2());

        return list;
    }
}
