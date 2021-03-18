package openSW;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class SimpleParser {
	
	private String htmlDirPath = "2주차 실습 html";
	private File dir;
	private File[] html_lists;
	private File collection;
	
	private BufferedReader curFile;
	private BufferedWriter collectionWriter;
	
	
	//
	private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private Document doc;
	
	private TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private Transformer transformer;
	//
	
	public SimpleParser() {
		dir = new File(htmlDirPath);
		
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			transformer = transformerFactory.newTransformer();
		} catch (ParserConfigurationException | TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	void refreshFiles() { 
		html_lists = dir.listFiles();
//		makeFile();	
		
		Element tagDocs = doc.createElement("docs");
		doc.appendChild(tagDocs);
		
		
		
		try {
//			collectionWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(collection), "UTF8"));
			
			for (int i = 0; i < html_lists.length; i++) {
				curFile = new BufferedReader(new InputStreamReader(new FileInputStream(html_lists[i]), "UTF8"));
				Element tagDocId = doc.createElement("doc");
				tagDocs.appendChild(tagDocId);
				tagDocId.setAttribute("id", Integer.toString(i));
				
				updateCollection(curFile, tagDocId);
			}
			
			makeFile();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void updateCollection(BufferedReader curFile, Element tagDocId) throws IOException {
		String curLine = "";
		String title = "";
		String temp = "";
		
		while ((curLine = curFile.readLine()) != null) {
			if(curLine.contains("title")) {
				title = removeTag(curLine);
				continue;
			}
			if(curLine.contains("body")) {
				while (!(curLine = curFile.readLine()).contains("body")) {
					temp += removeTag(curLine);
				}
			}
		}
		
		
		Element tagTitle = doc.createElement("title");
		tagTitle.appendChild(doc.createTextNode(title));
		tagDocId.appendChild(tagTitle);
		
		Element body = doc.createElement("body");
		body.appendChild(doc.createTextNode(temp));
		tagDocId.appendChild(body);
		
//		System.out.println(temp);
	}

//	void makeFile() {
//		collection = new File(htmlDirPath + "/collection.xml");
//		try {
//			collection.createNewFile();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//	
	void makeFile() {
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result;
		
		try {
			result = new StreamResult(new FileOutputStream(new File("collection.xml")));
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
		} catch (FileNotFoundException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	 String removeTag(String curLine) {
//		 <(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>
//		 return curLine.replaceAll("", "");
		return curLine.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}
	
}
