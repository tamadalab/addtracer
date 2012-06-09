interface HelloInterface{
  void hello();
}

public class InvokeInterface{
  public static void main(String[] args){
    HelloInterface hi = new HelloInterface(){
      public void hello(){
        System.out.println("Hello World");
      }
    };
    hi.hello();
  }
}
