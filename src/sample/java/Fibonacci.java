public class Fibonacci{
    public int fibonacci(int n){
        if(n == 1 || n == 2){
            return 1;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static void main(String[] args){
        Fibonacci f = new Fibonacci();
        if(args.length == 0){
            f.fibonacci(3);
        }
        else{
            f.fibonacci(Integer.parseInt(args[0]));
        }
    }
}
