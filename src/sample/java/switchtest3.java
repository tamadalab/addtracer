public class switchtest3{
    public static void main(String[] args){
        switch(Integer.parseInt(args[0])){
        case 1:
            System.out.println("1");
            break;
        case 10:
            System.out.println("100");
            break;
        case 100:
            System.out.println("100");
            break;
        case 1000:
            System.out.println("1000");
            break;
        default:
            System.out.println("default");
            break;
        }
    }
}
