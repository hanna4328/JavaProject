package lostfound;
import javax.swing.*;
public class Homepg 
{
	public Homepg() 
	{
	       JFrame frame = new JFrame("Home Page");
	       frame.setSize(400, 200);
	       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	       frame.setLayout(null);
	       JLabel welcome = new JLabel("Welcome! You are logged in.");
	       welcome.setBounds(100, 80, 200, 30);
	       frame.add(welcome);
	       frame.setVisible(true);
	 }
}

