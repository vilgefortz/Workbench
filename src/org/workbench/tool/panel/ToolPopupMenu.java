package org.workbench.tool.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.workbench.tool.Tool;
import org.workbench.window.ToolContainer;
import org.workbench.window.misc.Strings;

public class ToolPopupMenu extends JPopupMenu implements ActionListener {
	Tool t;
	public ToolPopupMenu(Tool t) {
		this.t=t;
		JMenuItem createSameType = new JMenuItem (Strings.POPUP_CREATE_SAME_TYPE);
		createSameType.setActionCommand(Strings.POPUP_CREATE_SAME_TYPE);
		createSameType.addActionListener(this);
		this.add(createSameType);
		
		JMenuItem deleteTool = new JMenuItem (Strings.DELETE_TOOL);
		deleteTool.setActionCommand(Strings.DELETE_TOOL);
		deleteTool.addActionListener(this);
		this.add(deleteTool);
		
		JMenuItem changePattern = new JMenuItem (Strings.CHANGE_PATTERN);
		changePattern.setActionCommand(Strings.CHANGE_PATTERN);
		changePattern.addActionListener(this);
		this.add(changePattern);
		
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(Strings.POPUP_CREATE_SAME_TYPE)) {
			createSameType ();
		}
		if (command.equals(Strings.DELETE_TOOL)) {
			deleteThisTool();
		}
		if (command.equals(Strings.CHANGE_PATTERN)) {
			changePattern(t);
		}
	}
	private void createSameType() {
		Tool t = new Tool(this.t);
		t.setName("new tool");
		t.setType(this.t.getType());
		ToolContainer.main.add(t);
		ToolContainer.refreshTabbedPane();
		t.getThumbnail().refreshImage();
		t.getToolEditWindow().setVisible(true);
	}
	
	private void deleteThisTool () {
		ToolContainer.main.remove(t);
		ToolContainer.refreshTabbedPane();
	}
	
	private void changePattern (Tool t) {

		String result = (String)JOptionPane.showInputDialog(ToolContainer.frame, Strings.NEW_PATTERN_MESSAGE, Strings.NEW_PATTERN_TITLE, JOptionPane.PLAIN_MESSAGE,
				null, null, t.getPattern());
		if (result == null) return;
		t.setPatternForType(result);
	}
	
}
