public class invokespecial{
    private Integer value;

    public invokespecial(Integer i){
        this.value = i;
    }

    public invokespecial(int i){
        this(new Integer(i));
    }

    public static void main(String[] args){
        new invokespecial(10);
    }
}
