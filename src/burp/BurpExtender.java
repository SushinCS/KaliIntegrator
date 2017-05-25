/**
 * 
 */
package burp;
import java.io.*;
import java.awt.*;
import javax.swing.*;


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
		if (messageIsRequest==true)
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
