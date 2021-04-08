package openSW;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class searcher {
	
	private FileInputStream fs;
	private ObjectInputStream os;
	private HashMap<String, List<Object>> readObj;
	private List<Double> similarity;
	private List<Double>[] docWeightList;
	private double[] cosSimilarity;
	private List<Double> totalKeyWeidht;
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
		
		totalKeyWeidht = new LinkedList<>();
		cosSimilarity = new double[docCnt];
		docWeightList = new List[docCnt];
		
		for (int i = 0; i < docCnt; i++) {
			docWeightList[i] = new LinkedList<>();
		}
		
		InnerProduct(query);
		setCosSimilarityDenominator(query);
		
		double absoluteA = 0.0;
		for (double i : totalKeyWeidht) {
			absoluteA += i;
		}
		
		absoluteA = Math.sqrt(absoluteA);
		calcCosSimilarity(absoluteA);

		for (double i : cosSimilarity) {
			System.out.println(i);
		}
		
	}

	
	
	public void InnerProduct(String query) {
		
		similarity = new LinkedList<>();
		for (int i = 0; i < docCnt; i++) {
			similarity.add(0.0);
		}
		
		calculateQuery(query);
//		showTitle();
	}
	
	// 새로운 calcSim 
	private void calcCosSimilarity(double absoluteA) {
		
		for (int i = 0; i < docCnt; i++) {
			double absoluteB = 0.0;
			for (double j : docWeightList[i]) {
				absoluteB += j;
			}
			System.out.println(absoluteB);
			System.out.println("분자 : " + similarity.get(i));
			System.out.println("분모 : " + (absoluteA * Math.sqrt(absoluteB)));
			cosSimilarity[i] = similarity.get(i) / (absoluteA * Math.sqrt(absoluteB));
		}
		
	}
	
	private void setCosSimilarityDenominator(String query) {
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);
		
		for (Keyword key : kl) {
			String keyName = key.getString();
			int keyWeight = key.getCnt();
			totalKeyWeidht.add(Math.pow(keyWeight, 2));
			
			List<Object> readObjValue = readObj.get(keyName);
			
			try {
				for (int i = 0; i < readObjValue.size() / 2; i = i + 2) {
					double docWeight = (double) readObjValue.get(i + 1); 
					docWeightList[i].add(Math.pow(docWeight, 2));
				}
			}
			catch (NullPointerException e) {
				continue;
			}
		}
	}
	//
	
	
	private void calculateQuery(String query) {
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);
		
		for (Keyword key : kl) {
			String keyName = key.getString();
			int keyWeight = key.getCnt();
			
			List<Object> readObjValue = readObj.get(keyName);
			
			try {
				for (int i = 0; i < readObjValue.size() / 2; i = i + 2) {
					int index = (int)readObjValue.get(i);
					double docWeight = (double) readObjValue.get(i + 1); 
					double tempValue = similarity.get(index);
					
					similarity.set(index, tempValue + (keyWeight * docWeight));
				}
			}
			catch (NullPointerException e) {
				continue;
			}
			
		
		}
		
	}
	
	private void showTitle() {
		double target = 0.0;
		int targetIdx = 0;
		int arrCnt = 0;
		int[] targetDocIds = new int[3];
		
		for (int i = 0; i < 3; i++) {
			target = 0.0;
			targetIdx = -1;
			for (int j = 0; j < docCnt; j++) {
				if (similarity.get(j) > target) {
					boolean isIn = false;
					for (int k = 0; k < arrCnt; k++) {
						if (targetDocIds[k] == j) {
							isIn = true;
							break;
						}
					}

					if (!isIn) {
						target = similarity.get(j);
						targetIdx = j;
					}
					
				}
			}
			if (targetIdx != -1) {
				targetDocIds[arrCnt++] = targetIdx;
			}
			
			
		}
		
		for (int i = 0; i < arrCnt; i++) {
			System.out.println(searchTitle(targetDocIds[i]));
		}
		
	}
	
	private void docBuilderInit() {
		toReadXmlFile = new File("index.xml");
		dbFactoty = DocumentBuilderFactory.newInstance();

		try {
			dBuilder = dbFactoty.newDocumentBuilder();
			doc = dBuilder.parse(toReadXmlFile);
			doc.getDocumentElement().normalize();
			nl = doc.getElementsByTagName("title");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private String searchTitle(int docId) {
		Node targetNode = nl.item(docId);
		
		try {
			String outTitle = targetNode.getTextContent();
			return outTitle;
		}
		catch (NullPointerException e) {
			return null;
		}
				
	}
	
	

}
