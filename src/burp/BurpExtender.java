package burp;

import java.awt.Component;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

public class BurpExtender extends AbstractTableModel implements IBurpExtender,IExtensionStateListener,ITab, IHttpListener, IMessageEditorController
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
                
                // register ourselves as an HTTP listener
                callbacks.registerExtensionStateListener(BurpExtender.this); 
        		callbacks.issueAlert("Extension Loaded Successfully");
                
                // register ourselves as an HTTP listener
                callbacks.registerHttpListener(BurpExtender.this);
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
        // only process responses
        if (!messageIsRequest)
        {
            // create a new log entry with the message details
            /*synchronized(log)
            {
                int row = log.size();
                log.add(new LogEntry((row+1), callbacks.saveBuffersToTempFiles(messageInfo), 
                        helpers.analyzeRequest(messageInfo)));
                fireTableRowsInserted(row, row);
            }*/
        }
        if(messageIsRequest==true)
		{
        	int row;
        	messageInfo.setComment("Processing");
        	synchronized(log)
            {
                 row = log.size();
                log.add(new LogEntry((row+1), callbacks.saveBuffersToTempFiles(messageInfo), 
                        helpers.analyzeRequest(messageInfo)));
                fireTableRowsInserted(row, row);
            }
        	
			ereqinfo=helpers.analyzeRequest(messageInfo);
			IHttpRequestResponse ereqres[]=callbacks.getProxyHistory();
		//	stdout.println("History Length :"+ecallbacks.getProxyHistory().length);
			for(IHttpRequestResponse  i:ereqres)
			{
				ereqinfo1=helpers.analyzeRequest(i);
			//	stdout.println("History Contains :"+ereqinfo1.getUrl());
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

					}}
					else
					{ if(eparamlist.get(j).getType()!=0)
					{
						
			            param=param.concat(eparamlist.get(j).getName()+"="+eparamlist.get(j).getValue()+";");
			       
					}}
		        }
				if(eparamlist.size()!=0)
				{

				stdout.println("Scanning the URL for LFI Vulnerability:");
				this.kaliintegrator(ereqinfo,param,cparam,messageInfo,row);
				param="";
				cparam="";
			}else
			{
				messageInfo.setComment("Cannot Be tested for LFI Vulnerability");
				log.set(row, new LogEntry((row+1), callbacks.saveBuffersToTempFiles(messageInfo), 
                        helpers.analyzeRequest(messageInfo)));
			}
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
        return 4;
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
    
    //
    // class to hold details of each log entry
    //
    
    private static class LogEntry
    {
        final int slno;
        final IHttpRequestResponsePersisted requestResponse;
        final URL url;
        final String method;
        public String status;

        LogEntry(int sl, IHttpRequestResponsePersisted requestResponse, IRequestInfo ereqinfoobj)
        {
            this.slno = sl;
            this.requestResponse = requestResponse;
            this.url = ereqinfoobj.getUrl();
            this.method=ereqinfoobj.getMethod();
            this.status=requestResponse.getComment();
            
        }
    }

	@Override
	public void extensionUnloaded() {
		callbacks.issueAlert("Extension Unloaded Successfully");
		
	}
	
	public synchronized void kaliintegrator(IRequestInfo ereqinfo2,String param2,String cparam,IHttpRequestResponse messageInfo,int row1)
	{
		PythonInterpreter interp = new PythonInterpreter(); 
		 interp.exec("import sys");
		 interp.exec("import os");
		 interp.exec("import subprocess");
		 interp.exec("import socket");
		 String cmd1="fimap --url="+ereqinfo2.getUrl()+" --post='"+param2+"'"+" --cookie='"+cparam+"'";
		 stdout.println(cmd1);   
		 interp.set("output", new PyString());
		 interp.set("cmd", cmd1);		 
		 interp.exec("output += subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)");
		
	        PyObject output = interp.get("output");
	        stdout.println("output is: " + output);
	        if(output.toString().contains("Target URL isn't affected by any file inclusion bug :("))
	        {
	        	messageInfo.setComment("Not Vulnerable");
	        	log.set(row1, new LogEntry((row1+1), callbacks.saveBuffersToTempFiles(messageInfo), 
                        helpers.analyzeRequest(messageInfo)));
	        }
	        else if (output.toString().contains("#::VULN INFO"))
	        {
	        	messageInfo.setComment("Vulnerable");
	        	log.set(row1, new LogEntry((row1+1), callbacks.saveBuffersToTempFiles(messageInfo), 
                        helpers.analyzeRequest(messageInfo)));
	        }
	        interp.cleanup();
	        interp.close();
	}
}