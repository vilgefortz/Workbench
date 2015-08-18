package org.workbench.window.misc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.workbench.tool.Tool;

public class Revolver {
	/**
	 * 
	 */
	public static ArrayList<Tool> tools = new ArrayList<Tool>();
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final WindowListener WND_LISTENER = new WindowAdapter() {
		public void windowClosing(java.awt.event.WindowEvent e) {
			windowExist = false;
		};
	};

	static ArrayList<String> revolverList = new ArrayList<String>();
	private static boolean windowExist = false;

	static DefaultListModel<String> listModel = new DefaultListModel<String>();
	static JList<String> list = new JList<String>(listModel);
	static JScrollPane sp = new JScrollPane(list);
	private static MouseListener mouseListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (list != null) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					showValueEditDialog(list.getSelectedIndex());
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	};
	private static JFrame frame;
	private static ActionListener al = new ActionListener () {

		@Override
		public void actionPerformed(ActionEvent e) {
			String c= e.getActionCommand();
			if (c.equals(Strings.COPY_BUTTON)) {
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Clipboard clipboard = toolkit.getSystemClipboard();
				StringSelection strSel = new StringSelection(createStringFromRevolver ());
				clipboard.setContents(strSel, null);
			}
			
			if (c.equals(Strings.CLEAR_BUTTON)) {
				listModel.clear();
				revolverList.clear();
			}
			
		}
	};
		
	private static String createStringFromRevolver() {
		StringBuilder sb= new StringBuilder ();
		int size = listModel.size();
		for (int i=0; i<size; i++) {
			sb.append(listModel.get(i));
			sb.append(i==size-1?"":"\n");
		}
		return sb.toString();
	};

	
	public static void showRevolverWindow() {
		if (windowExist)
			return;
		windowExist = true;

		revolverList = new ArrayList<String>();
		frame = new JFrame();
		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		list.setFont(new Font("Courier",Font.PLAIN,12));
		list.addMouseListener(mouseListener);
		list.setBorder(BorderFactory.createLoweredBevelBorder());
		sp = new JScrollPane(list);

		
		class ConstraintsGenerator {
			public GridBagConstraints getConstraints(int i) {
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = i;
				c.gridy = 0;
				c.weightx = 0;
				c.insets = new Insets(5, 5, 5, 5);
				if (i == 0)
					c.weightx = 2;
				return c;
			}
		}
		ConstraintsGenerator csgen = new ConstraintsGenerator();
		JButton copy = new JButton (Strings.COPY_BUTTON);
		JButton clear = new JButton (Strings.CLEAR_BUTTON);
		copy.setPreferredSize (new Dimension (110,20));
		clear.setPreferredSize (new Dimension (110,20));
		copy.setActionCommand(Strings.COPY_BUTTON);
		copy.addActionListener(al);
		clear.addActionListener(al);
		clear.setActionCommand(Strings.CLEAR_BUTTON);
		GridBagLayout gbl = new GridBagLayout ();
		JPanel south = new JPanel ();
		JPanel plug = new JPanel ();
		
		gbl.setConstraints(copy,csgen.getConstraints(2));
		gbl.setConstraints(clear,csgen.getConstraints(1));
		gbl.setConstraints(plug,csgen.getConstraints(0));
		south.setLayout (gbl);
		south.add(copy);
		south.add(clear);
		south.add(plug);

		
		// remove remove all copy ok

		frame.setLayout(new BorderLayout());
		frame.add(sp, BorderLayout.CENTER);
		frame.add(south,BorderLayout.SOUTH);
		frame.setSize(new Dimension(WIDTH, HEIGHT));
		frame.setVisible(true);
		frame.addWindowListener(WND_LISTENER);
	}

	protected static void showValueEditDialog(int index) {

		String result = (String)JOptionPane.showInputDialog(frame, Strings.INPUT_MESSAGE, Strings.ENTRY_TITLE, JOptionPane.PLAIN_MESSAGE,
				null, null, list.getModel().getElementAt(index));
		if (result == null) return;
		listModel.set(index, result);
	}

	
	public static void add(Tool element) {

		if (!windowExist)
			showRevolverWindow();
		tools.add(element);
		listModel.addElement(element.getDefaultToolDescription());
	}
}

	