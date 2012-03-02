package jp.cafebabe.commons.bcul;

import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.POP2;

/**
 *
 * @author Haruaki TAMADA
 */
public class SWAP2 extends InstructionList{
    private static final long serialVersionUID = 632097923147092354L;
    public SWAP2(){
        super.append(new DUP2_X2());
        super.append(new POP2());
    }
}
