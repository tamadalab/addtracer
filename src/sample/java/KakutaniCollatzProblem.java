import java.util.List;
import java.util.ArrayList;

/**
 * @author Haruaki TAMADA
 */
public class KakutaniCollatzProblem{
    public int[] getSequence(int value){
        List<Integer> values = new ArrayList<Integer>();
        if(value <= 1){
            return new int[] { };
        }
        values.add(value);
        while(value > 1){
            if((value % 2) == 1){
                value = 3 * value + 1;
            }
            else{
                value = value / 2;
            }
            values.add(value);
        }

        int[] v = new int[values.size()];
        for(int i = 0; i < v.length; i++){
            v[i] = values.get(i);
        }
        return v;
    }

    public static void main(String[] args){
        KakutaniCollatzProblem kcp = new KakutaniCollatzProblem();
        for(String arg: args){
            int[] seq = kcp.getSequence(Integer.parseInt(arg));
            System.out.print(arg + ": ");
            for(int i = 0; i < seq.length; i++){
                if(i > 0) System.out.print(", ");
                System.out.print(seq[i]);
            }
            System.out.println();
        }
    }
}
