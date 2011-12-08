public class booleanvalue{
    private boolean printdeclaration = false;
    public booleanvalue(){
        if(!printdeclaration){
            boolean v1 = false;
            boolean v2 = true;
        }
    }

    public static void main(String[] args){
        System.out.println(new booleanvalue().printdeclaration);
    }
}
