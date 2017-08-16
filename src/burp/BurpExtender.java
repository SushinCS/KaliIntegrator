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
    private final JLabel label3 = new JLabel("Command");
    private final JTextField commandField = new JTextField();
    private final JButton Add = new JButton("Add");
    private final JButton Remove = new JButton("Remove");
    private final JLabel label4 = new JLabel("Please enter the Command in the following pattern:\n fimap --url=GET_PARAMETER --post='POST_PARAMETER' --cookie='COOKIE_PARAMETER' --force-run");
    private final JLabel label5 = new JLabel("Console");
    private final List<CommandEntry> log = new ArrayList<CommandEntry>();
    Table logTable = new Table(BurpExtender.this);
    CommandEntry comnd;
    public JTabbedPaneCloseButton tab=new JTabbedPaneCloseButton();
    public KaliIntegrator fimap[]=new KaliIntegrator[10];
    
    public static int threads=0;
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
          
          
      
          
          
          JScrollPane scrollPane = new JScrollPane(logTable);
          logTable.setAutoCreateRowSorter(true);
          logTable.setBounds(12, 177, 820, 300);
          logTable.setVisible(true);
          logTable.setGridColor(Color.black);
          logTable.setForeground(Color.BLACK);
          logTable.setAutoCreateColumnsFromModel(true);
          logTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
          scrollPane.setBounds(12,177,820,466);
          panel.add(scrollPane);
          
          panel.add(Remove);
          Remove.setBounds(900,257, 117, 30);
          
          int row=log.size();
          
         SwingUtilities.invokeLater(new Runnable() 
         {
             @Override
             public void run()
             {
                 // main split pane
            	 tab.addTab("Configuration Window",panel);
            	 
                 Add.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent arg0) {
                         String cmd=commandField.getText();
                         
                         if(!(cmd.contains("POST_PARAMETER")||cmd.contains("GET_PARAMETER")||cmd.contains("COOKIE_PARAMETER"))&& threads>=11)
                         {
                             cmd="";
                         }
                         
                         if(cmd.contains("fimap")&&cmd!="")
                         {
                        	
                             fimap[threads]=new KaliIntegrator(tab,"Fimap",commandField.getText());
                             fimap[threads].registerCallbacks(callbacks);
                            comnd=new CommandEntry(row,commandField.getText());
                             //textArea.append("Command Addedd Successfully:\n"+commandField.getText());
                             label4.setText("Success!!");
                             log.add(comnd);
                             tab.addTab(fimap[threads].toolName, fimap[threads].getUiComponent());
                             fireTableRowsInserted(row,row);
                             threads++;
                         }
                         else if(cmd.contains("xsser"))
                         {
                             fimap[threads]=new KaliIntegrator(tab,"Xsser",commandField.getText());
                             fimap[threads].registerCallbacks(callbacks);
                             comnd=new CommandEntry(row,commandField.getText());
                             //textArea.append("Command Addedd Successfully:\n"+commandField.getText());
                             log.add(comnd);
                             label4.setText("Success!!");
                             tab.addTab(fimap[threads].toolName, fimap[threads].getUiComponent());
                             fireTableRowsInserted(row,row);
                             threads++;
                         }
                         else
                         {
                             label4.setText("No Command Set");
                         }
                     }
                 });
                 
                 
                 Remove.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent arg0) {
                         int select=logTable.getSelectedRow();
                         if(select==-1 || log.size()==0)
                         {
                        	 
                         }
                         else
                         {
                        	 log.remove(select);
                        	 callbacks.issueAlert("Number is "+select);
                        	 try {
                        		 tab.removeTabAt(select+1);
								fimap[select].remove();
							} catch (Throwable e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        	 
                        	 fimap[select]=new KaliIntegrator();
                   
                        	 fireTableRowsDeleted(select,select);
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
                 callbacks.customizeUiComponent(logTable);
                 callbacks.customizeUiComponent(tab);
                 callbacks.customizeUiComponent(scrollPane);
                callbacks.addSuiteTab(BurpExtender.this);
                 
             }
         });
        
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
                    return "SlNo";
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
                    return commandEntry.slno+1;
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

