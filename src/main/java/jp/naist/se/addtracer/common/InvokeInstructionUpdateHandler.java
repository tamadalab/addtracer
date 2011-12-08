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
package jp.naist.se.addtracer.common;

import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.SWAP;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
 */
public class InvokeInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    public UpdateType getUpdateType(){
        return UpdateType.INSERT;
    }

    @Override
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        Instruction i = ih.getInstruction();
        if(i instanceof INVOKESTATIC){
            // exclude invocation of System#arraycopy method.
            String className = getTargetClassName((INVOKESTATIC)i, data.getConstantPoolGen());
            String invokeMethod = ((INVOKESTATIC)i).getMethodName(data.getConstantPoolGen());
            return !(className.equals("java.lang.System") && invokeMethod.equals("arraycopy"));
        }
        return i instanceof InvokeInstruction;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData data){
        ClassGen cg = data.getClassGen();
        InvokeInstruction invoke = (InvokeInstruction)handle.getInstruction();

        String className = getTargetClassName(invoke, data.getConstantPoolGen());
        String invokeMethod = invoke.getMethodName(data.getConstantPoolGen());

        InstructionList il = new InstructionList();

        il.append(data.getFactory().createConstant(
            "invoke " + className + "#" + invokeMethod + " in " + cg.getClassName() +
            "#" + data.getMethodName() + " at line " + data.getLineNumber()
        ));
        il.append(data.getFactory().createGetStatic(
            "java/lang/System", "out", new ObjectType("java/io/PrintStream")
        ));
        il.append(new SWAP());
        il.append(data.getFactory().createInvoke(
            "java/io/PrintStream", "println", Type.VOID,
            new Type[]{ Type.STRING }, Constants.INVOKEVIRTUAL
        ));
        return il;
    }
}
