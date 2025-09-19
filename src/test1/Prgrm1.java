package test1;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
public class Prgrm1 {

	public static void main(String[] args) {
		JFrame f = new JFrame ("Frame");
		f.setSize(600,400);
		JLabel l1 = new JLabel("First number");
		JTextField t1 = new JTextField (10);
		JLabel l2 = new JLabel("Second number");
		JTextField t2 = new JTextField (10);
		JButton b = new JButton("Submit");
		
		f.setLayout(null);  // important for absolute positioning
		l1.setBounds(40, 55, 100, 14);
		t1.setBounds(160, 55, 200, 20);
		l2.setBounds(40, 70, 100, 14);
		t2.setBounds(160,70,200,20);
		b.setBounds(90, 90, 150, 25);
		b.setForeground(Color.RED);
		
	    f.add(l1);
	    f.add(l2);
	    f.add(t1);
	    f.add(t2);
		f.add(b);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

	}

}
