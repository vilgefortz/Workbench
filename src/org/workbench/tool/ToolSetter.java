package org.workbench.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.workbench.tool.attributes.Attribute;

public class ToolSetter {
	private final static Logger LOGGER = Logger.getLogger(ToolSetter.class
			.getName());
	private static final String PATH = MainSettings.PATH;
	private static final String SETTINGS_FILE_NAME = "tool_build.xml";
	private static ArrayList<ToolSetting> settings = new ArrayList<>();
	private static int nameAttribNumber = 1;
	private static int typeAttribNumber = 0;

	/**
	 * escape chars ** - * *{ - { *} - } {TYPE} - type of tool {NAME} - name of
	 * tool {T1} - type of first attribute {N1} - name of first attribute {V1} -
	 * value of first attriubte
	 */

	private static String defaultDescriptionPattern = "{T1}{V1}.{T2}{V2}.{T3}{V3}.{E4}.{E5}.";
	private static int platteAttribNumber=2;

	// *{}* "{" {*}
	public static void loadToolSetter() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(PATH + SETTINGS_FILE_NAME));
			settings.clear();
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			try {
				nameAttribNumber = Integer.parseInt(root
						.getAttribute("name.number"));
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "no name number set");
			}
			;
			try {
				typeAttribNumber = Integer.parseInt(root
						.getAttribute("type.number"));
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "no type number set");
			}
			;
			try {
				platteAttribNumber = Integer.parseInt(root
						.getAttribute("platte.number"));
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "no platte number set");
			}
			;

			NodeList attribs = doc.getElementsByTagName("attribute");

			for (int temp = 0; temp < attribs.getLength(); temp++) {
				Node nNode = attribs.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ToolSetting ts = new ToolSetting();
					try {
						ts.setChangeable(Boolean.parseBoolean(eElement
								.getAttribute("changeable").equals("") ? "true"
								: eElement.getAttribute("changeable")));
					} catch (Exception e) {
						LOGGER.log(Level.INFO, "Attribute : " + temp
								+ " changeable default");
					}
					;
					try {
						ts.setNecessary(Boolean.parseBoolean(eElement
								.getAttribute("necessary").equals("") ? "false"
								: eElement.getAttribute("necessary")));
					} catch (Exception e) {
						LOGGER.log(Level.INFO, "Attribute : " + temp
								+ " necessary default");
					}
					;
					try {
						ts.setSearchable(Boolean
								.parseBoolean(eElement.getAttribute(
										"searchable").equals("") ? "false"
										: eElement.getAttribute("searchable")));
					} catch (Exception e) {
						LOGGER.log(Level.INFO, "Attribute : " + temp
								+ " searchable default");
					}
					;
					ts.setName(eElement.getAttribute("name"));
					ts.setType(eElement.getAttribute("type"));
					settings.add(ts);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setupTool(Tool tool) {
		for (ToolSetting ts : settings) {
			Attribute a = new Attribute();
			a.setChangeable(ts.isChangeable());
			a.setNecessary(ts.isNecessary());
			a.setSearchable(ts.isSearchable());
			a.setType(ts.getType());
			a.setName(ts.getName());
			tool.add(a);
		}
	}

	public static String getPlatte(Tool tool) {
		return tool.get(platteAttribNumber).getValue();
	}

	public static String getName(Tool tool) {
		return tool.get(nameAttribNumber).getValue();
	}

	public static void setName(Tool tool, String dummy) {
		tool.get(nameAttribNumber).setValue(dummy);
	}

	public static String getType(Tool tool) {
		return tool.get(typeAttribNumber).getValue();
	}

	public static void setType(Tool tool, String typeName) {
		tool.get(typeAttribNumber).setValue(typeName);
	}

	public static String getTypeFromForm(Tool tool) {
		return tool.get(typeAttribNumber).getValueField().getText();
	}

	public static String getNameFromForm(Tool tool) {
		return tool.get(nameAttribNumber).getValueField().getText();
	}

	public static String createDefaultDescription(Tool tool) {
		StringBuilder sb = new StringBuilder ();
		sb.append("(&");
		sb.append(tool.get(typeAttribNumber).getType());
		sb.append(tool.getType());
		sb.append(".");
		sb.append(tool.get(nameAttribNumber).getType());
		sb.append(tool.getName());
		
		for (int i=0; i<tool.size(); i++) {
			if (i==nameAttribNumber||  i == platteAttribNumber || i ==typeAttribNumber) continue;
			if (!tool.get(i).isNecessary()) continue;
			sb.append(".");
			sb.append(tool.get(i).getEntry());
		}
		
		sb.append(".&.");
	//	sb.append(tool.get(platteAttribNumber).getType());
		sb.append(tool.getPlatte());
		sb.append(")");
		
		return sb.toString();
	}

	
	public static String createDescription(Tool tool, String dp) {
		// first switch all escape characters - change them into something else
		class Constants {
			final String[] plugs = new String[] { "" + Math.random(),
					"" + Math.random(), "" + Math.random() };
			final String[] source = new String[] { "**", "*{", "*}" };
			final String[] dest = new String[] { "*", "{", "}" };

		}
		;
		Constants constants = new Constants();
		String temp = dp;
		dp = replaceEscapeCharacters(dp, constants.source, constants.dest);
		temp = replaceEscapeCharacters(temp, constants.source, constants.plugs);
		String[] firstBracketList;

		firstBracketList = temp.split("\\{");
		String[] patternList = new String[firstBracketList.length - 1];
		// first string was not in bracket so will be ommited
		for (int i = 1; i < firstBracketList.length; i++) {
			patternList[i - 1] = firstBracketList[i].substring(0,
					firstBracketList[i].indexOf("}"));
		}
		String[] resultList = new String[patternList.length];
		for (int i = 0; i < patternList.length; i++) {
			patternList[i] = replaceEscapeCharacters(patternList[i],
					constants.plugs, constants.dest);
			if (patternList[i].equals("TYPE"))
				resultList[i] = tool.getType();
			else if (patternList[i].equals("NAME"))
				resultList[i] = tool.getName();
			else if (patternList[i].equals("WND"))
				resultList[i] = tool.getWindowName();
			else if (patternList[i].startsWith("T"))
				resultList[i] = tool.get(getIndexFromPattern(patternList[i]))
						.getType();
			else if (patternList[i].startsWith("N"))
				resultList[i] = tool.get(getIndexFromPattern(patternList[i]))
						.getName();
			else if (patternList[i].startsWith("V"))
				resultList[i] = tool.get(getIndexFromPattern(patternList[i]))
						.getValue();
			else if (patternList[i].startsWith("E"))
				resultList[i] = tool.get(getIndexFromPattern(patternList[i]))
						.getEntry();
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append(patternList[i]);
			sb.append("}");
			patternList[i] = sb.toString();

			dp = dp.replaceAll(Pattern.quote(patternList[i]), resultList[i]);
		}

		System.out.println(dp);
		return dp;
	}

	private static int getIndexFromPattern(String string) {
		return Integer.parseInt(string.substring(1, string.length()));
	}

	public static String replaceEscapeCharacters(String source, String[] chars,
			String[] replacement) {
		String result = source;
		if (chars.length != replacement.length)
			return null;
		boolean found = true;
		int start = 0;
		while (found) {
			found = false;
			int position = source.length();
			// *{}*{{*}
			int index = 0;
			for (int i = 0; i < chars.length; i++) {
				String temp = source.substring(start, source.length());
				int newPos = temp.indexOf(chars[i]);
				if (newPos == -1)
					continue;
				newPos += start;
				if (newPos < position) {
					found = true;
					index = i;
					position = newPos;
				}
			}
			if (!found)
				break;
			StringBuilder sb = new StringBuilder();
			String begining = source.substring(0, position);
			String ending = source.substring(position + chars[index].length(),
					source.length());
			sb.append(begining);
			sb.append(replacement[index]);
			sb.append(ending);
			source = sb.toString();
			start = position + replacement[index].length();
		}

		return source;

	}
	/**
	 * escape chars ** - * *{ - { *} - } {TYPE} - type of tool {NAME} - name of
	 * tool {E1} - prepared attribute (prefix/postfix) {T1} - type of first
	 * attribute {N1} - name of first attribute {V1} - value of first attriubte
	 */
}
