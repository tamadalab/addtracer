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
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.generic.ArithmeticInstruction;
import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.Type;

/**
 * 
 * @author Haruaki TAMADA
 */
public class ArithmeticInstructionUpdateHandler extends TracerInstructionUpdateHandler{
    @Override
    public boolean isTarget(InstructionHandle i, UpdateData data){
        return i.getInstruction() instanceof ArithmeticInstruction;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        ArithmeticInstruction i = (ArithmeticInstruction)handle.getInstruction();

        String operand = "?";
        String name = i.getClass().getName().toLowerCase();
        name = name.substring(name.lastIndexOf(".") + 1);

        if(name.indexOf("add") > 0)       operand = "+";   // 足し算
        else if(name.indexOf("sub")  > 0) operand = "-";   // 引き算
        else if(name.indexOf("mul")  > 0) operand = "*";   // 掛け算
        else if(name.indexOf("div")  > 0) operand = "/";   // 割り算
        else if(name.indexOf("rem")  > 0) operand = "%";   // 剰余
        else if(name.indexOf("and")  > 0) operand = "&";   // and
        else if(name.indexOf("xor")  > 0) operand = "^";   // xor
        else if(name.indexOf("or")   > 0) operand = "|";   // or
        else if(name.indexOf("ushr") > 0) operand = ">>>"; // >>>
        else if(name.indexOf("shr")  > 0) operand = ">>";  // >>
        else if(name.indexOf("shl")  > 0) operand = "<<";  // <<
        else if(name.indexOf("neg")  > 0) operand = "~";   // 符号反転
        else                              operand = name;

        Type type = i.getType(d.getConstantPoolGen());
        int size = type.getSize();

        InstructionList list = new InstructionList();
        list.append(InstructionFactory.createDup(size));
        list.append(pushSystemOutAndStringBuffer(d));
        list.append(getAppendInstructions(d, operand + "\t"));

        if(size == 1){
            list.append(new DUP2_X1());
        }
        else{
            list.append(new DUP2_X2());
        }
        list.append(new POP());
        list.append(new POP());
        list.append(getAppendInstructions(d, type));
        list.append(getToStringAndPrintln(
            d, "{" + type + "}\toperation\t// line " + d.getLineNumber()
        ));

        return list;
    }
}
