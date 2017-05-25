/**
 * 
 */
package burp;
import java.io.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
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
	String ehost="testphp.vulnweb.com";
	IRequestInfo ereqinfo;
	IRequestInfo ereqinfo1;
	IHttpService eserv;
	public JPanel eframe;
	public JLabel elabel;
	public JTextField etextfield;
	
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		// TODO Auto-generated method stub
		this.ecallbacks=callbacks;
		ecallbacks.setExtensionName("Extension1");
		eframe=new JPanel();
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
			stdout.println(ereqinfo.getUrl());
//			List<IParameter> eparamlist=ereqinfo.getParameters();
//			for(int i = 0; i < eparamlist.size(); i++) {
//	            stdout.println(eparamlist.get(i).getName());
//	        }
			stdout.println(ereqinfo.getHeaders());
			IHttpRequestResponse ereqres[]=ecallbacks.getProxyHistory();
			stdout.println("History Length"+ecallbacks.getProxyHistory().length);
			for(IHttpRequestResponse  i:ereqres)
			{
				ereqinfo1=ehelpers.analyzeRequest(i);
				stdout.println("History Contains "+ereqinfo1.getUrl());
			}
			for(IHttpRequestResponse  i:ereqres)
			{  
				ereqinfo1=ehelpers.analyzeRequest(i);
			     if (ereqinfo1.getUrl()!= ereqinfo.getUrl())
			     {
			    	 stdout.println("Unique URL"+ereqinfo1.getUrl());
			     }
			   }  
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
		return eframe;
	}

	@Override
	public IHttpService getHttpService() {
		// TODO Auto-generated method stub
		return null;
	}

}
