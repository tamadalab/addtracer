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

/*
 * $Id: ReturnInstructionUpdateHandler.java 111 2006-09-19 12:10:31Z harua-t $
 */

import java.util.ArrayList;
import java.util.List;

import jp.cafebabe.commons.bcul.updater.PostProcessRequired;
import jp.naist.se.addtracer.TracerInstructionUpdateHandler;

import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Select;

/**
 * 
 * @author Haruaki TAMADA
 * @version $Revision: 111 $ $Date: 2006-09-19 21:10:31 +0900 (Tue, 19 Sep 2006) $
 */
public abstract class ReturnInstructionUpdateHandler extends TracerInstructionUpdateHandler implements PostProcessRequired{
    /**
     * return $B$KD>@\Ht$s$G$$$k(B BranchInstruction $B$r(B
     * return $B$NA0$N%H%l!<%5$KHt$V$h$&$KJQ99$9$k!%(B
     */
    protected InstructionList updateBranchTarget(InstructionList list){
        InstructionHandle[] handles = list.getInstructionHandles();
        for(int i = 0; i < handles.length; i++){
            Instruction inst = handles[i].getInstruction();
            if(inst instanceof BranchInstruction){
                InstructionHandle[] targets;
                if(inst instanceof Select){
                    InstructionHandle[] h = ((Select)inst).getTargets();
                    List<InstructionHandle> handleList = new ArrayList<InstructionHandle>();
                    for(int k = 0; k < h.length; k++) handleList.add(h[k]);
                    handleList.add(((Select)inst).getTarget());
                    targets = handleList.toArray(new InstructionHandle[handleList.size()]);
                }
                else{
                    targets = new InstructionHandle[] { 
                        ((BranchInstruction)inst).getTarget(), 
                    };
                }

                // RETURN $B$NA0$N(B GETSTATIC $B$rC5$7!$$=$3$K(B target $B$rCV$/!%(B
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
