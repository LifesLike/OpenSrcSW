package openSW;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class makeKeyword {
	
	private String xmlPath;
	private File xmlFile;
	private BufferedReader curFile;
	
	
	// xml 파일 생성용 변수
	private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private Document createDoc;
	private TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private Transformer transformer;
	private Element tagDocs;
	//
	
	
	public makeKeyword(String path) {
		xmlPath = path;
		xmlFile = new File(xmlPath);
		init();
		getContents(curFile);
		makeFile();
	}
	
	
	/*
	 * xml 파일 생성을 위한 document 객체 초기
	 */
	private void init() {
		try {
			docBuilder = docFactory.newDocumentBuilder();
			createDoc = docBuilder.newDocument();
			transformer = transformerFactory.newTransformer();
			
			tagDocs = createDoc.createElement("docs");
			createDoc.appendChild(tagDocs);
			
			curFile = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile), "UTF-8"));

			
		} catch (ParserConfigurationException | TransformerConfigurationException | UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	
	/*
	 * 1. collection.xml 문서로 부터 제목과 내용을 읽어서 리스트에 저장
	 * 2. 리스트의 내용을 읽어서 태그에 붙임
	 */
	private void getContents(BufferedReader curFileReader) {
		String curLine = "";
		List<String> titleList = new ArrayList<>();
		List<String> bodyList = new ArrayList<>();
		
		try {
			while ((curLine = curFileReader.readLine()) != null) {
				if (curLine.contains("title")) {
					titleList.add(Jsoup.parse(curLine).text());
				}
				else if (curLine.contains("body")) {
					bodyList.add(Jsoup.parse(curLine).text());
				}
			}
			
			for (int i = 0; i < titleList.size(); i++) {
				Element tagDocId = createDoc.createElement("doc");
				tagDocs.appendChild(tagDocId);
				tagDocId.setAttribute("id", Integer.toString(i));
				
				Element tagTitle = createDoc.createElement("title");
				tagTitle.appendChild(createDoc.createTextNode(titleList.get(i)));
				tagDocId.appendChild(tagTitle);
				
				Element body = createDoc.createElement("body");
				body.appendChild(createDoc.createTextNode(kkmReturnString(bodyList.get(i))));
				tagDocId.appendChild(body);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	
	/*
	 * 입력받은 문자열에 대해 형태소 분석 결과를 리턴
	 */
	private String kkmReturnString(String body) {
		String returnString = "";
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(body, true);
		
		for (int i = 0; i < kl.size(); i++) {
			Keyword kw = kl.get(i);
			returnString += kw.getString() + ":" + kw.getCnt();
			if (i < kl.size()) {
				returnString += "#";
			}
		}
		
		return returnString;
	}
	
	
	/*
	 * Dom 객체 생성
	 */
	private void makeFile() {
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result;
		
		try {
			result = new StreamResult(new FileOutputStream(new File("index.xml")));
			DOMSource source = new DOMSource(createDoc);
			transformer.transform(source, result);
		} catch (FileNotFoundException | TransformerException e) {
			e.printStackTrace();
		}
		
	}

	
}
