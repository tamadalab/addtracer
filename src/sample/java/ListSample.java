import java.util.*;

public class ListSample{
  public static void main(String[] args){
    List<Integer> list = new ArrayList<Integer>();
    Random rand = new Random();
    for(int i = 0; i < 10; i++){
      list.add(new Integer(rand.nextInt()));
    }
    for(Integer i: list){
      System.out.println(i);
    }
  }
}
