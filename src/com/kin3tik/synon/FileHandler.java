package com.kin3tik.synon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FileHandler {
	
	private final boolean DEBUG = true;
	private String dictSavePath = "synon.dict";
	private String ignoreListSavePath = "synon.il";
	
	public void saveDict(HashMap<String, ArrayList<String>> dict) {
		try {
			FileOutputStream fos = new FileOutputStream(dictSavePath);;
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    
		    oos.writeObject(dict);
		    
		    oos.close();
		    fos.close();
		    if(DEBUG)System.out.println("FileHandler: saving dict");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>> loadDict() {
		HashMap<String, ArrayList<String>> dict;
		
		if(new File(dictSavePath).isFile()) {
			//files exists, load it
			try {
				FileInputStream fis = new FileInputStream(dictSavePath);
			    ObjectInputStream ois = new ObjectInputStream(fis);
			    
			    dict = (HashMap<String, ArrayList<String>>) ois.readObject();
			    
			    ois.close();
			    fis.close();
			    
			    if(DEBUG)System.out.println("FileHandler: dict exitst, attempting to load");
			    return dict;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}			
			
		} else {
			//else make a new dictionary
			dict = new HashMap<String, ArrayList<String>>();
			if(DEBUG)System.out.println("FileHandler: creating a new dictionary");
			return dict;
		}
		
		if(DEBUG)System.out.println("FileHandler: dict load failed");
		return null;
	}
	
	public void saveIgnoreList(ArrayList<String> ignoreList) {
		try {
			FileOutputStream fos = new FileOutputStream(ignoreListSavePath);;
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    
		    oos.writeObject(ignoreList);
		    
		    oos.close();
		    fos.close();
		    if(DEBUG)System.out.println("FileHandler: saving ignore list");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> loadIgnoreList() {
		ArrayList<String> ignoreList;
		
		if(new File(ignoreListSavePath).isFile()) {
			//files exists, load it
			try {
				FileInputStream fis = new FileInputStream(ignoreListSavePath);
			    ObjectInputStream ois = new ObjectInputStream(fis);
			    
			    ignoreList = (ArrayList<String>) ois.readObject();
			    
			    ois.close();
			    fis.close();
			    
			    if(DEBUG)System.out.println("FileHandler: ignore list exists, attempting to load");
			    return ignoreList;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}			
			
		} else {
			//else make a new list
			ignoreList = new ArrayList<String>(Arrays.asList(
					"a", "i", "the", "is", "at", "it"));
			if(DEBUG)System.out.println("FileHandler: creating a new ignore list");
			return ignoreList;
		}
		
		if(DEBUG)System.out.println("FileHandler: dict load failed");
		return null;
	}
	
	public String getSavePath() {
		return this.dictSavePath;
	}
}