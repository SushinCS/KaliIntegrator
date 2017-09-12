/**
 * 
 *
 * @author Sushin
 * @version 1.0
 * @since 2017-10-20
 */

package burp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class KaliIntegrator extends AbstractTableModel implements IContextMenuFactory, IExtensionStateListener, IHttpListener, IMessageEditorController
{
	/**
	* 
	*/
	public static final long serialVersionUID = 1L;
	public String toolName = "default";
	public String command = "default";
	public IBurpExtenderCallbacks callbacks;
	public IExtensionHelpers helpers;
	public JSplitPane splitPane;
	public IMessageEditor requestViewer;
	public IMessageEditor responseViewer;
	public ITextEditor resultViewer;
	public final List<LogEntry> log = new ArrayList<LogEntry>();
	public List<String> parameterList = new ArrayList<String>();
	public final HashMap<URL, List<String>> loghm = new HashMap<URL, List<String>>();
	public IHttpRequestResponse currentlyDisplayedItem;
	public PrintWriter stderr;
	public PrintWriter stdout;
	public IRequestInfo reqinfoObjk;
	public IRequestInfo reqinfoObj;
	public String uparam = "";
	public String cparam = "";
	public String bparam = "";
	public String jparam = "";
	public String success = "";
	public String failure = "";
	public MyRenderer renderer = new MyRenderer();
	public JPanel panel = new JPanel();
	public JPanel insidepanel = new JPanel();
	public JTabbedPane tabs = new JTabbedPane();
	public JCheckBox checkbox = new JCheckBox();
	public JLabel checkboxlabel = new JLabel("On the Go Processing");
	private final JButton Start = new JButton("Start");
	private final JButton Stop = new JButton("Stop");
	private final JButton config = new JButton("Config");
	private final JButton export = new JButton("Export");
	public FileOperation fileobj = new FileOperation();

	public KaliIntegrator()
	{

	}

	public KaliIntegrator(String toolName, String command, String success, String failure)
	{
		this.toolName = toolName;
		this.command = command;
		this.success = success;
		this.failure = failure;

	}
	//
	// implement IBurpExtender
	//

	public void registerCallbacks(IBurpExtenderCallbacks callbacks)
	{
		// keep a reference to our callbacks object
		this.callbacks = callbacks;
		// obtain an extension helpers object
		helpers = callbacks.getHelpers();
		// set our extension name
		callbacks.setExtensionName(this.toolName);
		// create our UI
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				// main split pane

				panel.setLayout(new BorderLayout());
				insidepanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				checkbox.setEnabled(true);

				insidepanel.add(Start);
				Start.setBounds(900, 100, 120, 30);
				Start.setEnabled(false);
				insidepanel.add(Stop);

				Stop.setBounds(900, 100, 120, 30);
				insidepanel.add(config);
				config.setBounds(900, 100, 120, 30);
				insidepanel.add(export);
				export.setBounds(900, 100, 120, 30);
				insidepanel.add(checkboxlabel);
				insidepanel.add(checkbox);
				checkbox.setSelected(true);

				checkbox.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (checkbox.isSelected())
						{
							callbacks.registerHttpListener(KaliIntegrator.this);
						}
						else
						{
							callbacks.removeHttpListener(KaliIntegrator.this);
						}
					}
				});

				Start.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						start();
					}
				});

				Stop.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						stop();
					}
				});

				config.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						HashMap<String, String[]> processedlist = new HashMap<String, String[]>();
						processedlist = fileobj.processXML();
						if (processedlist != null)
						{
							for (String key : processedlist.keySet())
							{
								String[] temp = processedlist.get(key);
								setString(temp[2], temp[3]);
							}
							JOptionPane.showMessageDialog(null, "Success and Failure String updated");
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Invalid File");
						}
					}
				});

				export.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							exportTable();
						}
						catch (IOException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

				// table of log entries
				Table logTable = new Table(KaliIntegrator.this);
				logTable.setAutoCreateRowSorter(true);
				logTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				logTable.setAlignmentY(JTable.LEFT_ALIGNMENT);
				logTable.setDefaultRenderer(Object.class, renderer);
				JScrollPane scrollPane = new JScrollPane(logTable);
				splitPane.setLeftComponent(scrollPane);

				// tabs with request/response viewers

				requestViewer = callbacks.createMessageEditor(KaliIntegrator.this, false);
				responseViewer = callbacks.createMessageEditor(KaliIntegrator.this, false);
				resultViewer = callbacks.createTextEditor();
				tabs.addTab("Request", requestViewer.getComponent());
				tabs.addTab("Response", responseViewer.getComponent());
				tabs.addTab("Vulnerablity Status", resultViewer.getComponent());
				splitPane.setRightComponent(tabs);

				panel.add(insidepanel, BorderLayout.NORTH);
				panel.add(splitPane, BorderLayout.CENTER);
				// customize our UI components
				callbacks.customizeUiComponent(splitPane);
				callbacks.customizeUiComponent(logTable);
				callbacks.customizeUiComponent(scrollPane);
				callbacks.customizeUiComponent(tabs);
				callbacks.customizeUiComponent(panel);
				callbacks.customizeUiComponent(checkbox);
				stderr = new PrintWriter(callbacks.getStderr(), true);
				stdout = new PrintWriter(callbacks.getStdout(), true);

				// add the custom tab to Burp's UI

				// register ourselves as an Extension State listener
				callbacks.registerExtensionStateListener(KaliIntegrator.this);

				// register ourselves as an HTTP listener
				callbacks.registerHttpListener(KaliIntegrator.this);

				// register ourselves as an Context Menu Factory
				callbacks.registerContextMenuFactory(KaliIntegrator.this);

			}
		});
	}

	void stop()
	{
		callbacks.removeExtensionStateListener(KaliIntegrator.this);
		callbacks.removeHttpListener(KaliIntegrator.this);
		callbacks.removeContextMenuFactory(KaliIntegrator.this);
		Start.setEnabled(true);
		Stop.setEnabled(false);
	}

	void start()
	{
		callbacks.registerExtensionStateListener(KaliIntegrator.this);
		callbacks.registerHttpListener(KaliIntegrator.this);
		callbacks.registerContextMenuFactory(KaliIntegrator.this);
		Start.setEnabled(false);
		Stop.setEnabled(true);
	}
	//
	// implement ITab
	// oveContextMenuFactory(KaliIntegrator.this);

	public void remove() throws Throwable
	{
		this.callbacks.removeContextMenuFactory(this);
		this.callbacks.removeExtensionStateListener(this);
		this.callbacks.removeHttpListener(this);

	}

	public String getTabCaption()
	{
		return this.toolName;
	}

	public Component getUiComponent()
	{

		return this.panel;
	}

	//
	// implement IHttpListener
	//

	@Override
	public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo)
	{

		// only process responses
		if (messageIsRequest == false && toolFlag != 8)
		{
			int row = log.size();
			String output = "Processing";
			int uniqueFlag = 0;
			reqinfoObj = helpers.analyzeRequest(messageInfo);
			parameterList = this.paramlist(reqinfoObj);

			synchronized (this.log)
			{
				messageInfo.setComment("Processing..");
				LogEntry beforeProcessing = new LogEntry(row, callbacks.saveBuffersToTempFiles(messageInfo), helpers.analyzeRequest(messageInfo), toolFlag, output, parameterList);
				log.add(beforeProcessing);
				fireTableRowsInserted(row, row);

			}
			reqinfoObj = helpers.analyzeRequest(messageInfo);

			if (loghm.size() == 0)
			{
				callbacks.issueAlert("No log");
				uniqueFlag = 0;
			}
			else
			{
				synchronized (this.loghm)
				{
					List<String> temp = this.paramlist(reqinfoObj);
					if (temp.isEmpty())
					{
						output = "Cannot Be tested for Vulnerability";
						messageInfo.setComment("Cannot Be tested for Vulnerability");

					}
					else if (loghm.get(reqinfoObj.getUrl()) != null
							&& (temp.containsAll(loghm.get(reqinfoObj.getUrl()))) && (temp.size() != 0))
					{

						uniqueFlag = 1;
						callbacks.issueAlert("Duplicate Link");

					}
					else if (temp.size() != 0)
					{
						output = "Cannot Be tested for Vulnerability";
						messageInfo.setComment("Cannot Be tested for Vulnerability");

					}
					else
					{
						callbacks.issueAlert("Unique link");
						uniqueFlag = 0;

					}
				}
			}

			if (uniqueFlag == 0)
			{

				List<IParameter> eparamlist = reqinfoObj.getParameters();
				cparam = getrequestparameter(eparamlist, 2);
				uparam = getrequestparameter(eparamlist, 0);
				bparam = getrequestparameter(eparamlist, 1);
				jparam = getrequestparameter(eparamlist, 6);
				if (eparamlist.size() != 0)
				{
					synchronized (this.loghm)
					{
						loghm.put(reqinfoObj.getUrl(), parameterList);
					}
					synchronized (this)
					{
						stdout.println("Scanning the Request for Vulnerability:" + this.toolName);
						output = this.kaliintegrator(reqinfoObj, uparam, bparam, cparam, jparam, messageInfo, command);
						callbacks.issueAlert(output);
						uparam = "";
						cparam = "";
						bparam = "";
						jparam = "";

					}
				}
				else
				{
					output = "Cannot Be tested for Vulnerability";
					messageInfo.setComment("Cannot Be tested for Vulnerability");

				}
			}
			else
			{
				messageInfo.setComment("Duplicate Link/Already Tested");
				output = "Duplicate Link/Already Tested";
				System.out.println("URL:Message_9" + reqinfoObj.getUrl().toString() + ":" + messageInfo.getComment());
			}
			stdout.println("\n\n");
			synchronized (this.log)
			{

				LogEntry afterProcessing = new LogEntry(row, callbacks.saveBuffersToTempFiles(messageInfo), helpers.analyzeRequest(messageInfo), toolFlag, output, parameterList);
				loghm.put(reqinfoObj.getUrl(), parameterList);
				// System.out.println("asasURL aaaa:" + reqinfoObj.getUrl() +
				// ":::" + messageInfo.getComment());
				System.out.println("URL:Message_10" + reqinfoObj.getUrl().toString() + ":" + messageInfo.getComment());
				this.setValueAt(afterProcessing, row, row);
				fireTableRowsUpdated(row, row);
			}

		}

	}

	// To get required parameters from request
	public String getrequestparameter(List<IParameter> temp_paramlist, int type)
	{
		String parameter = "";
		for (int j = 0; j < temp_paramlist.size(); j++)
		{
			// System.out.println("Parameter List" +
			// temp_paramlist.get(j).getType() + ":"
			// + temp_paramlist.get(j).getName());

			if (temp_paramlist.get(j).getType() == type)
			{
				if (parameter.contains(temp_paramlist.get(j).getName()) != true)
				{
					parameter = parameter.concat(temp_paramlist.get(j).getName() + "="
							+ temp_paramlist.get(j).getValue() + ";");
				}
			}

		}
		return parameter;
	}

	// To Compare the Strings
	public List<String> paramlist(IRequestInfo req)
	{
		List<IParameter> eparamlist = req.getParameters();
		List<String> paramlist = new ArrayList<String>();
		if (eparamlist.isEmpty())
		{
			return paramlist;
		}
		else
		{
			for (int i = 0; i < eparamlist.size(); i++)
			{
				paramlist.add(eparamlist.get(i).getName());
			}
			return paramlist;
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
			return "Status";
		case 4:
			return "Tool";
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

		LogEntry logEntry = log.get(rowIndex);

		switch (columnIndex)
		{
		case 0:
			return rowIndex + 1;
		case 1:
			return logEntry.url.toString();
		case 2:
			return logEntry.method;
		case 3:
			// System.out.println("Url:Status:" + logEntry.url.toString() + " :
			// " + logEntry.status);
			return logEntry.status;
		case 4:
			return callbacks.getToolName(logEntry.tool);
		default:
			return "";
		}

	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{

		LogEntry logEntry = (LogEntry) aValue;
		log.set(rowIndex, logEntry);
	}

	//
	// implement IMessageEditorController
	// this allows our request/response viewers to obtain details about the
	// messages being displayed
	//

	@Override
	public byte[] getRequest()
	{
		return currentlyDisplayedItem.getRequest();
	}

	@Override
	public byte[] getResponse()
	{
		if (currentlyDisplayedItem.getResponse() == null)
		{
			return new byte[0];
		}

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
	public void setString(String success, String failure)
	{
		this.success = success;
		this.failure = failure;
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
			// row=convertRowIndexToView(row);
			// callbacks.issueAlert("row:"+row+"column:"+col);
			int row1 = convertRowIndexToView(row);
			int col1 = convertColumnIndexToView(col);
			int row2 = convertRowIndexToModel(row);
			int col2 = convertColumnIndexToModel(col);

			// callbacks.issueAlert("row1:"+row1+"column1:"+col1);
			// callbacks.issueAlert("row2:"+row2+"column2:"+col2);
			LogEntry logEntry = log.get(row2);
			requestViewer.setMessage(logEntry.requestResponse.getRequest(), true);
			if (logEntry.requestResponse.getResponse() == null)
			{
				responseViewer.setMessage(new byte[0], false);
			}
			else
			{
				responseViewer.setMessage(logEntry.requestResponse.getResponse(), false);
			}
			resultViewer.setText(logEntry.voutput.getBytes());
			currentlyDisplayedItem = logEntry.requestResponse;
			super.changeSelection(row, col, toggle, extend);

		}

	}

	@Override
	public void extensionUnloaded()
	{
		callbacks.issueAlert("Extension Unloaded Successfully");

	}

	public String kaliintegrator(IRequestInfo reqinfoObjk, String uparam, String bparam, String cparam, String jparam,
			IHttpRequestResponse messageInfo, String command)
	{
		PythonInterpreter interp = new PythonInterpreter();
		interp.exec("import sys");
		interp.exec("import os");
		interp.exec("import subprocess");
		interp.exec("import socket");
		String cmd1 = command;
		stdout.println(cmd1);

		cmd1 = cmd1.replace("POST_PARAMETER", bparam);
		cmd1 = cmd1.replace("COOKIE_PARAMETER", cparam);
		cmd1 = cmd1.replace("URL", reqinfoObjk.getUrl().toString());
		cmd1 = cmd1.replace("GET_PARAMETER", uparam);
		cmd1 = cmd1.replace("JSON_PARAMETER", jparam);

		stdout.println(cmd1);

		interp.set("output", new PyString());
		interp.set("cmd", cmd1);
		interp.exec("output += subprocess.check_output(cmd, shell=True, stderr=subprocess.STDOUT)");

		PyObject output = interp.get("output");
		stdout.println("Output is: " + output);
		if (output.toString().contains(this.failure))
		{
			messageInfo.setComment("Not Vulnerable");
		}
		else if (output.toString().contains(this.success))
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
		return output.asString();
	}

	public void exportTable() throws IOException
	{
		File file = fileobj.getfile();

		FileWriter out = new FileWriter(file);
		for (int i = 0; i < this.getColumnCount(); i++)
		{
			out.write(this.getColumnName(i) + "\t");
		}
		out.write("\n");

		for (int i = 0; i < this.getRowCount(); i++)
		{
			for (int j = 0; j < this.getColumnCount(); j++)
			{
				out.write(this.getValueAt(i, j).toString() + "\t");
			}
			out.write("\n");
		}
		JOptionPane.showMessageDialog(null, "Data exported Successfully to loacation" + file.getAbsolutePath());
		out.close();

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
		public String voutput;
		public List<String> paramlist = new ArrayList<String>();

		LogEntry(int sl, IHttpRequestResponsePersisted requestResponse, IRequestInfo ereqinfoobj, int tool, String output, List<String> paramlist)
		{
			this.slno = sl;
			this.tool = tool;
			this.requestResponse = requestResponse;
			this.url = ereqinfoobj.getUrl();
			this.method = ereqinfoobj.getMethod();
			if (requestResponse.getComment() == null)
			{
				this.status = "Cannotbe tested for Vulnerability";
			}
			else
			{
				this.status = requestResponse.getComment();
			}
			this.voutput = output;
			this.paramlist = paramlist;

		}

	}

	// TODO Context Menu Creation
	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation)
	{

		if (this.toolName != "default"
				&& ((invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_TARGET_SITE_MAP_TREE)
						|| (invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_TARGET_SITE_MAP_TABLE)
						|| (invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_PROXY_HISTORY)))
		{

			JMenuItem menu = new JMenuItem("Send to " + this.toolName);
			menu.addActionListener(new MenuItemListener(invocation));
			List<JMenuItem> item = new ArrayList<JMenuItem>();
			item.add(menu);

			return item;
		}
		else
		{
			return null;
		}
	}

	class MenuItemListener extends Thread implements ActionListener
	{

		public IHttpRequestResponse[] reqres;
		public int flag;
		public IContextMenuInvocation invocation1;

		public MenuItemListener(IContextMenuInvocation invocation)
		{
			super();
			this.invocation1 = invocation;
			this.reqres = this.invocation1.getSelectedMessages();
			callbacks.issueAlert("Invocation" + invocation1.getInvocationContext() + ":::"
					+ IContextMenuInvocation.CONTEXT_TARGET_SITE_MAP_TREE);
			if (invocation1.getInvocationContext() == IContextMenuInvocation.CONTEXT_TARGET_SITE_MAP_TREE)
			{
				IRequestInfo temp = helpers.analyzeRequest(reqres[0]);
				String host = temp.getUrl().getProtocol() + "://" + temp.getUrl().getHost();
				callbacks.issueAlert(host);
				this.reqres = callbacks.getSiteMap(host);
			}
			this.flag = this.invocation1.getToolFlag();
		}

		public void run()
		{
			for (int i = 0; i < reqres.length; i++)
			{
				callbacks.issueAlert("Call" + i + ":" + flag + ":" + reqres.length);
				processHttpMessage(flag, false, reqres[i]);

			}

		}

		@Override
		public void actionPerformed(ActionEvent arg0)
		{

			MenuItemListener ne = new MenuItemListener(this.invocation1);
			ne.start();

		}

	}

}

class MyRenderer extends DefaultTableCellRenderer
{
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
	{
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (isSelected)
		{
			c.setBackground(table.getSelectionBackground());
		}
		else if (value != null && value == "Vulnerable")
		{
			c.setBackground(Color.RED);
		}
		else
		{
			c.setBackground(Color.WHITE);
		}
		return c;
	}
}
