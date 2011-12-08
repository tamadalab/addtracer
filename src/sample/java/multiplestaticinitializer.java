public class multiplestaticinitializer{
    public static final int DEFAULT_VALUE = 5;
    static{
        System.out.println("static initializer 1");
    }
    public static void main(String[] args){
        multiplestaticinitializer msi = new multiplestaticinitializer();
    }
    static{
        System.out.println("static initializer 2");
    }
}
