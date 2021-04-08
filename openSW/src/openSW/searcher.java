package openSW;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class searcher {
	
	private FileInputStream fs;
	private ObjectInputStream os;
	private HashMap<String, List<Object>> readObj;
	private List<Double> similarity;
	private int docCnt;
	
	private File toReadXmlFile;
	private DocumentBuilderFactory dbFactoty;
	private DocumentBuilder dBuilder;
	private Document doc;
	private NodeList nl;
	
	
	
	public searcher(String objName, int docCnt) {
		try {
			fs = new FileInputStream(objName);
			os = new ObjectInputStream(fs);
			readObj = (HashMap<String, List<Object>>) os.readObject();
			
			this.docCnt = docCnt;
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		docBuilderInit();
	}


	public void CalcSim(String query) {
		
	}

	private void docBuilderInit() {
	}
	

}
