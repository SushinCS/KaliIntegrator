package burp;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;

public class TestFrame extends JPanel {

	/**
	 * Create the panel.
	 */
	public TestFrame() {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Decode on the Go!!");
		lblNewLabel.setBounds(12, 12, 426, 15);
		add(lblNewLabel);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(22, 70, 177, 109);
		add(textPane);
		
		JTextPane textPane_1 = new JTextPane();
		textPane_1.setBounds(241, 70, 177, 109);
		add(textPane_1);
		
		JButton btnNewButton = new JButton("Set");
		btnNewButton.setForeground(Color.GREEN);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				lblNewLabel.setBackground(Color.GREEN);
			}
		});
		btnNewButton.setBounds(43, 221, 134, 25);
		add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Select");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton_1.setBounds(226, 221, 117, 25);
		add(btnNewButton_1);

	}
}
