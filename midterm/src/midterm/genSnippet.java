package midterm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class genSnippet {

	private File inputFile;
	private BufferedReader curFile;
	
	private int[] resultArray;
	
	
	public genSnippet(String inputPath, String query) {
		
		inputFile = new File(inputPath);
		
		resultArray = new int[5];
		try {
			
			String[] queryList = query.split(" ");
			
			for (String s : queryList) {
				calc(s);
			}
			
			showLine(getBestLine());
			
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	int getBestLine() {
		int bestIdx = 0;
		for (int i = 1; i < 5; i++) {
			if (resultArray[i] > resultArray[bestIdx]) {
				bestIdx = i;
			}
		}
		
		return bestIdx;	
		
	}
	
	void calc(String query) throws UnsupportedEncodingException, FileNotFoundException {
		curFile = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
		String curLine = "";
		int lineCnt = 0;
		try {
			while ((curLine = curFile.readLine()) != null) {
				if (curLine.contains(query)) {
					resultArray[lineCnt]++;
				}
				lineCnt++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// 마지막에 라인 출력
	void showLine(int lineNum) throws UnsupportedEncodingException, FileNotFoundException {
		String curLine = "";
		curFile = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
		int n = 0;
		
		try {
			while ((curLine = curFile.readLine()) != null) {
				if (n == lineNum) {
					System.out.println(curLine);
					return;
				}
				n++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
