package burp;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import java.awt.event.ActionListener;

public class ss extends JPanel {

	/**
	 * Create the panel.
	 */
	public ss() {
		setLayout(null);
		
		JButton config = new JButton("New button");
		config.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showOpenDialog(ss.this);
				System.out.println("Selected file: " +result);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = fileChooser.getSelectedFile();
				    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
				}
			}
		});
		config.setBounds(148, 73, 117, 16);
		add(config);
		
	    

	}
}
