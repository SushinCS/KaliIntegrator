package burp;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.JList;
import javax.swing.JEditorPane;

public class Demo extends JPanel {

	public JLabel lblUrlTested;
	public JLabel lblVulnerabilityStatus;
	public JTextPane textPane;
	public JTextPane textPane_1 ;
	
	public Demo() {
		setLayout(null);
		
		JLabel lblUrlTested = new JLabel("URL Tested");
		lblUrlTested.setBackground(Color.WHITE);
		lblUrlTested.setForeground(Color.BLUE);
		lblUrlTested.setHorizontalAlignment(SwingConstants.CENTER);
		lblUrlTested.setBounds(28, 27, 248, 28);
		add(lblUrlTested);
		
		JLabel lblVulnerabilityStatus = new JLabel("Status");
		lblVulnerabilityStatus.setForeground(Color.BLUE);
		lblVulnerabilityStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblVulnerabilityStatus.setBounds(281, 27, 125, 28);
		add(lblVulnerabilityStatus);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBounds(28, 58, 248, 193);
		add(textPane);
		
		
		JTextPane textPane_1 = new JTextPane();
		textPane_1.setEditable(false);
		textPane_1.setBounds(281, 58, 125, 193);
		add(textPane_1);
		

	}
	public void append(String Text1,String Text2)
	{
		textPane.setText(Text1);
		textPane_1.setText(Text2);
		
}
}
