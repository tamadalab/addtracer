public class invokespecial2 extends invokespecial{
    public invokespecial2(int value){
        super(new Integer(value));
    }

    public static void main(String[] args){
        new invokespecial2(10);
    }
}
