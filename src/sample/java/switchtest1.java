public class switchtest1{
    public static void main(String[] args){
        switch(Integer.parseInt(args[0])){
        case 1:
            System.out.println("1");
            break;
        case 3:
            System.out.println("3");
            break;
        case 5:
            System.out.println("5");
            break;
        case 7:
            System.out.println("7");
            break;
        default:
            System.out.println("default");
            break;
        }
    }
}
