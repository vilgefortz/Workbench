package org.workbench.window;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.workbench.exceptions.MissingImageFileException;
import org.workbench.tool.MainSettings;
import org.workbench.tool.Tool;
import org.workbench.window.misc.Grid;

public class ToolContainer extends ArrayList<Tool> implements ActionListener {
	private static ComponentListener componentListener = new ComponentListener() {

		@Override
		public void componentHidden(ComponentEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void componentMoved(ComponentEvent e) {
			// TODO Auto-generated method stub

		}

		// @Override
		public void componentResized(ComponentEvent e) {
			JScrollPane sp = (JScrollPane) tabbedPane.getSelectedComponent();
			ThumbnailsPane tp = ((ThumbnailsPane) (sp.getViewport().getView()));
			tp.resized();
			((JScrollPane) tabbedPane.getSelectedComponent())
					.setViewportView(tp);

			tabbedPane.revalidate();

		}

		@Override
		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub
		}
	};
	public static ToolContainerList containerList = new ToolContainerList();
	protected static String dataPath = "data/";
	static Tool dummy;
	public static final String DUMMY = "DUMMY";
	protected static String filename = dataPath + "data.xml";

	public static JFrame frame;
	public static ToolContainer main = new ToolContainer();

	private static final String MAIN_WINDOW_NAME = "AUSSEN.V";

	private static final long serialVersionUID = 8190107488171880814L;
	private static final String imagePath = "images/";

	public static JTabbedPane tabbedPane;
	private static ThumbnailsPaneList thumbnailsPaneList = new ThumbnailsPaneList();

	private static Tool createDummy() {
		Tool dummy = new Tool();
		dummy.setName(DUMMY);
		dummy.setWindowName(MAIN_WINDOW_NAME);
		dummy.setImageName(Tool.dummyImageName);

		return dummy;
	}

	public static JTabbedPane createTabbedPane()
			throws MissingImageFileException {
		tabbedPane = new JTabbedPane();
//		main.setName(MAIN_WINDOW_NAME);
		tabbedPane.addComponentListener(componentListener);
		refreshTabbedPane();
		return tabbedPane;
	}

	public static String[] getListOfWindows() {
		ArrayList<String> l = new ArrayList<String>();
		for (Tool t : main) {
			if (!l.contains(t.getWindowName()))
				l.add(t.getWindowName());
		}

		return l.toArray(new String[l.size()]);
	}

	public static void main(String[] args) {
		MainSettings.loadSettings();
		ToolContainer.load(filename);
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				main.save(filename);
				MainSettings.finish();
				super.windowClosing(e);
			}
		});
		try {
			frame.add(createTabbedPane());
		} catch (MissingImageFileException e) {

		}
		frame.setSize(new Dimension(800, 600));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static ToolContainer newInstance() {
		ToolContainer tc = new ToolContainer();
		return tc;
	}

	public static void refreshTabbedPane() {
		int index = tabbedPane.getSelectedIndex();
		tabbedPane.removeAll();
		boolean isDummy = false;
		for (Tool t : main)
			if (t.getName().equals(DUMMY)) {
				isDummy = true;
				break;
			}
		if (!isDummy) {
			dummy = createDummy();
			main.add(dummy);
		}
		String[] tcList = getListOfWindows();
		containerList.clear();
		thumbnailsPaneList.clear();
		/*System.out.println("REFRESHING TABBED PANE\nMAIN LIST:");*/
/*		for (Tool t : main)
			System.out.println(t.getName());*/
		//System.out.println("end");
		for (int i = 0; i < tcList.length; i++) {
			containerList.add(main.getChildToolContainer(tcList[i]));
			ThumbnailsPane tp = containerList.get(i).getPanelRefreshed();
			thumbnailsPaneList.add(tp);
			tabbedPane.add(new JScrollPane(tp) {
				private static final long serialVersionUID = -2219692352801374904L;

				@Override
				public String getName() {
					// TODO Auto-generated method stub
					return tp.getName();
				}
			});
		}
		tabbedPane.setSelectedIndex(index > 0 ? index : 0);

	}

	public Grid grid = new Grid(5, 5);
	String name;
	ThumbnailsPane panel = new ThumbnailsPane(this);

	int width, height;

	private ToolContainer() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	public ToolContainer getChildToolContainer(String containerName) {
		ToolContainer newContainer = new ToolContainer();
		newContainer.setName(containerName);
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getWindowName().equals(containerName))
				newContainer.add(this.get(i));
		}
		return newContainer;
	}

	public String getName() {
		return name;
	}

	public ThumbnailsPane getPanelRefreshed() {
		panel.removeAll();
		((ThumbnailsPane) panel).createThumbnails();
		panel.setName(this.getName());
		panel.revalidate();
		return panel;
	}

	public static void load(String fileName) {
		try {
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(filename));

			doc.getDocumentElement().normalize();

			Element tool = (Element) (doc.getElementsByTagName("tools").item(0));

			NodeList toolsNodeList = tool.getElementsByTagName("tool");
			for (int i = 0; i < toolsNodeList.getLength(); i++) {
				Tool t = new Tool("");
				Element toolElement = (Element) toolsNodeList.item(i);
				t.setWindowName(toolElement.getAttribute("window"));
				t.setImageName(toolElement.getAttribute("image"));
				t.setPattern(toolElement.getAttribute("pattern"));
				NodeList attributeNodeList = toolElement
						.getElementsByTagName("attribute");
				for (int j = 0; j < attributeNodeList.getLength(); j++) {
					Element att = (Element) attributeNodeList.item(j);
					org.workbench.tool.attributes.Attribute a = org.workbench.tool.attributes.Attribute
							.load(att, doc);
					a.addSearchListener(t);
					t.add(a);
				}
				t.setupThumbnail();
				main.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save(String fileName) {
		try {
			// saving images
			//FileUtils.
			FileUtils.cleanDirectory(new File("data/images")); 
			for (Tool t : this)
				if (t.thumbnail.getImage() != null) {

					File outputfile = new File(dataPath + imagePath
							+ t.getCanonicalImageName());
					ImageIO.write(t.thumbnail.getImage(), MainSettings.EXTENSION, outputfile);
					t.setImageName(outputfile.getAbsolutePath());
				}

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			System.out.println(docBuilder.newDocument());
			Document doc = docBuilder.newDocument();

			Element tools = doc.createElement("tools");
			doc.appendChild(tools);
			for (Tool t : this) {
				t.save(tools, doc);
			}

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(doc);
			File file = new File(fileName);
			StreamResult result = new StreamResult(file);

			// Output to console for testing
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void switchDummyWith(Tool e) {

		System.out.println("adding tool " + e.getName() + " to " + this.name);
		thumbnailsPaneList.get(this.name).createThumbnails();
		ToolContainer.refreshTabbedPane();
		// debug :
		for (Tool t : this) {
			System.out.println("SWITCHING DUMMY : " + this.indexOf(t) + " "
					+ t.getName());
		}
	}

	public boolean hasType(String s,Tool tool) {
		for (Tool t : this)
		{ 
			if (t==tool) continue;
			if (s.equals(t.getType()))
				return true;
		}
		return false;
	}

	public boolean hasName(String s, Tool tool) {
		for (Tool t : this) {
			if (t==tool) continue;
			if (s.equals(t.getName()))
				return true;
		}
		return false;
	}

}
