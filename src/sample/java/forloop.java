public class forloop{
    public forloop(String string){
        System.out.println(string);
    }

    public void sample(int value){
        int result = 0;
        for(int i = 0; i < value; i++){
            result = result + value * value;
        }
        System.out.println(result);
    }

    public static void main(String[] args){
        forloop s;
        if(args.length == 0) s = new forloop("test");
        else                 s = new forloop(args[0]);
        s.sample(10);
    }
}
