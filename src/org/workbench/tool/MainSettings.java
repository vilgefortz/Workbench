package org.workbench.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class MainSettings {
	public static final String PROPERTIES = "config.xml";
	public static final String PATH = "data/";
	public static final String EXTENSION = "png";
	public static Properties prop= new Properties();
	
	public static void loadSettings() {
		ToolSetter.loadToolSetter();
		loadProperties();
	}
	
	public static void saveProperties () {
		try {
			OutputStream output = new FileOutputStream(PATH+PROPERTIES);
			prop.storeToXML(output, "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public static void loadProperties () {
		try {
			
			InputStream input = new FileInputStream(PATH+PROPERTIES);
			prop.loadFromXML(input);
			input.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void finish() {
		saveProperties();
	}
}
