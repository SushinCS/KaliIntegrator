package burp;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import javax.swing.plaf.basic.BasicTabbedPaneUI.TabbedPaneLayout;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.python.bouncycastle.util.Arrays;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import burp.KaliIntegrator;
import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;


public class BurpExtender extends AbstractTableModel  implements IBurpExtender,ITab 
{
  
    /**
    * 
    */
    private static final long serialVersionUID = 1L;
    public IBurpExtenderCallbacks callbacks;
    public JPanel panel=new JPanel();
    private final JLabel label1 = new JLabel("Kali Integrator");
    private final JLabel label2 = new JLabel("Extension that lets you run linux tools on the background for the request received by BurpTool");
    private final JLabel label6 = new JLabel("CommandList");
    private final JLabel label3 = new JLabel("Command");
    private final JTextField commandField = new JTextField();
    private final JButton Add = new JButton("Add");
    private final JButton Remove = new JButton("Remove");
    private final JButton commandAdd = new JButton("Add");
    public JButton config = new JButton("config");
    public JComboBox<String> commandList = new JComboBox();
    private final JLabel label4 = new JLabel("<html><p>Please enter the Command in the following pattern:<br> fimap --url=GET_PARAMETER --post='POST_PARAMETER' --cookie='COOKIE_PARAMETER' --force-run<br>select the config file containing success and error message Keywords for the tool</p></html>");
    private final JLabel label5 = new JLabel("Active Tool List");
    private final List<CommandEntry> log = new ArrayList<CommandEntry>();
    private final HashMap<String,String> list = new HashMap<String,String>();
    Table logTable = new Table(BurpExtender.this);
    CommandEntry comnd;
    public JTabbedPaneCloseButton tab=new JTabbedPaneCloseButton();
    public KaliIntegrator fimap[]=new KaliIntegrator[10];
    public String successStr="";
    public String failureStr="";
   
    public static int threads=0;
    public int row=log.size();
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        this.callbacks=callbacks;
        
         
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
          
        
          commandField.setColumns(10);
          
          panel.setLayout(null);
         
          
          label1.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 14));
          label1.setForeground(new Color(255, 140, 0));
          label1.setBounds(12, 15, 600, 24);
          panel.add(label1);
          
          label2.setBounds(12, 45, 620, 30);
          panel.add(label2);
          
          label6.setBounds(12, 75, 620, 30);
          label6.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 12));
          panel.add(label6);
          
          commandList.setBounds(105,75,500,30);
          
          panel.add(commandList);
          
          commandAdd.setBounds(900, 75, 120, 30);
          panel.add(commandAdd);
       
          
          label3.setBounds(12,140, 82, 15);
          label3.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 12));
          panel.add(label3);
          
          panel.add(commandField);
          commandField.setBounds(105, 135, 770, 30);
          
          label4.setBounds(12,165, 1200, 65);
          label4.setFont(new Font("Lato Light", Font.BOLD, 12));
          panel.add(label4);
          
          Add.setBounds(1050, 135, 117, 30);
          Add.setEnabled(false);
          panel.add(Add);
          
          config.setBounds(900, 135, 117, 30);
          panel.add(config);
          
          label5.setBounds(12, 195, 375, 70);
          label5.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 12));
          panel.add(label5);
          
         
          
          panel.add(Remove);
          Remove.setBounds(900,250, 117, 30);
          
          
          JScrollPane scrollPane = new JScrollPane(logTable);
          logTable.setAutoCreateRowSorter(true);
          
          logTable.setVisible(true);
          logTable.setGridColor(Color.black);
          logTable.setForeground(Color.BLACK);
          logTable.setAutoCreateColumnsFromModel(true);
          
          logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
          logTable.getColumnModel().getColumn(0).setPreferredWidth(60);
          logTable.getColumnModel().getColumn(1).setPreferredWidth(800);
          scrollPane.setBounds(12,250,862,300);
          panel.add(scrollPane);
          
          commandList.addItem("Fimap");
          commandList.addItem("Xsser");
          list.put("Fimap","fimap --url=GET_PARAMETER --post='POST_PARAMETER' --cookie='COOKIE_PARAMETER' --force-run");
          list.put("Xsser","xsser -u \"GET_PARAMETER\" -s");
          
          
         SwingUtilities.invokeLater(new Runnable() 
         {
             @Override
             public void run()
             {
                 // main split pane
            	 tab.addTab("Configuration Window",panel);
            	 
            	 commandAdd.addActionListener(new ActionListener() {
         			public void actionPerformed(ActionEvent arg0) {
         				
         				if(commandList.getSelectedObjects()==null)
         				{
         					label4.setText("Plase select one of the tools from the dropdown list or Enter the command Manually ");
         				}
         				else
         				{
         					addIntegrator((String)commandList.getSelectedItem(),list.get(commandList.getSelectedItem()),row);
         				}
         			}
         		});
            	 
                 Add.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent arg0) {
                         String cmd=commandField.getText();
                         if(cmd==null)
                         {
                        	 label4.setText("No Command Set");
                        	 cmd="";
                         }
                         
                         else if(!(cmd.contains("POST_PARAMETER")||cmd.contains("GET_PARAMETER")||cmd.contains("COOKIE_PARAMETER"))&& threads>=11)
                         {
                             cmd="";
                         }
                         
                         if(cmd!="")
                         {
                        	 
                        	 addIntegrator(cmd.substring(0, cmd.indexOf(" ")),commandField.getText(),row);
                             
                         }
                         else
                         {
                             label4.setText("No Command Set");
                         }
                     }
                 });
                 
                 
                 Remove.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent arg0) {
                    	 int select=log.get(logTable.getSelectedRow()).slno;
                         int selectedrow=logTable.getSelectedRow();
                         if(selectedrow==-1 || log.size()==0)
                         {
                        	 callbacks.issueAlert("Number is cannot"); 
                         }
                         else
                         {
                        	 log.remove(selectedrow);
                        	 callbacks.issueAlert("Number is "+select);
                        	 callbacks.issueAlert("Number is s"+selectedrow);
                        	 try {
                        		 tab.removeTabAt(selectedrow+1);
								fimap[select].remove();
							} catch (Throwable e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        	 
                        	 fimap[select]=new KaliIntegrator();
                   
                        	 fireTableRowsDeleted(selectedrow,selectedrow);
                         }
                     }
                 });

                 
         		config.addActionListener(new ActionListener() {
         			public void actionPerformed(ActionEvent e) {
         			gettext(e);
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
                 callbacks.customizeUiComponent(logTable);
                 callbacks.customizeUiComponent(tab);
                 callbacks.customizeUiComponent(commandList);
                 callbacks.customizeUiComponent(label6);
                 callbacks.customizeUiComponent(commandAdd);
                 callbacks.customizeUiComponent(scrollPane);
                 callbacks.addSuiteTab(BurpExtender.this);
                 
             }
         });
        
    }
    public void addIntegrator(String name,String command,int row)
    {
    	if(name == "Fimap")
    	{
    		this.successStr="#::VULN INFO";
    		this.failureStr="Target URL isn't affected by any file inclusion bug";
    	}
    	else if(name=="Xsser")
    	{
    		this.successStr="Failed: 0";
    		this.failureStr="Could not find any vulnerability";
    	}
    	
    	
    	
    	fimap[threads]=new KaliIntegrator(name,command,this.successStr,this.failureStr);
        fimap[threads].registerCallbacks(callbacks);
        comnd=new CommandEntry(threads,command);
        
        label4.setText("Success!!");
        log.add(comnd);
        tab.addTab(fimap[threads].toolName, fimap[threads].getUiComponent());
        fireTableRowsInserted(row,row);
        threads++;
    }
    
    public void gettext(ActionEvent e)
    {
    	JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(null);
			System.out.println("Selected file: " +result);
			if (result == JFileChooser.APPROVE_OPTION) {
			    File selectedFile = fileChooser.getSelectedFile();
			   try {
			Scanner sc=new Scanner(selectedFile);
			String scan="";
		    while (sc.hasNextLine()) 
		    {
	            scan = scan+sc.nextLine();
	        }
			  System.out.println("scan contains " + scan);
			  
			this.failureStr=scan.substring(scan.indexOf("--errorstart--"), scan.indexOf("--errorend--"));
			this.successStr=scan.substring(scan.indexOf("--successstart--"), scan.indexOf("--successend--"));
			
			if(failureStr!=null&&successStr!=null&&failureStr!=""&&successStr!="")
			{
				Add.setEnabled(true);
			}
			else
			{
				label4.setText("Config file selected is not valid");
			}
			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			    
			    System.out.println("Selected file: " + selectedFile.toString());
			}
    }
    
    @Override
    public String getTabCaption() {
        // TODO Auto-generated method stub
        return "KaliIntegrator";
    }
    @Override
    public Component getUiComponent() {
        // TODO Auto-generated method stub
        return tab;
    }


     public int getRowCount()
        {
            return log.size();
        }

        @Override
        public int getColumnCount()
        {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    return "Slno";
                case 1:
                    return "Command";
                
                default:
                    return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            
            CommandEntry commandEntry = log.get(rowIndex);

            switch (columnIndex)
            {
                case 0:
                    return rowIndex+1;
                case 1:
                    return commandEntry.command;
                
                default:
                    return "";
            }
        
        }
        
        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            
            CommandEntry commandEntry =(CommandEntry) aValue;
            log.set(rowIndex,commandEntry);
        }
        

    
    private static class CommandEntry
    {
        final int slno;
        final String command;
        

        CommandEntry(int sl, String command)
        {
            this.slno = sl;
            this.command=command;
            
        }

    }
    
    private class Table extends JTable
    {
        /**
        * 
        */
        private static final long serialVersionUID = 1L;

        public Table(TableModel tableModel)
        {
            super(tableModel);
        }
        
        @Override
        public void changeSelection(int row, int col, boolean toggle, boolean extend)
        {
            // show the log entry for the selected row
            //row=convertRowIndexToView(row);
           
           
            int row1=convertRowIndexToView(row);
            int col1=convertColumnIndexToView(col);
            int row2=convertRowIndexToModel(row);
            int col2=convertColumnIndexToModel(col);
            CommandEntry commandEntry = log.get(row2);


            super.changeSelection(row, col, toggle, extend);
            
        }  
        
    }
    
    
}

