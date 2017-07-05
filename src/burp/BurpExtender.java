package burp;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class BurpExtender extends AbstractTableModel implements IBurpExtender,IContextMenuFactory,IExtensionStateListener,ITab, IHttpListener, IMessageEditorController
{
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private JSplitPane splitPane;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private final List<LogEntry> log = new ArrayList<LogEntry>();
    private IHttpRequestResponse currentlyDisplayedItem;
    public PrintWriter stderr;
	public PrintWriter stdout;
	public IRequestInfo ereqinfo;
	public IRequestInfo ereqinfo1;
	public IRequestInfo ereqinfo2;
	String param="";
	String cparam="";

    //
    // implement IBurpExtender
    //
    
    @Override
    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks)
    {
        // keep a reference to our callbacks object
        this.callbacks = callbacks;
        
        // obtain an extension helpers object
        helpers = callbacks.getHelpers();
        
        // set our extension name
        callbacks.setExtensionName("Fimap");
        // create our UI
        SwingUtilities.invokeLater(new Runnable() 
        {
            @Override
            public void run()
            {
                // main split pane
                splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                        
                // table of log entries
                Table logTable = new Table(BurpExtender.this);
                logTable.setAutoCreateRowSorter(true);
                logTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                JScrollPane scrollPane = new JScrollPane(logTable);
                splitPane.setLeftComponent(scrollPane);

                // tabs with request/response viewers
                JTabbedPane tabs = new JTabbedPane();
                requestViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                responseViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                tabs.addTab("Request", requestViewer.getComponent());
                tabs.addTab("Response", responseViewer.getComponent());
                splitPane.setRightComponent(tabs);

                // customize our UI components
                callbacks.customizeUiComponent(splitPane);
                callbacks.customizeUiComponent(logTable);
                callbacks.customizeUiComponent(scrollPane);
                callbacks.customizeUiComponent(tabs);
                
        		stderr=new PrintWriter(callbacks.getStderr(), true);
        		stdout=new PrintWriter(callbacks.getStdout(), true);
                // add the custom tab to Burp's UI
                callbacks.addSuiteTab(BurpExtender.this);
                
                // register ourselves as an Extension State listener
                callbacks.registerExtensionStateListener(BurpExtender.this); 
        		callbacks.issueAlert("Extension Loaded Successfully");
                
                // register ourselves as an HTTP listener
                callbacks.registerHttpListener(BurpExtender.this);
                
            	// register ourselves as an Context Menu Factory
                callbacks.registerContextMenuFactory(BurpExtender.this);
            }
        });
    }

    //
    // implement ITab
    //

    @Override
    public String getTabCaption()
    {
        return "Fimap";
    }

    @Override
    public Component getUiComponent()
    {
        return splitPane;
    }

    //
    // implement IHttpListener
    //
    
    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo)
    {
       callbacks.issueAlert("ProcessHttpMessage invoked by"+toolFlag);
    	// only process responses
        if (!messageIsRequest && toolFlag!=8)
        {
            // create a new log entry with the message details
            synchronized(log)
            {
                int row = log.size();
                log.add(new LogEntry(row, callbacks.saveBuffersToTempFiles(messageInfo), 
                        helpers.analyzeRequest(messageInfo),toolFlag));  
                
                fireTableRowsInserted(row, row);
            }
        }
        // only process resquests
        if(messageIsRequest==true && toolFlag!=8)
		{
        	
        	  	
			ereqinfo=helpers.analyzeRequest(messageInfo);
			IHttpRequestResponse ereqres[]=callbacks.getProxyHistory();
			for(IHttpRequestResponse  i:ereqres)
			{
				ereqinfo1=helpers.analyzeRequest(i);
			}
			int cnt=0;
			
			for(IHttpRequestResponse  i:ereqres)
			{  
				ereqinfo1=helpers.analyzeRequest(i);
				
			     if (ereqinfo1.getUrl().equals(ereqinfo.getUrl()))
			     {
			    	 cnt++;
			     }
			   } 
			if(cnt==1)
			{
				List<IParameter> eparamlist=ereqinfo.getParameters();
				for(int j = 0,n=0; j < eparamlist.size(); j++) {
					
					if(eparamlist.get(j).getType()==2)
					{
						if(cparam.contains(eparamlist.get(j).getName())!=true)
						{ 
		            cparam=cparam.concat(eparamlist.get(j).getName()+"="+eparamlist.get(j).getValue()+";");
						}
					}
					else
					{ 
						if(eparamlist.get(j).getType()!=0)
						{
						
			            param=param.concat(eparamlist.get(j).getName()+"="+eparamlist.get(j).getValue()+";");
			       
						}
					}
		        }
				if(eparamlist.size()!=0)
				{
					synchronized(this)
					{
						messageInfo.setComment("Processing");
						stdout.println("Scanning the URL for LFI Vulnerability:");
						this.kaliintegrator(ereqinfo,param,cparam,messageInfo);
						param="";
						cparam="";
					}
			}else
			{
				
				messageInfo.setComment("Cannot Be tested for LFI Vulnerability");
			}
			}
			else
			{
				messageInfo.setComment("Duplicate Link/Already Tested");
			}
			stdout.println("\n\n");
		}
	
	
    }

    //
    // extend AbstractTableModel
    //
    
    @Override
    public int getRowCount()
    {
        return log.size();
    }

    @Override
    public int getColumnCount()
    {
        return 5;
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
                return "#";
            case 1:
                return "URL";
            case 2:
                return "Request Type";
            case 3:
                return "LFI Status";
            case 4:
                return "Tool";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        LogEntry logEntry = log.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return rowIndex;
            case 1:
                return logEntry.url.toString();
            case 2:
                return logEntry.method;
            case 3:
                return logEntry.status;
            case 4:
                return callbacks.getToolName(logEntry.tool);
            default:
                return "";
        }
    }

    //
    // implement IMessageEditorController
    // this allows our request/response viewers to obtain details about the messages being displayed
    //
    
    @Override
    public byte[] getRequest()
    {
        return currentlyDisplayedItem.getRequest();
    }

    @Override
    public byte[] getResponse()
    {
        return currentlyDisplayedItem.getResponse();
    }

    @Override
    public IHttpService getHttpService()
    {
        return currentlyDisplayedItem.getHttpService();
    }

    //
    // extend JTable to handle cell selection
    //
    
    private class Table extends JTable
    {
        public Table(TableModel tableModel)
        {
            super(tableModel);
        }
        
        @Override
        public void changeSelection(int row, int col, boolean toggle, boolean extend)
        {
            // show the log entry for the selected row
            LogEntry logEntry = log.get(row);
            requestViewer.setMessage(logEntry.requestResponse.getRequest(), true);
            responseViewer.setMessage(logEntry.requestResponse.getResponse(), false);
            currentlyDisplayedItem = logEntry.requestResponse;
            super.changeSelection(row, col, toggle, extend);
        }        
    }
    
	@Override
	public void extensionUnloaded() {
		callbacks.issueAlert("Extension Unloaded Successfully");
		
	}
	
	public void kaliintegrator(IRequestInfo ereqinfo2,String param2,String cparam,IHttpRequestResponse messageInfo)
	{
		PythonInterpreter interp = new PythonInterpreter(); 
		 interp.exec("import sys");
		 interp.exec("import os");
		 interp.exec("import subprocess");
		 interp.exec("import socket");
		 String cmd1="fimap --url="+ereqinfo2.getUrl()+" --post='"+param2+"'"+" --cookie='"+cparam+"' --force-run";
		 stdout.println(cmd1);   
		 interp.set("output", new PyString());
		 interp.set("cmd", cmd1);		 
		 interp.exec("output += subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)");
		
	        PyObject output = interp.get("output");
	        stdout.println("output is: " + output);
	        if(output.toString().contains("Target URL isn't affected by any file inclusion bug :("))
	        {
	        	messageInfo.setComment("Not Vulnerable");
	        }
	        else if (output.toString().contains("#::VULN INFO"))
	        {
	        	messageInfo.setHighlight("red");
	        	messageInfo.setComment("Vulnerable");
	        }
	        else
	        {
	        	messageInfo.setComment("Interupted!!");
	        }
	        interp.cleanup();
	        interp.close();
	}


    //
    // class to hold details of each log entry
    //
    
    private static class LogEntry
    {
        final int slno;
        final int tool;
        final IHttpRequestResponsePersisted requestResponse;
        final URL url;
        final String method;
        public String status;

        LogEntry(int sl, IHttpRequestResponsePersisted requestResponse, IRequestInfo ereqinfoobj,int tool)
        {
            this.slno = sl;
            this.tool=tool;
            this.requestResponse = requestResponse;
            this.url = ereqinfoobj.getUrl();
            this.method=ereqinfoobj.getMethod();
            this.status=requestResponse.getComment();
            
        }
    }

 // TODO Context Menu Creation
	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
		
		JMenuItem menu=new JMenuItem("Send it to Fimap");
		menu.addActionListener(new MenuItemListener(invocation));
		List<JMenuItem> item=new ArrayList<JMenuItem>();
		item.add(menu);
		return item;
	}


	
	class MenuItemListener extends Thread implements ActionListener
	{
		
		public IHttpRequestResponse[] reqres;
		public int flag;
		public IContextMenuInvocation invocation;
		
		public MenuItemListener(IContextMenuInvocation invocation) {
			super();
			this.reqres=invocation.getSelectedMessages();
			this.flag=invocation.getToolFlag();
		}

		public void run(){  
			for(int i=0;i<reqres.length;i++)
			{
				
				processHttpMessage(flag,true,reqres[i]); 
				
			}
			
			}  
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(flag==2)
			{
				IRequestInfo temp=helpers.analyzeRequest(reqres[0]);
				String host=temp.getUrl().getProtocol()+"://"+temp.getUrl().getHost();
				callbacks.issueAlert(host);
				this.reqres=callbacks.getSiteMap(host);
				callbacks.issueAlert("legth   "+this.reqres.length);
				callbacks.issueAlert("act   "+callbacks.getSiteMap("http://etechnik-wichmann.de").length);
				
			}
			callbacks.issueAlert("length    log"+reqres.length+log.size());
			MenuItemListener ne=new MenuItemListener(invocation);
			ne.start();
			
			
		}
		
	}


}