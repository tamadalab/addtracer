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

import java.util.List;
import java.util.ArrayList;

import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.ReturnInstruction;

import jp.cafebabe.commons.bcul.updater.PostProcessRequired;
import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

/**
 * 
 * @author Haruaki TAMADA
 */
public class ReturnInstructionUpdateHandler extends TracerInstructionUpdateHandler implements PostProcessRequired{
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        return ih.getInstruction() instanceof ReturnInstruction;
    }

    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.INSERT;
    }

    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        return d.getFactory().createPrintln(
            "<!-- end Method " + d.getClassName() +
            "#" + d.getMethodName() + "\t// - ending at line " +
            d.getLineNumber() + " -->"
        );
    }

    public void postprocess(InstructionList list, UpdateData d){
        updateBranchTarget(list);
    }

    /**
     * return に直接飛んでいる BranchInstruction を
     * return の前のトレーサに飛ぶように変更する．
     */
    private InstructionList updateBranchTarget(InstructionList list){
        InstructionHandle[] handles = list.getInstructionHandles();
        for(int i = 0; i < handles.length; i++){
            Instruction inst = handles[i].getInstruction();
            if(inst instanceof BranchInstruction){
                InstructionHandle[] targets;
                if(inst instanceof Select){
                    InstructionHandle[] h = ((Select)inst).getTargets();
                    List l = new ArrayList();
                    for(int k = 0; k < h.length; k++) l.add(h[k]);
                    l.add(((Select)inst).getTarget());
                    targets = (InstructionHandle[])l.toArray(
                        new InstructionHandle[l.size()]
                    );
                }
                else{
                    targets = new InstructionHandle[] { 
                        ((BranchInstruction)inst).getTarget(), 
                    };
                }

                // RETURN の前の GETSTATIC を探し，そこに target を置く．
                for(int j = 0; j < targets.length; j++){
                    Instruction ii = targets[j].getInstruction();
                    InstructionHandle handle = targets[j];
                    if(ii instanceof ReturnInstruction){
                        while(!(handle.getInstruction() instanceof GETSTATIC)){
                            handle = handle.getPrev();
                        }
                        if(inst instanceof Select){
                            ((Select)inst).updateTarget(targets[j], handle);
                        }
                        else{
                            ((BranchInstruction)inst).setTarget(handle);
                        }
                    }
                }
            }
        }

        return list;
    }
}
