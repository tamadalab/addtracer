package jp.cafebabe.commons.bcul.updater;

import org.apache.bcel.generic.InstructionList;

/**
 * @author Haruaki TAMADA
 */
public interface PreProcessRequired{
    public void preprocess(InstructionList list, UpdateData data);
}
