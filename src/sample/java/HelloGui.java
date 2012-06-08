import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class HelloGui{
  public HelloGui(){
    JFrame frame = new JFrame("Hello Gui");
    frame.getContentPane().add(new JLabel("Hello World"), BorderLayout.CENTER);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setVisible(true);
  }

  public static void main(String[] args){
    new HelloGui();
  }
}
