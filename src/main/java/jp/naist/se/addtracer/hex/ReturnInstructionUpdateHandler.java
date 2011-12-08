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
 * $Id: ReturnInstructionUpdateHandler.java 117 2006-10-02 06:21:58Z harua-t $
 */

import jp.cafebabe.commons.bcul.updater.PostProcessRequired;
import jp.cafebabe.commons.bcul.updater.UpdateData;
import jp.cafebabe.commons.bcul.updater.UpdateType;

import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.ReturnInstruction;

/**
 * 
 * @author Haruaki TAMADA
 * @version $Revision: 117 $ $Date: 2006-10-02 15:21:58 +0900 (Mon, 02 Oct 2006) $
 */
public class ReturnInstructionUpdateHandler extends jp.naist.se.addtracer.common.ReturnInstructionUpdateHandler implements PostProcessRequired{
    @Override
    public boolean isTarget(InstructionHandle ih, UpdateData data){
        return ih.getInstruction() instanceof ReturnInstruction;
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.INSERT;
    }

    @Override
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData d){
        return d.getFactory().createPrintln("<!-- end Method " + d.getClassName() +
                                            "#" + d.getMethodName() + "\t// - ending at line " +
                                            d.getLineNumber() + " -->");
    }

    @Override
    public void postprocess(InstructionList list, UpdateData d){
        updateBranchTarget(list);
    }
}
