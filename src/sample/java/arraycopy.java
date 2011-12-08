public class arraycopy{
    static{
    }

    public static void main(String[] args){
        String[] dest = new String[args.length];
        System.arraycopy(args, 0, dest, 0, args.length);
    }
}
