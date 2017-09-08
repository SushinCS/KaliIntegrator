/**
 * The File Operation class performs xml file processing to fetch KaliIntegrator
 * configuration data from configuration files in XML format.
 *
 * @author Sushin
 * @version 1.0
 * @since 2017-10-20
 */

package burp;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FileOperation
{

	/**
	 * Method used for processing the contents of XML configuration file
	 * 
	 * @return HashMap<String,String[]> This returns hashmap variable containing
	 *         values fetched from the XML configuration files.
	 */

	public HashMap<String, String[]> processXML()

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
				doc.getDocumentElement().normalize();
				NodeList commandList = doc.getElementsByTagName("command");
				int length = commandList.getLength();
				for (int s = 0; s < length; s++)
				{
					Node command = commandList.item(s);
					if (command.getNodeType() == Node.ELEMENT_NODE)
					{

						String[] temp = new String[5];
						Element commandElement = (Element) command;

						NodeList namenode = (commandElement).getElementsByTagName("name");
						Element nameElement = (Element) namenode.item(0);
						NodeList nameList = nameElement.getChildNodes();
						temp[0] = ((Node) nameList.item(0)).getNodeValue().trim();

						NodeList cmdnode = commandElement.getElementsByTagName("cmd");
						Element cmdElement = (Element) cmdnode.item(0);
						NodeList cmdList = cmdElement.getChildNodes();
						temp[1] = ((Node) cmdList.item(0)).getNodeValue().trim();

						NodeList successNode = commandElement.getElementsByTagName("success");
						Element successElement = (Element) successNode.item(0);
						NodeList successList = successElement.getChildNodes();
						temp[2] = ((Node) successList.item(0)).getNodeValue().trim();

						NodeList failureNode = commandElement.getElementsByTagName("failure");
						Element failureElement = (Element) failureNode.item(0);
						NodeList failureList = failureElement.getChildNodes();
						temp[3] = ((Node) failureList.item(0)).getNodeValue().trim();

						templist.put(temp[0], temp);

					}
				}
			}

		}
		catch (SAXParseException err)
		{
			System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

			return null;

		}
		catch (SAXException e)
		{
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

			return null;

		}
		catch (Throwable t)
		{
			t.printStackTrace();
			return null;
		}
		return templist;
	}

	/**
	 * Method used for selecting the XML configuration files
	 * 
	 * @return File This returns the selected configuration file object.
	 */
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

}
