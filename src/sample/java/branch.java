public class branch{
    private int value = 0;
    public branch(){
        this(10);
    }
    public branch(int value){
        this.value = value;
    }
    public static void main(String[] args){
        branch s;
        if(args.length == 0) s = new branch();
        else                 s = new branch(Integer.parseInt(args[0]));
    }
}
