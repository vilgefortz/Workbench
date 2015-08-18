package org.workbench.tool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.workbench.exceptions.MissingImageFileException;
import org.workbench.tool.attributes.AttribList;
import org.workbench.tool.attributes.Attribute;
import org.workbench.tool.attributes.SearchEvent;
import org.workbench.tool.attributes.SearchListener;
import org.workbench.tool.filechooser.ImageFileView;
import org.workbench.tool.filechooser.ImageFilter;
import org.workbench.tool.filechooser.ImagePreview;
import org.workbench.tool.panel.ImagePanel;
import org.workbench.tool.panel.ToolThumbnail;
import org.workbench.tool.res.Icons;
import org.workbench.window.ToolContainer;
import org.workbench.window.misc.Revolver;

public class Tool extends AttribList implements ActionListener, SearchListener {
	private static final String CANCEL = "Abbrechen";
	private static final String CHOOSE_PICTURE = "Choose picture";
	private static final String CLEAR = "Orginalverte";
	public static String dummyImageName = "data/res/dummy.png";
	private static final String FILE_CHOOSEN = "FILE_CHOOSEN";
	private static final Object FINISH_FORM = "Form not finished!";
	public static final int GAP = 10;
	public static final int GAPH = 16;
	private static String lastPath = "c:\\";
	private static final String NEEDED = "* = parameter optional";
	private static final String OK = "OK";
	private static final String PICTURE_SET = "PICTURE_SET";
	private static final String OPEN_FILE = "SAVE";
	private static final String UNIQUE_NAME = "Tool name must be unique";
	private static final String UNIQUE_TYPE = "Tool type must be unique";
	private static final String SELECT_WINDOW = "Choose window : ";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String STORE_ATTRIBUTE_FORM = "STORE";
	private static final WindowAdapter WINDOW_CLOSER = new WindowAdapter() {
		public void windowClosing(java.awt.event.WindowEvent e) {
			frameCount--;
		};
	};
	private static final String ADD_TO_REVOLVER = "Revolver";
	private JFileChooser fileChooser = new JFileChooser();
	private JFrame frame;
	public String imageName = "";
	private ImagePanel imagePanel;
	public String missingImageName = "none.png";
	private JTextField path;
	public ToolThumbnail thumbnail;
	private JComboBox<String> windowChoose;
	public String windowName = "";
	private static int frameCount = 0;
	private String SHOW_ALL_ATTRIBUTES = "show_all_attributes",
			show_all_attributes_default = "true";
	private JList<String> searchResult = new JList<>(
			new DefaultListModel<String>());
	private final String SEARCH_RESULT_TITLE = "Search results : ";
	private ArrayList<Tool> searchResultList = new ArrayList<Tool>();
	private MouseListener searchResultMouseListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				int index = searchResult.getSelectedIndex();
				if (index >= 0) {
					searchResultList.get(index).getToolEditWindow()
							.setVisible(true);
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

	};
	private String descriptionPattern = null;
	private boolean toolTypeDescription = false;
	private boolean defaultDescription = true;
	private boolean uniqueDescription;

	public Tool(String s) {

	}

	public void setupThumbnail() {
		thumbnail = new ToolThumbnail(this);
	}

	public Tool() {
		ToolSetter.setupTool(this);
		setupThumbnail();
		for (Attribute a : this) {
			a.addSearchListener(this);
		}
	}

	public Tool(Tool t) {
		for (Attribute b:t 	) {
			Attribute a = new Attribute();
			a.setChangeable(b.isChangeable());
			a.setNecessary(b.isNecessary());
			a.setSearchable(b.isSearchable());
			a.setType(b.getType());
			a.setName(b.getName());
			a.setPrefix(b.isPrefix());
			a.setPostfix(b.isPostfix());
			this.add(a);
			a.addSearchListener(this);
		}
		this.windowName=t.windowName;

		setupThumbnail();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(ADD_TO_REVOLVER)) {
			Revolver.add(this);
		}
		if (e.getActionCommand().equals(FILE_CHOOSEN)) {
			showOpenFileDialog();
		}
		if (e.getActionCommand().equals(CANCEL)) {
			if (this.getName().equals(ToolContainer.DUMMY)) frameCount--;
			frame.dispose();
		}
		if (e.getActionCommand().equals(CLEAR)) {
			for (Attribute a : this) {
				a.restoreTextFields();
			}
		}
		if (e.getActionCommand().equals(PICTURE_SET)) {
			imageName = lastPath;
			try {
				imagePanel.setImage(imageName);
				path.setText(lastPath);
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			imagePanel.revalidate();
		}

		if (e.getActionCommand().equals(OPEN_FILE))
			showOpenFileDialog();
		if (e.getActionCommand().equals(STORE_ATTRIBUTE_FORM)) {
			boolean checkOk = true;

			for (Attribute a : this) {
				checkOk &= a.checkForm(e);
			}
			if (!checkOk)
				JOptionPane.showMessageDialog(frame, FINISH_FORM);
			else if (ToolContainer.main.hasName(this.getNameFromForm(), this))
				JOptionPane.showMessageDialog(frame, UNIQUE_NAME);
			else if (this.getName().equals(ToolContainer.DUMMY) && ToolContainer.main.hasType(this.getTypeFromForm(), this))
				JOptionPane.showMessageDialog(frame, UNIQUE_TYPE);
			else
				saveFromAttributeForm(e);
		}
	}

	private String getTypeFromForm() {
		return ToolSetter.getTypeFromForm(this);
	}

	private String getNameFromForm() {
		return ToolSetter.getNameFromForm(this);
	}

	public JPanel getMiniPanel() throws MissingImageFileException {
		JPanel p = new ToolThumbnail(this);
		return p;
	}

	public JFrame getToolEditWindow() {
		if (this.getName().equals(ToolContainer.DUMMY))
			return this.getNewToolCreationWindow();
		/*
		 * if (frameCount > 0) return null; frameCount++;
		 */

		frame = new JFrame();
		imagePanel = new ImagePanel();
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setTitle(this.getName());
		JPanel west = this.getToolValueEditorPanel();
		frame.add(west, BorderLayout.WEST);

		// east panel
		JPanel eastMain = new JPanel();
		eastMain.setLayout(new GridLayout(2, 0));
		eastMain.setPreferredSize(west.getPreferredSize());
		JPanel east = new JPanel();
		eastMain.add(east);
		// east.setMinimumSize(new Dimension(360, 2));
		east.setPreferredSize(new Dimension(360,
				eastMain.getPreferredSize().height / 2));
		east.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(BevelBorder.RAISED),
				CHOOSE_PICTURE));
		lastPath = this.imageName;
		// System.out.println(this.imageName);
		path = new JTextField(lastPath);
		path.setPreferredSize(new Dimension(240, 20));
		frame.add(eastMain, BorderLayout.EAST);
		GridBagLayout gb = new GridBagLayout();
		east.setLayout(gb);
		GridBagConstraints cs = new GridBagConstraints();
		JButton browse = new JButton(Icons.getIcon(Icons.BROWSE));
		browse.setActionCommand(FILE_CHOOSEN);
		browse.addActionListener(this);
		cs.insets = new Insets(20, 5, 5, 5);
		cs.gridy = 0;
		cs.weighty = 0;
		gb.setConstraints(path, cs);
		gb.setConstraints(browse, cs);
		cs = new GridBagConstraints();
		cs.insets = new Insets(20, 5, 5, 5);
		cs.gridy = 1;
		cs.weighty = 2;
		cs.gridwidth = 3;
		cs.anchor = GridBagConstraints.CENTER;
		cs.fill = GridBagConstraints.BOTH;
		gb.setConstraints(imagePanel, cs);
		// imagePanel.setPreferredSize(new Dimension (240,(int)
		// (west.getPreferredSize().getHeight()-60)));
		try {
			imagePanel.setImage(lastPath);
		} catch (IOException e) {

		}
		east.add(path);
		east.add(browse);
		east.add(imagePanel);
		searchResult = new JList<String>(new DefaultListModel<String>());
		searchResult.addMouseListener(searchResultMouseListener);
		searchResult.setBorder(BorderFactory.createLoweredBevelBorder());
		this.performSearch(null);
		JScrollPane scrollPane = new JScrollPane(searchResult);

		scrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createRaisedBevelBorder(),
				this.SEARCH_RESULT_TITLE));
		eastMain.add(scrollPane);
		// eastSouth.setMinimumSize(new Dimension (360,
		// eastMain.getPreferredSize().height/2));
		// south panel
		JLabel neededLabel = new JLabel(NEEDED);
		JButton ok = new JButton(OK);
		ok.setPreferredSize(new Dimension(110, 20));
		JButton cancel = new JButton(CANCEL);
		cancel.setPreferredSize(new Dimension(110, 20));
		JButton clear = new JButton(CLEAR);
		clear.setPreferredSize(new Dimension(110, 20));
		JPanel south = new JPanel();
		JPanel glue = new JPanel();
		gb = new GridBagLayout();
		south.setLayout(gb);
		class ConstraintsGenerator {
			public GridBagConstraints getConstraints(int i) {
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = i;
				c.weightx = 0;
				c.insets = new Insets(5, 5, 5, 5);
				if (i == 1)
					c.weightx = 2;
				return c;
			}
		}
		ConstraintsGenerator generator = new ConstraintsGenerator();
		ok.setActionCommand(STORE_ATTRIBUTE_FORM);
		cancel.setActionCommand(CANCEL);
		clear.setActionCommand(CLEAR);
		JButton addToRevolver = new JButton(ADD_TO_REVOLVER);
		addToRevolver.setActionCommand(ADD_TO_REVOLVER);
		addToRevolver.addActionListener(this);
		addToRevolver.setPreferredSize(new Dimension(110, 20));
		clear.setPreferredSize(new Dimension(110, 20));
		gb.setConstraints(addToRevolver,generator.getConstraints(3));
		ok.addActionListener(this);

		cancel.addActionListener(this);
		clear.addActionListener(this);

		gb.setConstraints(ok, generator.getConstraints(5));
		gb.setConstraints(cancel, generator.getConstraints(4));
		gb.setConstraints(clear, generator.getConstraints(3));
		gb.setConstraints(addToRevolver, generator.getConstraints(2));
		gb.setConstraints(neededLabel, generator.getConstraints(0));
		gb.setConstraints(glue, generator.getConstraints(1));

		south.add(neededLabel);
		south.add(addToRevolver);
		south.add(clear);
		south.add(cancel);
		south.add(ok);
		south.add(glue);

		frame.add(south, BorderLayout.SOUTH);

		// north panel
		JPanel north = new JPanel();
		windowChoose = new JComboBox<String>();
		windowChoose.setEditable(true);
		String[] list = ToolContainer.getListOfWindows();
		for (String s : list)
			windowChoose.addItem(s);
		JLabel selectWindow = new JLabel(SELECT_WINDOW);
		north.add(selectWindow);
		north.add(windowChoose);
		frame.add(north, BorderLayout.NORTH);
		// south panel

		// finally

		// frame.addWindowListener(WINDOW_CLOSER);
		frame.pack();
		frame.setLocationRelativeTo(ToolContainer.frame);
		frame.setResizable(false);
		return frame;
	}

	public JFrame getNewToolCreationWindow() {

		if (frameCount > 0)
			return null;
		frameCount++;
		frame = new JFrame();
		imagePanel = new ImagePanel();
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setTitle(this.getName());
		frame.add(this.getToolEditorPanel(), BorderLayout.WEST);

		// east panel

		JPanel east = new JPanel();
		east.setMinimumSize(new Dimension(360, 2));
		east.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(BevelBorder.RAISED),
				CHOOSE_PICTURE));
		path = new JTextField(lastPath);
		path.setPreferredSize(new Dimension(240, 20));
		frame.add(east, BorderLayout.EAST);
		GridBagLayout gb = new GridBagLayout();
		east.setLayout(gb);
		GridBagConstraints cs = new GridBagConstraints();
		JButton browse = new JButton(Icons.getIcon(Icons.BROWSE));
		browse.setActionCommand(FILE_CHOOSEN);
		cs.insets = new Insets(20, 5, 5, 5);
		cs.gridy = 0;
		cs.weighty = 0;
		gb.setConstraints(path, cs);
		gb.setConstraints(browse, cs);
		cs = new GridBagConstraints();
		cs.insets = new Insets(20, 5, 5, 5);
		cs.gridy = 1;
		cs.weighty = 2;
		cs.gridwidth = 3;
		cs.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(imagePanel, cs);
		imagePanel.setPreferredSize(new Dimension(340, 300));
		try {
			imagePanel.setImage(lastPath);
		} catch (IOException e) {

		}
		east.add(path);
		east.add(browse);
		east.add(imagePanel);
		// north panel
		JPanel north = new JPanel();
		windowChoose = new JComboBox<String>();
		windowChoose.setEditable(true);
		String[] list = ToolContainer.getListOfWindows();
		for (String s : list)
			windowChoose.addItem(s);
		JLabel selectWindow = new JLabel(SELECT_WINDOW);
		north.add(selectWindow);
		north.add(windowChoose);
		frame.add(north, BorderLayout.NORTH);
		// south panel
		JLabel neededLabel = new JLabel(NEEDED);
		JButton ok = new JButton(OK);
		ok.setPreferredSize(new Dimension(110, 20));
		JButton cancel = new JButton(CANCEL);
		cancel.setPreferredSize(new Dimension(110, 20));
		JButton clear = new JButton(CLEAR);
		clear.setPreferredSize(new Dimension(110, 20));
		JPanel south = new JPanel();
		JPanel glue = new JPanel();
		gb = new GridBagLayout();
		south.setLayout(gb);
		class ConstraintsGenerator {
			public GridBagConstraints getConstraints(int i) {
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = i;
				c.weightx = 0;
				c.insets = new Insets(5, 5, 5, 5);
				if (i == 1)
					c.weightx = 2;
				return c;
			}
		}
		ConstraintsGenerator generator = new ConstraintsGenerator();
		ok.setActionCommand(STORE_ATTRIBUTE_FORM);
		browse.setActionCommand(OPEN_FILE);
		cancel.setActionCommand(CANCEL);
		clear.setActionCommand(CLEAR);

		ok.addActionListener(this);
		browse.addActionListener(this);
		cancel.addActionListener(this);
		clear.addActionListener(this);

		gb.setConstraints(ok, generator.getConstraints(4));
		gb.setConstraints(cancel, generator.getConstraints(3));
		gb.setConstraints(clear, generator.getConstraints(2));
		gb.setConstraints(neededLabel, generator.getConstraints(0));
		gb.setConstraints(glue, generator.getConstraints(1));
		south.add(neededLabel);
		south.add(clear);
		south.add(cancel);
		south.add(ok);
		south.add(glue);

		frame.add(south, BorderLayout.SOUTH);
		frame.addWindowListener(WINDOW_CLOSER);
		frame.pack();
		frame.setLocationRelativeTo(ToolContainer.frame);
		frame.setResizable(false);
		return frame;
	}

	public ToolThumbnail getThumbnail() {
		return thumbnail;
	}

	JPanel getToolEditorPanel() {
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(BevelBorder.RAISED),
				"Setup new tool"));
		p.setLayout(new GridLayout(size(), 1));

		for (Attribute a : this) {
			p.add(a.getAttributeEditPanel());
		}
		return p;
	}

	JPanel getToolValueEditorPanel() {
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(BevelBorder.RAISED),
				"Setup new tool"));
		p.setLayout(new GridLayout(size(), 1));
		String val = MainSettings.prop.getProperty(SHOW_ALL_ATTRIBUTES);
		if (val == null) {
			MainSettings.prop.setProperty(SHOW_ALL_ATTRIBUTES,
					show_all_attributes_default);
			val = show_all_attributes_default;
		}

		Boolean showAll = Boolean.parseBoolean(val);

		for (Attribute a : this) {
			if (showAll) {
				MainSettings.prop.setProperty(SHOW_ALL_ATTRIBUTES,
						show_all_attributes_default);
				p.add(a.getAttributeValueEditPanel());
			} else if (!a.getName().equals("") || !a.getValue().equals(""))
				p.add(a.getAttributeValueEditPanel());
		}
		return p;
	}

	public String getWindowName() {
		return windowName;
	}

	public boolean isDummy() {
		if (this.getName().equals(ToolContainer.DUMMY))
			return true;
		return false;
	}

	public void save(Element tools, Document doc) {
		Element tool = doc.createElement("tool");
		tools.appendChild(tool);
		tool.setAttribute("window", windowName);
		tool.setAttribute("image", imageName);
		tool.setAttribute("pattern", descriptionPattern);
		for (Attribute a : this)
			a.save(tool, doc);
	}

	private void saveFromAttributeForm(ActionEvent e) {
		for (Attribute a : this) {
			a.saveFromForm(e);
		}
		if (windowChoose != null) {
			String wn = (String) windowChoose.getSelectedItem();
			this.windowName = wn;
		}
		lastPath = path.getText();
		imageName = lastPath;
		if (imageName.equals(dummyImageName))
			imageName = "";
		thumbnail.refreshImage();
		frame.dispose();
		frameCount--;

		ToolContainer.refreshTabbedPane();
	}

	@Override
	public boolean equals(Object o) {
		Tool t = (Tool) o;
		return t.getName().equals(this.getName());
	}

	public void setImageName(String dummyImageName2) {
		imageName = dummyImageName2;
		if (thumbnail != null)
			thumbnail.refreshImage();
	}

	public void setWindowName(String name) {
		this.windowName = name;
	}

	void showOpenFileDialog() {
		fileChooser = new JFileChooser();
		ImageFilter filter = new ImageFilter();
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		// Add custom icons for file types.
		fileChooser.setFileView(new ImageFileView());
		// Add the preview pane.
		fileChooser.setAccessory(new ImagePreview(fileChooser));
		int ans = fileChooser.showOpenDialog(frame);
		if (ans == JFileChooser.APPROVE_OPTION) {
			lastPath = fileChooser.getSelectedFile().getAbsolutePath();
			this.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, PICTURE_SET));
		}
	}
	public boolean isDefaultDescription () {
		return defaultDescription;
	};
	public boolean isToolTypeDescription () {
		return toolTypeDescription;
	}
	public boolean isUniqueDescription () {
		return uniqueDescription;
	}
	public String getName() {
		return ToolSetter.getName(this);
	}

	public void setName(String dummy) {
		ToolSetter.setName(this, dummy);
	}

	public String getType() {
		return ToolSetter.getType(this);
	}

	public String getCanonicalImageName() {
		return this.getType() + "_" + this.getName() + "."
				+ MainSettings.EXTENSION;
	}

	@Override
	public void performSearch(SearchEvent e) {
		ArrayList<Tool> result = new ArrayList<Tool>();
		ToolContainer tc = ToolContainer.main;
		for (Tool t : tc) {
			if (t == this)
				continue;
			AttribList al = t.getSearchableAttributeList();
			AttribList thisAl = this.getSearchableAttributeList();
			for (int i = 0; i < al.size(); i++) {
				System.out.println(i);
				if (al.get(i).getValue()
						.equals(thisAl.get(i).getTextFieldValue())) {
					result.add(t);
					break;
				}
			}
		}
		DefaultListModel<String> model = (DefaultListModel<String>) searchResult
				.getModel();
		model.removeAllElements();
		result.forEach((Tool t) -> {
			addToSearchList(t);
		});
		System.out.println(result.size());
		searchResultList = result;
	}

	private void addToSearchList(Tool t) {
		String result = "";
		for (Attribute a : t) {
			if (!a.getValue().equals(""))
				result = result + a.getType() + ":" + a.getValue() + ", ";
		}
		result = result.substring(0, result.length() - 2);
		((DefaultListModel<String>) searchResult.getModel()).addElement(result);
	}

	private AttribList getSearchableAttributeList() {
		AttribList al = new AttribList();
		for (Attribute a : this) {
			if (a.isSearchable())
				al.add(a);
		}
		return al;
	}

	public String getDefaultToolDescription() {
		if (this.descriptionPattern == null
				|| this.descriptionPattern.equals("")) {
			return ToolSetter.createDefaultDescription(this);

		} else {
			return ToolSetter.createDescription(this, this.descriptionPattern);
		}
	}

	public void setType(String type) {
		ToolSetter.setType(this, type);
	}

	public String getPlatte() {
		// TODO Auto-generated method stub
		return ToolSetter.getPlatte(this);
	}

	public String getPattern() {
		return this.descriptionPattern;
		
	}
	public void setPattern (String p) {
		this.descriptionPattern=p;
	}
	
	public void setPatternForType (String p ) {
		for (Tool t : ToolContainer.main){
			if (t.getType().equals(this.getType())) t.setPattern(p);
		}
	}
}
