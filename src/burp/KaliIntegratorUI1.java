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
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

public class KaliIntegratorUI1 extends JPanel {

	public JLabel urllabel;
	public JLabel statuslabel;
	public JTextArea scannedurllist;
	public JTextArea result;
	
	
	
	public KaliIntegratorUI1() {
		setLayout(null);
		
		urllabel = new JLabel("URL Tested");
		urllabel.setFont(UIManager.getFont("Label.font"));
		urllabel.setBackground(Color.WHITE);
		urllabel.setForeground(Color.BLUE);
		urllabel.setHorizontalAlignment(SwingConstants.CENTER);
		urllabel.setBounds(77, 27, 248, 28);
		add(urllabel);
		
		statuslabel = new JLabel("Status");
		statuslabel.setFont(UIManager.getFont("Label.font"));
		statuslabel.setForeground(Color.BLUE);
		statuslabel.setHorizontalAlignment(SwingConstants.CENTER);
		statuslabel.setBounds(391, 27, 125, 28);
		add(statuslabel);
		
		scannedurllist = new JTextArea();
		scannedurllist.setEnabled(false);
		scannedurllist.setFont(UIManager.getFont("EditorPane.font"));
		scannedurllist.setForeground(new Color(255, 140, 0));
		scannedurllist.setToolTipText("Scanned Url List");
		scannedurllist.setEditable(false);
		scannedurllist.setBounds(49, 67, 307, 198);
		add(scannedurllist);
		
		result = new JTextArea();
		result.setEnabled(false);
		result.setFont(UIManager.getFont("EditorPane.font"));
		result.setForeground(new Color(0, 128, 0));
		result.setToolTipText("Result");
		result.setEditable(false);
		result.setBounds(385, 67, 131, 198);
		add(result);
		

	}
	public void append(String s1,String s2)
	{
		scannedurllist.append(s1+"\n");
		if (s2=="Not Vulnerable")
		{
			result.setForeground(Color.GREEN);
		}
			result.append(s2+"\n");
			result.setForeground(new Color(0, 128, 0));
		
    }
	
}
