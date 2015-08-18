package org.workbench.tool.attributes;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.workbench.tool.res.Misc;

public class Attribute {

	public final DocumentListener docListener = new DocumentListener() {

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			if (searchable && valueEditOpen) {
				notifySearchListeners(type, valueField.getText());
				
			}

		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			if (searchable && valueEditOpen) {
				notifySearchListeners(type, valueField.getText());
				
			}
		}

	};

	class JTextFieldLimit extends PlainDocument {
		private static final long serialVersionUID = -4950334501725532178L;
		private int limit;

		JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
			this.addDocumentListener(docListener);
		}

		JTextFieldLimit(int limit, boolean upper) {
			super();
			this.limit = limit;
			this.addDocumentListener(docListener);
		}

		public void insertString(int offset, String str, AttributeSet attr)
				throws BadLocationException {
		
			if (str == null)
				return;

			if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
			if (searchable && valueEditOpen) {
				notifySearchListeners(type, valueField.getText());
			}
		}
	}

	static final Dimension CHECK_BOX_SIZE = new Dimension(20, 20);
	static final Dimension DISTANCE_SIZE = new Dimension(40, 20);
	static final int HGAP = 6;
	static final Dimension STAR_SIZE = new Dimension(10, 20);
	static final Dimension TEXT_EDIT_SIZE = new Dimension(100, 20);
	static final Dimension TYPE_LABEL_SIZE = new Dimension(100, 20);
	static final int VGAP = 3;
	static final int WIDTH = 340;
	boolean attributeEditOpen = false;
	boolean changeable = true;
	boolean necessary = false;
	protected boolean searchable = true;

	private String name = "";
	private JPanel p;

	boolean postfix = false;
	JCheckBox postfix_box = new JCheckBox();
	boolean prefix = false;
	JCheckBox prefix_box = new JCheckBox();

	private ArrayList<SearchListener> sl = new ArrayList<SearchListener>();
	private JLabel star = new JLabel();
	String type = "";
	JTextField typeField = new JTextField();
	String value = "";
	boolean valueEditOpen = false;
	JTextField valueField = new JTextField();

	public JTextField getValueField() {
		return valueField;
	}

	public void setValueField(JTextField valueField) {
		this.valueField = valueField;
	}

	public void addSearchListener(SearchListener l) {
		if (l != null)
			sl.add(l);
	}

	public boolean checkForm(ActionEvent e) {
		if (valueEditOpen) {
			if (necessary) {
				if (valueField.getText().equals(""))
					return false;
			}
		}
		if (attributeEditOpen) {
			if (!changeable) {
				if (necessary) {
					if (valueField.getText().equals(""))
						return false;
				}
			} else if (prefix_box.isSelected() || postfix_box.isSelected()) {
				if (typeField.getText().equals(""))
					return false;
				if (valueField.getText().equals(""))
					return false;
			}
		}
		return true;
	}

	public JPanel getAttributeEditPanel() {
		attributeEditOpen = true;
		p = new JPanel();

		valueField = new JTextField(value);
		star = new JLabel((prefix || postfix || necessary) ? " " : "*");
		JLabel type = new JLabel(this.name);
		GridBagLayout l = new GridBagLayout();
		p.setLayout(l);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(VGAP, HGAP, VGAP, HGAP);
		c.gridx = GridBagConstraints.RELATIVE;

		l.setConstraints(star, c);
		l.setConstraints(valueField, c);
		l.setConstraints(type, c);

		star.setPreferredSize(STAR_SIZE);

		valueField.setPreferredSize(TEXT_EDIT_SIZE);
		valueField.setHorizontalAlignment(JTextField.RIGHT);
		valueField.setDocument(new JTextFieldLimit(40));
		type.setPreferredSize(TYPE_LABEL_SIZE);

		p.add(star);
		p.add(valueField);
		if (!changeable)
			p.add(type);

		if (changeable) {
			JPanel plug2 = new JPanel();
			plug2.setPreferredSize(DISTANCE_SIZE);
			l.setConstraints(plug2, c);
			p.add(plug2);
			typeField = new JTextField();
			typeField.setHorizontalAlignment(JTextField.RIGHT);
			l.setConstraints(typeField, c);
			typeField.setPreferredSize(TEXT_EDIT_SIZE);
			prefix_box = new JCheckBox();
			prefix_box.setPreferredSize(CHECK_BOX_SIZE);
			prefix_box.addActionListener((ActionEvent e) -> {

				if (prefix_box.isSelected())
					postfix_box.setSelected(false);
				updateStar();
			});
			postfix_box = new JCheckBox();
			postfix_box.setPreferredSize(CHECK_BOX_SIZE);
			postfix_box.addActionListener((ActionEvent e) -> {
				if (postfix_box.isSelected())
					prefix_box.setSelected(false);
				updateStar();
			});

			l.setConstraints(prefix_box, c);
			l.setConstraints(postfix_box, c);

			p.add(prefix_box);
			p.add(typeField);
			p.add(postfix_box);

		}
		JPanel plug = new JPanel();
		plug.setPreferredSize(new Dimension(changeable ? WIDTH - 2
				* TEXT_EDIT_SIZE.width - 2 * CHECK_BOX_SIZE.width
				- STAR_SIZE.width - DISTANCE_SIZE.width - 20 * VGAP : WIDTH
				- TEXT_EDIT_SIZE.width - STAR_SIZE.width
				- TYPE_LABEL_SIZE.width - 8 * VGAP, 1));
		l.setConstraints(plug, c);
		p.add(plug);
		return p;
	}

	public JPanel getAttributeValueEditPanel() {
		valueEditOpen = true;
		p = new JPanel();
/*		System.out.println("ATTRIBUTE VALUE EDIT PANEL TYPE = " + name + " "
				+ type + " : " + value);*/
		valueField = new JTextField();
		star = new JLabel((prefix || postfix || necessary) ? " " : "*");
		JLabel typeLabel = new JLabel(name.equals("") ? type : name);
		GridBagLayout l = new GridBagLayout();
		p.setLayout(l);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(VGAP, HGAP, VGAP, HGAP);
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridy = GridBagConstraints.RELATIVE;
		l.setConstraints(star, c);
		l.setConstraints(valueField, c);
		l.setConstraints(typeLabel, c);
		JPanel plug = new JPanel();
		// plug.setPreferredSize(new Dimension(200, 20));
		l.setConstraints(plug, c);
		star.setPreferredSize(STAR_SIZE);
		valueField.setPreferredSize(TEXT_EDIT_SIZE);
		valueField.setDocument(new JTextFieldLimit(40));
		valueField.setText(value);
		valueField.setHorizontalAlignment(JTextField.RIGHT);
		typeLabel.setPreferredSize(TYPE_LABEL_SIZE);
		p.add(star);
		p.add(valueField);
		p.add(typeLabel);
		plug.setPreferredSize(new Dimension(WIDTH - TEXT_EDIT_SIZE.width
				- STAR_SIZE.width - TYPE_LABEL_SIZE.width - 8 * VGAP, 20));
		p.add(plug);
		return p;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public boolean isChangeable() {
		return changeable;
	}

	public boolean isPostfix() {
		return postfix;
	}

	public boolean isPrefix() {
		return prefix;
	}

	protected void notifySearchListeners(String type, String value) {
		for (SearchListener l : sl) {
			System.out.println("SEARCHING!!!");
			l.performSearch(new SearchEvent(type, value, this));
		}
	}

	public void save(Element tool, Document doc) {
		Element att = doc.createElement("attribute");
		tool.appendChild(att);
		att.setAttribute("type", type);
		att.setAttribute("name", name);
		att.setAttribute("value", value);
		att.setAttribute("prefix", "" + prefix);
		att.setAttribute("postfix", "" + prefix);
		att.setAttribute("necessary", "" + necessary);
		att.setAttribute("changeable", "" + changeable);
		att.setAttribute("searchable", "" + searchable);

	}

	public static Attribute load(Element attrib, Document doc) {
		Attribute att = new Attribute();
		try {
			att.setType(Misc.xmlGetString("type", attrib));
			att.setName(Misc.xmlGetString("name", attrib));
			att.setValue(Misc.xmlGetString("value", attrib));
			att.setPrefix(Boolean.parseBoolean(Misc.xmlGetString("prefix",
					attrib)));
			att.setPostfix(Boolean.parseBoolean(Misc.xmlGetString("postfix",
					attrib)));
			att.setNecessary(Boolean.parseBoolean(Misc.xmlGetString(
					"necessary", attrib)));
			att.setChangeable(Boolean.parseBoolean(Misc.xmlGetString(
					"changeable", attrib)));
			att.setSearchable(Boolean.parseBoolean(Misc.xmlGetString(
					"searchable", attrib)));
		} catch (Exception e) {
			System.out.println("Problem loading xml");
		}
		return att;
	}

	public void saveFromForm(ActionEvent e) {
		if (attributeEditOpen) {
			attributeEditOpen = false;
			value = valueField.getText();
			if (changeable) {
				type = typeField.getText();
				prefix = prefix_box.isSelected();
				postfix = postfix_box.isSelected();
				necessary = prefix || postfix;
			}

		}
		if (valueEditOpen) {
			valueEditOpen = false;
			value = valueField.getText();
		}
	}

	public void setChangeable(boolean isChangeable) {
		this.changeable = isChangeable;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNecessary(boolean isNecessary) {
		this.necessary = isNecessary;
	}

	public void setPostfix(boolean postfix) {
		this.postfix = postfix;
	}

	public void setPrefix(boolean prefix) {
		this.prefix = prefix;
	}

	public void setType(String type) {
		if (this.name.equals(""))
			this.name = type;
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private void updateStar() {
		if (attributeEditOpen) {
			if (necessary || prefix_box.isSelected()
					|| postfix_box.isSelected()) {
				star.setText(" ");
			} else {
				star.setText("*");
			}
		}
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public String getTextFieldValue() {
		if (valueField!=null) return valueField.getText();
		return "";
	}

	public String getEntry() {
		StringBuilder sb = new StringBuilder ();
		sb.append (prefix? this.getType():"");
		sb.append(this.value);
		sb.append (postfix? this.getType():"");
		return sb.toString();
	}

	public boolean isNecessary() {
		if (!necessary) necessary = prefix||postfix;
		return necessary;
	}

	public void restoreTextFields() {
		if (valueField!=null) {
			valueField.setText(this.value);
		}
		if (typeField!=null) {
			typeField.setText(this.getType());
		}
	}
	@Override
	public boolean equals(Object obj) {
		return this.type.equals(((Attribute)obj).type);
	}
}
