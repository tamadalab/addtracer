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

import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.DUP2;
import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.PopInstruction;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
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
        String className = i.getReferenceType(d.getConstantPoolGen()).toString();
        if(i.getClass().getName().toLowerCase().indexOf("static") > 0){
            refType = "<s>";
        }

        Type type = getStringBufferArgumentType(i.getType(d.getConstantPoolGen()));
        Type fieldType = i.getFieldType(d.getConstantPoolGen());

        InstructionList list = new InstructionList();
        if(fieldType.equals(Type.LONG) || fieldType.equals(Type.DOUBLE)){
            list.append(new DUP2());
        }
        else{
            list.append(new DUP());
        }

        list.append(pushSystemOutAndStringBuffer(
            d, className + "#" + fieldName + refType + "\t"
        ));

        if(fieldType instanceof BasicType){
            if(fieldType.equals(Type.DOUBLE)  || fieldType.equals(Type.LONG)){
                list.append(new DUP2_X2());
            }
            else{
                list.append(new DUP2_X1());
            }
            list.append(new POP());
            list.append(new POP());
            list.append(getAppendInstructions(d, type));
        }
        else{
            list.append(getAppendInstructions(d, fieldType.toString()));
            list.append(getAppendInstructions(d, "@"));

            if(fieldType.equals(Type.LONG) || fieldType.equals(Type.DOUBLE)){
                list.append(new DUP2_X2());
            }
            else{
                list.append(new DUP2_X1());
            }
            list.append(new POP());
            list.append(new POP());
            list.append(d.getFactory().createInvoke(
                "java.lang.System", "identityHashCode", Type.INT,
                new Type[] {Type.OBJECT, }, Constants.INVOKESTATIC
            ));
            list.append(d.getFactory().createInvoke(
                "java.lang.Integer", "toHexString", Type.STRING,
                new Type[] { Type.INT, }, Constants.INVOKESTATIC
            ));
            list.append(getAppendInstructions(d, Type.STRING));
        }
        list.append(getToStringAndPrintln(d, "\tassignment\t// line " + d.getLineNumber()));

        return list;
    }
}
