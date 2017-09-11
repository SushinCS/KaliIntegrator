/**
 * 
 *
 * @author Sushin
 * @version 1.0
 * @since 2017-10-20
 */

package burp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class BurpExtender extends AbstractTableModel implements IBurpExtender, ITab
{

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	public IBurpExtenderCallbacks callbacks;
	public JPanel panel = new JPanel();
	private final JLabel label1 = new JLabel("Kali Integrator");
	private final JLabel label2 = new JLabel("Extension that lets you run linux tools on the background for the request received by BurpTool");
	private final JLabel label3 = new JLabel("Command");
	private final JLabel label4 = new JLabel("<html><p>Please enter the Command in the following pattern:<br> fimap --url=GET_PARAMETER --post='POST_PARAMETER' --cookie='COOKIE_PARAMETER' --force-run<br>select the config file containing success and error message Keywords for the tool</p></html>");
	private final JLabel label5 = new JLabel("Active Tool List");
	private final JLabel label6 = new JLabel("CommandList");
	private final JButton lconfig = new JButton("Load from Config file");
	private final JButton commandAdd = new JButton("Add");
	private final JTextField commandField = new JTextField();
	private final JButton config = new JButton("Config");
	private final JButton Add = new JButton("Add");
	private final JButton Remove = new JButton("Remove");
	public JComboBox<String> commandList = new JComboBox();
	private List<CommandEntry> log = new ArrayList<CommandEntry>();
	private HashMap<String, String[]> list = new HashMap<String, String[]>();
	Table logTable = new Table(BurpExtender.this);
	CommandEntry comnd;
	public JTabbedPane tab = new JTabbedPane();
	public KaliIntegrator fimap[] = new KaliIntegrator[10];
	public String successStr = "";
	public String failureStr = "";
	public static int threads = 0;
	public int row = log.size();
	public FileOperation fileobj = new FileOperation();

	public final HashMap<String, Integer> loghm = new HashMap<String, Integer>();

	/**
	 * @wbp.parser.entryPoint
	 */
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
	{
		this.callbacks = callbacks;

		panel.setLayout(null);
		panel.setBackground(Color.WHITE);

		commandField.setColumns(10);
		panel.setLayout(null);

		label1.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 14));
		label1.setForeground(new Color(255, 140, 0));
		label1.setBounds(12, 10, 600, 24);
		panel.add(label1);

		label2.setBounds(12, 39, 816, 30);
		panel.add(label2);

		lconfig.setBounds(12, 81, 200, 30);
		panel.add(lconfig);

		label6.setBounds(12, 142, 91, 30);
		label6.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 12));
		panel.add(label6);

		commandList.setBounds(112, 142, 617, 30);

		panel.add(commandList);
		commandList.addItem("--Select--");

		commandAdd.setBounds(741, 142, 120, 30);
		panel.add(commandAdd);

		label3.setBounds(12, 196, 82, 30);
		label3.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 12));
		panel.add(label3);

		panel.add(commandField);
		commandField.setBounds(112, 197, 617, 30);

		label4.setBounds(12, 249, 700, 76);
		label4.setFont(new Font("Lato Light", Font.BOLD, 12));
		panel.add(label4);

		Add.setBounds(883, 196, 117, 30);
		Add.setEnabled(false);
		panel.add(Add);

		config.setBounds(741, 196, 117, 30);
		panel.add(config);

		label5.setBounds(12, 351, 103, 15);
		label5.setFont(new Font("DejaVu Sans Condensed", Font.BOLD, 12));
		panel.add(label5);

		panel.add(Remove);
		Remove.setBounds(711, 493, 117, 30);

		JScrollPane scrollPane = new JScrollPane(logTable);
		logTable.setAutoCreateRowSorter(true);

		logTable.setVisible(true);
		logTable.setGridColor(Color.black);
		logTable.setForeground(Color.BLACK);
		logTable.setAutoCreateColumnsFromModel(true);

		logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		logTable.getColumnModel().getColumn(0).setPreferredWidth(60);
		logTable.getColumnModel().getColumn(1).setPreferredWidth(800);
		scrollPane.setBounds(12, 378, 680, 251);
		panel.add(scrollPane);

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				// main split pane
				tab.addTab("Configuration Window", panel);

				commandAdd.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{

						if (commandList.getSelectedItem() == null || commandList.getSelectedItem() == "--Select--")
						{
							label4.setText("Please select one of the tools from the dropdown list or Enter the command Manually ");
						}
						else
						{

							String[] templist = list.get(commandList.getSelectedItem());
							setString(templist[2], templist[3]);
							addIntegrator(templist[0], templist[1], row);
						}
					}
				});

				Add.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						String cmd = commandField.getText();

						if (validateCommand(cmd))
						{
							addIntegrator(cmd.substring(0, cmd.indexOf(" ")), cmd, row);
							label4.setText("Command Added Successfully");
						}
						else
						{
							label4.setText("No Command Set");
						}
					}
				});

				Remove.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						int select = log.get(logTable.getSelectedRow()).slno;
						int selectedrow = logTable.getSelectedRow();
						if (selectedrow == -1 || log.size() == 0)
						{
							callbacks.issueAlert("Number is cannot");
						}
						else
						{
							log.remove(selectedrow);
							callbacks.issueAlert("Number is " + select);
							callbacks.issueAlert("Number is s" + selectedrow);
							try
							{
								tab.removeTabAt(selectedrow + 1);
								fimap[select].remove();
							}
							catch (Throwable e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							fimap[select] = new KaliIntegrator();

							fireTableRowsDeleted(selectedrow, selectedrow);
						}
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
							Add.setEnabled(true);
						}
						else
						{
							label4.setText("Invalid Configuration files");
						}
					}
				});

				lconfig.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						HashMap<String, String[]> processedlist = new HashMap<String, String[]>();

						processedlist = fileobj.processXML();
						if (processedlist != null)
						{
							list.putAll(processedlist);
							addCommandList(processedlist);

						}
						else
						{
							label4.setText("Invalid Configuration files");
						}
					}
				});

				callbacks.customizeUiComponent(panel);
				callbacks.customizeUiComponent(label1);
				callbacks.customizeUiComponent(label2);
				callbacks.customizeUiComponent(label3);
				callbacks.customizeUiComponent(label4);
				callbacks.customizeUiComponent(label5);
				callbacks.customizeUiComponent(label6);
				callbacks.customizeUiComponent(commandField);
				callbacks.customizeUiComponent(Add);
				callbacks.customizeUiComponent(logTable);
				callbacks.customizeUiComponent(tab);
				callbacks.customizeUiComponent(commandList);
				callbacks.customizeUiComponent(commandAdd);
				callbacks.customizeUiComponent(scrollPane);
				callbacks.addSuiteTab(BurpExtender.this);

			}
		});

	}

	public void addIntegrator(String name, String command, int row)
	{
		name = checkDuplicate(name, command);
		fimap[threads] = new KaliIntegrator(name, command, this.successStr, this.failureStr);
		fimap[threads].registerCallbacks(callbacks);

		comnd = new CommandEntry(threads, command);
		label4.setText("Success!!");
		log.add(comnd);
		tab.addTab(fimap[threads].toolName, fimap[threads].getUiComponent());
		fireTableRowsInserted(row, row);
		threads++;
	}

	public boolean validateCommand(String cmd)
	{
		if (cmd == null || cmd.length() <= 1)
		{

			label4.setText("No Command Set");
			cmd = "";
			return false;
		}

		else if (!(cmd.contains("POST_PARAMETER") || cmd.contains("GET_PARAMETER") || cmd.contains("COOKIE_PARAMETER"))
				|| threads >= 11)
		{
			cmd = "wrongformat";
			label4.setText("Command is not specified in requested format");
			return false;
		}
		else
		{
			return true;
		}
	}

	public void addCommandList(HashMap<String, String[]> templist)
	{
		for (String key : templist.keySet())
		{
			String[] temp = templist.get(key);
			this.commandList.addItem(temp[0]);
		}

	}

	public String checkDuplicate(String name, String cmd)
	{
		if (loghm.size() != 0 && loghm.containsKey(name))
		{

			Integer n = loghm.get(name);
			n = n + 1;
			loghm.put(name, n);
			name = name + "_" + Integer.toString(n);

		}
		else
		{
			loghm.put(name, 0);
		}

		return name;
	}

	public HashMap<String, String[]> processXML(ActionEvent evt)

	{
		HashMap<String, String[]> templist = new HashMap<String, String[]>();

		try
		{

			File selectedFile = null;
			selectedFile = this.getfile();
			if (selectedFile != null)
			{
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(selectedFile);

				// normalize text representation
				doc.getDocumentElement().normalize();
				System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());

				NodeList commandList = doc.getElementsByTagName("command");
				int length = commandList.getLength();

				System.out.println("Total no of commands : " + length);

				for (int s = 0; s < length; s++)
				{

					Node command = commandList.item(s);
					if (command.getNodeType() == Node.ELEMENT_NODE)
					{

						String[] temp = new String[5];
						Element commandElement = (Element) command;

						// -------
						NodeList namenode = (commandElement).getElementsByTagName("name");
						Element nameElement = (Element) namenode.item(0);

						NodeList nameList = nameElement.getChildNodes();
						System.out.println("name : " + ((Node) nameList.item(0)).getNodeValue().trim());
						temp[0] = ((Node) nameList.item(0)).getNodeValue().trim();

						// -------
						NodeList cmdnode = commandElement.getElementsByTagName("cmd");
						Element cmdElement = (Element) cmdnode.item(0);

						NodeList cmdList = cmdElement.getChildNodes();
						System.out.println("cmd: " + ((Node) cmdList.item(0)).getNodeValue().trim());
						temp[1] = ((Node) cmdList.item(0)).getNodeValue().trim();

						// ----
						NodeList successNode = commandElement.getElementsByTagName("success");
						Element successElement = (Element) successNode.item(0);

						NodeList successList = successElement.getChildNodes();
						System.out.println("Success: " + ((Node) successList.item(0)).getNodeValue().trim());
						temp[2] = ((Node) successList.item(0)).getNodeValue().trim();
						// ------

						NodeList failureNode = commandElement.getElementsByTagName("failure");
						Element failureElement = (Element) failureNode.item(0);
						NodeList failureList = failureElement.getChildNodes();
						System.out.println("Failure : " + ((Node) failureList.item(0)).getNodeValue().trim());
						temp[3] = ((Node) failureList.item(0)).getNodeValue().trim();

						templist.put(temp[0], temp);

					} // end of if clause
				}
			} // end of for loop with s var

		}
		catch (SAXParseException err)
		{
			System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());
			label4.setText("Parsing error occured");
			return null;

		}
		catch (SAXException e)
		{
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
			label4.setText("Parsing error occured");
			return null;

		}
		catch (Throwable t)
		{
			t.printStackTrace();
			label4.setText("Parsing error occured");
			return null;
		}

		System.out.println("size" + templist.size());
		return templist;

	}

	public File getfile()
	{
		JFileChooser fileChooser = new JFileChooser();
		File selectedFile = null;
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			selectedFile = fileChooser.getSelectedFile();
		}
		return selectedFile;
	}

	public void setString(String success, String failure)
	{
		this.successStr = success;
		this.failureStr = failure;
	}

	@Override
	public String getTabCaption()
	{
		// TODO Auto-generated method stub
		return "KaliIntegrator";
	}

	@Override
	public Component getUiComponent()
	{
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
			return rowIndex + 1;
		case 1:
			return commandEntry.command;

		default:
			return "";
		}

	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{

		CommandEntry commandEntry = (CommandEntry) aValue;

		log.set(rowIndex, commandEntry);
	}

	private static class CommandEntry
	{
		final int slno;
		final String command;

		CommandEntry(int sl, String command)
		{
			this.slno = sl;
			this.command = command;

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
			// row=convertRowIndexToView(row);

			int row1 = convertRowIndexToView(row);
			int col1 = convertColumnIndexToView(col);
			int row2 = convertRowIndexToModel(row);
			int col2 = convertColumnIndexToModel(col);
			CommandEntry commandEntry = log.get(row2);
			super.changeSelection(row, col, toggle, extend);

		}

	}

}
