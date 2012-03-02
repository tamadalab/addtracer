package jp.cafebabe.commons.bcul.updater;

import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;

/**
 * <p>
 * The handler for updating certain instruction.
 * </p><p>
 * A subclass must implement modifying code in {@link #updateInstruction 
 * <code>updateInstruction</code>} and checking target from 
 * given {@link InstructionHandle <code>InstructionHandle</code>} object in
 * {@link #isTarget <code>isTarget</code>}.
 * </p>
 *
 * @author Haruaki TAMADA
 */
public abstract class AbstractInstructionUpdateHandler implements InstructionUpdateHandler{
    /**
     * do nothing...
     */
    public AbstractInstructionUpdateHandler(){
    }

    @Override
    public abstract InstructionList updateInstruction(InstructionHandle i, UpdateData data);

    @Override
    public abstract boolean isTarget(InstructionHandle i, UpdateData data);

    @Override
    public void reset(){
    }

    @Override
    public UpdateType getUpdateType(InstructionHandle i){
        return UpdateType.APPEND;
    }
}
