package burp;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class sss extends JPanel {

	/**
	 * Create the panel.
	 */
	public sss() {
		setLayout(null);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(103, 109, 239, 34);
		
		comboBox.addItem("Fimap");
		comboBox.addItem("Xsser");
		
	
		add(comboBox);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(0, 165, 117, 25);
		add(btnNewButton);

	}
}
