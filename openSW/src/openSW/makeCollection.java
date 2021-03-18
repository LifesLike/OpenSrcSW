package openSW;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class makeCollection {

	private String htmlDirPath;
	private File dir;
	private File[] html_lists;
	private File collection;
	
	private BufferedReader curFileReader;
	private BufferedWriter collectionWriter;
	
	
	//
	private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private Document doc;
	
	private TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private Transformer transformer;
	//
	
	public makeCollection(String path) {
		htmlDirPath = path;
		dir = new File(htmlDirPath);
		
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			transformer = transformerFactory.newTransformer();
			init();
			makeFile();
		} catch (ParserConfigurationException | TransformerConfigurationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void init() throws IOException {
		int i = 0;
		html_lists = dir.listFiles();
		Element tagDocs = doc.createElement("docs");
		doc.appendChild(tagDocs);
		
		for (File curFile : html_lists) {
			Element tagDocId = doc.createElement("doc");
			tagDocs.appendChild(tagDocId);
			tagDocId.setAttribute("id", Integer.toString(i));
			
			org.jsoup.nodes.Document sourceDoc = Jsoup.parse(curFile, "UTF-8");
			Elements titleElem = sourceDoc.getElementsByTag("title");
			String titleContent = titleElem.text();
			org.jsoup.nodes.Element bodyElem = sourceDoc.getElementById("content");
			String bodyContent = bodyElem.text();
			
			Element tagTitle = doc.createElement("title");
			tagTitle.appendChild(doc.createTextNode(titleContent));
			tagDocId.appendChild(tagTitle);
			
			Element body = doc.createElement("body");
			body.appendChild(doc.createTextNode(bodyContent));
			tagDocId.appendChild(body);
			
			i += 1;
		}
	}
	
	
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
	
}
