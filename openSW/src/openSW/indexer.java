package openSW;

import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class indexer {

	private File toReadXmlFile;
	private HashMap<String, List<Object>> outHashMapFile;
	private HashMap<Integer, String> docHashMap;
	private int docSize;
	private BufferedReader reader;
	private FileOutputStream fileStream;
	private ObjectOutputStream objectStream;
	
	private Document sourceDoc;
	
	
	public indexer(String path) {
		toReadXmlFile = new File(path);
		docHashMap = new HashMap<>();
		outHashMapFile = new HashMap<String, List<Object>>();
		
		init();
		makeHashMap();
		outHashMap();
	}
	
	//해시맵 객체 직렬화
	private void outHashMap() {
		try {
			objectStream.writeObject(outHashMapFile);
			objectStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void makeHashMap() {
		for (int i = 0; i < docSize; i++) {
			String[] srcStr = docHashMap.get(i).split("#");
			
			for (int j = 0; j < srcStr.length; j++) {
				String srcKey = srcStr[j].split(":")[0];
				int frequency = Integer.parseInt(srcStr[j].split(":")[1]);
				int wordInDocumentCnt = 1;
				
				for (int k = 0; k < docSize; k++) {
					if (i == k) {
						continue;
					}
					String[] dstStr = docHashMap.get(k).split("#");
					
					for (int l = 0; l < dstStr.length; l++) {
						if (dstStr[l].split(":")[0].equals(srcKey)) {
							wordInDocumentCnt++;
							break;
						}
					}
					
				}
				
				List<Object> tempList = outHashMapFile.get(srcKey);
				
				if (tempList == null) {
					tempList = new ArrayList<>();
					double weight = getWeight(frequency, wordInDocumentCnt); 
					tempList.add(i);
					tempList.add(weight);
					outHashMapFile.put(srcKey, tempList);
				}
				else {
					double weight = getWeight(frequency, wordInDocumentCnt); 
					tempList.add(i);
					tempList.add(weight);
				}

				
			}
			
			
		}
	}
	
	
	private void makeBodyToHashMap() {
		String curLine = "";
		int id = -1;
		try {
			while ((curLine = reader.readLine()) != null) {
				if (curLine.contains("id")) {
					id++;
					continue;
				}
				if (curLine.contains("body")) {
					docHashMap.put(id, removeTag(curLine).strip());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private String removeTag(String curLine) {
		return curLine.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}
	
	
	//가중치 계산
	private double getWeight(int frequency, int cnt) {
		double temp = frequency * Math.log(docSize / (double) cnt);
		temp = Math.round(temp * 100) / 100.0;
		return temp;
	}
	
	
	//문서 개수 반환
	private int cntDocId(File file) {
		try {
			sourceDoc = Jsoup.parse(file, "UTF-8");
			return sourceDoc.getElementsByTag("doc").size();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	private void init() {
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(toReadXmlFile), "UTF-8"));
			docSize = cntDocId(toReadXmlFile);
			makeBodyToHashMap();
			fileStream = new FileOutputStream("index.post");
			objectStream = new ObjectOutputStream(fileStream);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
