package burp;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class sss extends JPanel {

	/**
	 * Create the panel.
	 */
	 private JFileChooser fileChooser;
	 private int mode;
	    public static final int MODE_OPEN = 1;
	    public static final int MODE_SAVE = 2;
	    private JTextField textField;
	public sss() {
		setLayout(null);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				  buttonActionPerformed(e); 
			}
		});
		btnNewButton.setBounds(85, 76, 117, 25);
		add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(286, 188, 114, 19);
		add(textField);
		textField.setColumns(10);
		
		

	}
	
	 private void buttonActionPerformed(ActionEvent evt) {
	        if (MODE_OPEN==1) {
	            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
	            }
	        } else if (MODE_SAVE==1) {
	            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
	            }
	        }
	    }
}
