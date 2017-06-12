/**
 * 
 */
package burp;
import java.io.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.python.core.PyException;
import org.python.core.PyInteger;
import org.python.core.PyObject;



/**
 * @author SushinCS
 *
 */
public class BurpExtender implements IBurpExtender,ITab,IMessageEditorController, IExtensionStateListener, IHttpListener
{

	/* (non-Javadoc)
	 * @see burp.IBurpExtender#registerExtenderCallbacks(burp.IBurpExtenderCallbacks)
	 */
	
	IExtensionHelpers ehelpers;
	IBurpExtenderCallbacks ecallbacks;
	PrintWriter stderr;
	PrintWriter stdout;
	static int count=0;
	String param="";
	String cparam="";
	String ehost="testphp.vulnweb.com";
	IRequestInfo ereqinfo;
	IRequestInfo ereqinfo1;
	IHttpService eserv;
	
	public static KaliIntegratorUI1 kiui1=new KaliIntegratorUI1();
	public JPanel eframe;
	public JLabel elabel;
	public JTextField etextfield;
	
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		// TODO Auto-generated method stub
		this.ecallbacks=callbacks;
		ecallbacks.setExtensionName("Extension1");
		/*eframe=new JPanel();
		elabel=new JLabel("Statistics");
		 elabel.setBounds(50,100,80,30);  
		etextfield=new JTextField("Default Value is 0");
		 etextfield.setBounds(100,100,80,30);  
		ecallbacks.customizeUiComponent(etextfield);
		ecallbacks.customizeUiComponent(elabel);
		eframe.add(elabel);
		eframe.add(etextfield); 
		ecallbacks.customizeUiComponent(eframe);
		ecallbacks.customizeUiComponent(elabel);
		ecallbacks.customizeUiComponent(etextfield);
		ecallbacks.addSuiteTab(this);*/
		
		ecallbacks.customizeUiComponent(kiui1);
		ecallbacks.addSuiteTab(this);	
		
		this.ehelpers=ecallbacks.getHelpers();
		stderr=new PrintWriter(ecallbacks.getStderr(), true);
		stdout=new PrintWriter(ecallbacks.getStdout(), true);
		ecallbacks.registerExtensionStateListener(this); 
		ecallbacks.issueAlert("Extension Loaded Successfully");
		ecallbacks.registerHttpListener(this);

	}

	@Override
	public void extensionUnloaded() {
		ecallbacks.issueAlert("Extension Unloaded Successfully");
		
	}

	@Override
	public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
		// TODO Auto-generated method stub
		/*if (messageIsRequest==true)
		{
			stdout.println("Number of Message received:"+(count++)+"Message issued by Tool:"+ecallbacks.getToolName(toolFlag));
			ereqinfo=ehelpers.analyzeRequest(messageInfo);
			stdout.println("Request:"+ereqinfo.getHeaders());
			eserv=messageInfo.getHttpService();
			if(eserv.getHost().equalsIgnoreCase(this.ehost))
			{
				String temp="176.28.50.165";
				messageInfo.setHttpService(ehelpers.buildHttpService(temp, eserv.getPort(), eserv.getProtocol()));
				eserv=messageInfo.getHttpService();
				stdout.println("Modified Request sent:"+eserv.getHost());
			}
			else
			{
				stdout.println("Request is from :"+eserv.getHost());
			}
		}*/
		
		if(messageIsRequest==true)
		{
			ereqinfo=ehelpers.analyzeRequest(messageInfo);
			
		//stdout.println("Received URL :"+ereqinfo.getUrl());
			
		//	stdout.println(ereqinfo.getHeaders());
			IHttpRequestResponse ereqres[]=ecallbacks.getProxyHistory();
		//	stdout.println("History Length :"+ecallbacks.getProxyHistory().length);
			for(IHttpRequestResponse  i:ereqres)
			{
				ereqinfo1=ehelpers.analyzeRequest(i);
				stdout.println("History Contains :"+ereqinfo1.getUrl());
			}
			int cnt=0;
			
			for(IHttpRequestResponse  i:ereqres)
			{  
				ereqinfo1=ehelpers.analyzeRequest(i);
				stdout.println("Received URL:"+ereqinfo.getUrl());
				stdout.println("Compared URL:"+ereqinfo1.getUrl());
				stdout.println("compartision Status"+ereqinfo1.getUrl().equals(ereqinfo.getUrl()));
			     if (ereqinfo1.getUrl().equals(ereqinfo.getUrl()))
			     {
			    	 cnt++;
			    	 stdout.println("CNT value:"+cnt);
			    	
			     }
			   } 
			if(cnt==1)
			{
				stdout.println("Unique URL:"+ereqinfo.getUrl());
				
				List<IParameter> eparamlist=ereqinfo.getParameters();
				stdout.println("Parameter Size"+eparamlist.size());
				for(int j = 0; j < eparamlist.size(); j++) {
					
					if(eparamlist.get(j).getType()==2)
					{
						if(cparam.contains(eparamlist.get(j).getName())!=true)
						{ stdout.println("Cookie in given URL are:"+eparamlist.get(j).getName());
		            cparam=cparam.concat(eparamlist.get(j).getName()+"="+eparamlist.get(j).getValue()+";");
		            stdout.println("cookie List"+cparam);}
					}
					else
					{ if(eparamlist.get(j).getType()!=0)
					{
						stdout.println("Parameter in given URL are:"+eparamlist.get(j).getType());
			            param=param.concat(eparamlist.get(j).getName()+"="+eparamlist.get(j).getValue()+";");
			            stdout.println("PARAM List"+param);
					}}
		        }
				if(eparamlist.size()!=0)
				{
					stdout.println("Scanning the URL for LFI Vulnerability:");
				this.kaliintegrator(ereqinfo,param,cparam);
			}
			}
			stdout.println("\n\n");
		}
	
	}


	@Override
	public byte[] getRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTabCaption() {
		// TODO Auto-generated method stub
		return "Analyser";
	}

	@Override
	public Component getUiComponent() {
		// TODO Auto-generated method stub
		return kiui1;
	}

	@Override
	public IHttpService getHttpService() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void kaliintegrator(IRequestInfo ereqinfo2,String param2,String cparam)
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
	        	kiui1.append(ereqinfo2.getUrl().toString(),"Not Vulnerable");
	        }
	        else if (output.toString().contains("#::VULN INFO"))
	        {
	        	kiui1.append(ereqinfo2.getUrl().toString(),"Vulnerable");
	        }
	        interp.cleanup();
	        interp.close();
	}

}
