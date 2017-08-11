package burp;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.python.bouncycastle.util.Arrays;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import burp.KaliIntegrator;

public class BurpExtender  implements IBurpExtender,ITab 
{
  
    /**
    * 
    */
    private static final long serialVersionUID = 1L;
    public IBurpExtenderCallbacks callbacks;
    public JPanel panel=new JPanel();
    private final JLabel label1 = new JLabel("Kali Integrator");
    private final JLabel label2 = new JLabel("Extension that lets you run linux tools on the background for the request received by BurpTool");
    private final JLabel label3 = new JLabel("Command");
    private final JTextField commandField = new JTextField();
    private final JButton Add = new JButton("Add");
    private final JLabel label4 = new JLabel("Please enter the Command in the following pattern:\n fimap --url=GET_PARAMETER --post='POST_PARAMETER' --cookie='COOKIE_PARAMETER' --force-run");
    private final JLabel label5 = new JLabel("Console");
   
	
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
    	this.callbacks=callbacks;
    	
    	 
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
    	  
    	commandField.setBounds(104, 77, 770, 30);
  		commandField.setColumns(10);
  		
  		panel.setLayout(null);
  		label1.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 14));
  		label1.setForeground(new Color(255, 140, 0));
  		label1.setBounds(12, 12, 600, 24);
  		
  		panel.add(label1);
  		label2.setBounds(12, 35, 620, 30);
  		
  		panel.add(label2);
  		label3.setBounds(12, 84, 82, 15);
  		label3.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 12));
  		panel.add(label3);
  		
  		panel.add(commandField);
  		Add.setBounds(900, 77, 117, 30);
  		
  		panel.add(Add);
  		label4.setBounds(12,111, 900, 30);
  		
  		panel.add(label4);
  		label4.setFont(new Font("Lato Light", Font.BOLD, 12));
  		label5.setBounds(12, 147, 375, 40);
  		label5.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 12));
  		panel.add(label5);
  		
  		JTextArea textArea = new JTextArea();
  		textArea.setBounds(12, 183, 600, 137);
  		panel.add(textArea);
    	 SwingUtilities.invokeLater(new Runnable() 
         {
             @Override
             public void run()
             {
                 // main split pane

         		Add.addActionListener(new ActionListener() {
         			public void actionPerformed(ActionEvent arg0) {
         				String cmd=commandField.getText();
         				
         				if(!(cmd.contains("POST_PARAMETER")||cmd.contains("GET_PARAMETER")||cmd.contains("COOKIE_PARAMETER")))
         				{
         					cmd="";
         				}
         				
         				if(cmd.contains("fimap")&&cmd!="")
         				{
         					KaliIntegrator fimap=new KaliIntegrator("Fimap",commandField.getText());
         					fimap.registerCallbacks(callbacks);
         					textArea.append("Command Addedd Successfully:\n"+commandField.getText());
         					label4.setText("Success!!");
         				}
         				else if(cmd.contains("xsser"))
         				{
         					KaliIntegrator fimap=new KaliIntegrator("Xsser",commandField.getText());
         					fimap.registerCallbacks(callbacks);
         					
         					textArea.append("Command Addedd Successfully:\n"+commandField.getText());
         					label4.setText("Success!!");
         				}
         				else
         				{
         					label4.setText("No Command Set");
         				}
         			}
         		});

         		
         		callbacks.customizeUiComponent(label1);
         		callbacks.customizeUiComponent(label2);
         		callbacks.customizeUiComponent(label3);
         		callbacks.customizeUiComponent(label4);
         		callbacks.customizeUiComponent(label5);
         		callbacks.customizeUiComponent(panel);
         		callbacks.customizeUiComponent(commandField);
         		callbacks.customizeUiComponent(Add);
                callbacks.addSuiteTab(BurpExtender.this);
                 
             }
         });
    	
    }
	@Override
	public String getTabCaption() {
		// TODO Auto-generated method stub
		return "Configuration Page";
	}
	@Override
	public Component getUiComponent() {
		// TODO Auto-generated method stub
		return panel;
	}


}



