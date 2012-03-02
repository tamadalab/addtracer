package jp.cafebabe.commons.bcul.updater;

import org.apache.bcel.generic.InstructionList;

/**
 * @author Haruaki TAMADA
 */
public interface PostProcessRequired{
    public void postprocess(InstructionList list, UpdateData data);
}
