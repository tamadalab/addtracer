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
 * $Id: PutFieldInstructionUpdateHandler.java,v 1.3 2005/07/25 09:19:48 harua-t Exp $
 */

import jp.cafebabe.commons.bcul.SWAP2;
import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.DUP_X2;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.IAND;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.PopInstruction;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
 * @version $Revision: 1.3 $ $Date: 2005/07/25 09:19:48 $
 */
public class PutFieldInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        Instruction i = ih.getInstruction();
        return i instanceof FieldInstruction && i instanceof PopInstruction;
    }

    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.INSERT;
    }

    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        FieldInstruction i = (FieldInstruction)handle.getInstruction();
        String fieldName = i.getFieldName(d.getConstantPoolGen());
        String refType = "";
        boolean staticType = false;
        String className = i.getClassName(d.getConstantPoolGen());
        if(i.getClass().getName().toLowerCase().indexOf("static") > 0){
            staticType = true;
            refType = "<s>";
        }

        Type type = getStringBufferArgumentType(i.getType(d.getConstantPoolGen()));
        Type fieldType = i.getFieldType(d.getConstantPoolGen());

        InstructionList list = new InstructionList();

        list.append(pushSystemOutAndStringBuffer(
            d, className + "#" + fieldName + refType + "\t"
        ));

        if(staticType){
            list.append(d.getFactory().createGetStatic(className, fieldName, fieldType));
        }
        else{
            list.append(d.getFactory().createGetField(className, fieldName, fieldType));
        }
        if(fieldType.equals(Type.BOOLEAN) || fieldType.equals(Type.INT) ||
           fieldType.equals(Type.BYTE)    || fieldType.equals(Type.SHORT) ||
           fieldType.equals(Type.FLOAT)   || fieldType.equals(Type.CHAR)  ||
           fieldType.equals(Type.DOUBLE)  || fieldType.equals(Type.LONG)  ||
           fieldType.equals(Type.STRING)){
            list.append(getAppendInstructions(d, type));
        }
        else{
            list.append(d.getFactory().createInvoke(
                "java.lang.Object", "getClass", new ObjectType(CLASS),
                Type.NO_ARGS, Constants.INVOKEVIRTUAL
            ));
            list.append(d.getFactory().createInvoke(
                "java.lang.Class", "getName", Type.STRING,
                Type.NO_ARGS, Constants.INVOKEVIRTUAL
            ));
            list.append(getAppendInstructions(d, Type.STRING));
            list.append(getAppendInstructions(d, "@"));

            if(staticType){
                list.append(d.getFactory().createGetStatic(className, fieldName, fieldType));
            }
            else{
                list.append(d.getFactory().createGetField(className, fieldName, fieldType));
            }
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
