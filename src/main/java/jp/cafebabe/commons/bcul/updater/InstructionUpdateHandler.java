package jp.cafebabe.commons.bcul.updater;

import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;

/**
 * 
 * @author Haruaki TAMADA
 */
public interface InstructionUpdateHandler{
    public InstructionList updateInstruction(InstructionHandle handle, UpdateData data);

    /**
     * If this object targeted the instruction handled given i, this method returns true.
     */
    public boolean isTarget(InstructionHandle i, UpdateData data);

    public void reset();

    public UpdateType getUpdateType(InstructionHandle i);
}
