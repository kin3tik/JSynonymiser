package com.kin3tik.synon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.Resty;


public class APIHandler implements Runnable{

	private final boolean DEBUG = true;

	private String APIkey = ""; //Key can be obtained from http://words.bighugelabs.com/api.php
	private ArrayList<String> wordsToIgnore = new ArrayList<String>();
	private String input;
	private HashMap<String, ArrayList<String>> dict;
	private View view;

	public APIHandler(View view, HashMap<String, ArrayList<String>> dict) {
		this.view = view;
		this.dict = dict; //pass by reference	
	}

	/**
	 * Given a word, this method will query the API and return the list of synonyms
	 * @param word
	 * @return 
	 * @throws IOException
	 * @throws JSONException
	 */
	private ArrayList<String> getWordList(String word) throws IOException, JSONException, IllegalArgumentException {
		if(DEBUG)System.out.println("APIHandler: `"+word+"` Getting word list");
		String url = "http://words.bighugelabs.com/api/2/"+APIkey+"/"+word+"/json";
		Resty r = new Resty();
		JSONObject response = r.json(url).object();
		ArrayList<String> result = new ArrayList<String>();

		//for each type of word (noun, verb etc)
		for(int i=0; i < response.names().length(); i++) {
			String wordType = (String) response.names().get(i);
			//get an array of words from wordType
			try {
				JSONArray wordJA = (JSONArray) ((JSONObject) response.get(wordType)).get("syn");

				//append all words from array to result list
				for(int j=0; j<wordJA.length(); j++) {
					result.add(wordJA.getString(j));
				}
			} catch (JSONException e) {
				//sometimes a word type won't contain a "syn" field and causes an exception, but we still want the
				//results from the other word types
				if(DEBUG)System.out.println("APIHandler: `"+word+"` no synonyms found - "+e);
				if(result.isEmpty()) {
					return null;
				} else {
					return result;
				}
			}

		}

		return result;
	}

	/**
	 * Action performed when used as a thread. Builds a replacement string
	 * and updates the GUI with the result.
	 */
	public void run() {
		String result = buildResult(this.input);
		view.setOutputText(result);
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getInput() {
		return this.input;
	}
	
	public ArrayList<String> getWordsToIgnore() {
		return wordsToIgnore;
	}

	public void setWordsToIgnore(ArrayList<String> wordsToIgnore) {
		this.wordsToIgnore = wordsToIgnore;
	}

	//UTIL==============================

	/**
	 * Builds resulting synonym string from the users input
	 * @param input - user input
	 * @return String - resulting string from synonym replacement
	 */
	private String buildResult(String input) {
		String result = "";
		String nonWordRegex = "[\\s,.\\?!\";:-]"; //to avoid useless API calls
		ArrayList<String> wordList = processText(input);
		
		for(int i=0; i<wordList.size(); i++) {
			String word = wordList.get(i);
			if(word.matches(nonWordRegex) || wordsToIgnore.contains(word.toLowerCase())) {
				if(DEBUG) System.out.println("APIHandler: `"+word+"` Punctuation/Ignore word");
				//whitespace and punctuation can be ignored
				result += word;
			} else {
				result += getReplacement(word);
			}
			updateProgress(i+1, wordList.size());
		}

		return result;
	}
	
	/**
	 * Updates the progress bar within the view
	 * @param current - current item number
	 * @param max - total number of items
	 */
	private void updateProgress(int current, int max) {
		float tmp = ((float)current/(float)max)*100;
		int n = (int)tmp;
		view.getProgressBar().setValue(n);
	}

	/**
	 * Splits users input text on words. 
	 * @param input - user input string
	 * @return ArrayList<String> - collection of words and other characters (punctuation)
	 */
	private ArrayList<String> processText(String input) {
		Pattern stuff = Pattern.compile("[\\w']+|[\\s\".,!\\?;:-]");

		Matcher matcher = stuff.matcher(input);
		ArrayList<String> matchList = new ArrayList<String>();

		while (matcher.find()) {
			matchList.add(matcher.group(0));
		}

		return matchList;
	}

	/**
	 * Gets API query result and adds it to the dictionary
	 * @param word - word to add cached result to dict for
	 */
	private void updateDict(String word) {
		String lword = word.toLowerCase();

		try {
			ArrayList<String> result = getWordList(lword);
			dict.put(lword, result);
		} catch (IOException e) {
			//no result, append null to dict as result
			if(DEBUG)System.out.println("APIHandler: `"+word+"` no result, appending null to dict as result - "+e);
			dict.put(lword, null);
		} catch (JSONException e) {
			//no syn, but might be ant?
			if(DEBUG)System.out.println("APIHandler: `"+word+"` no synonyms found, appending null to dict as result - "+e);
			System.out.println(e);
			dict.put(lword, null);
		} catch (IllegalArgumentException e) {
			if(DEBUG)System.out.println("APIHandler: `"+word+"` illegal argument, appending null to dict as result - "+e);
			dict.put(lword, null);
		}
	}

	/**
	 * Get a synonym replacement for a single word. Will either return a cached
	 * result from the dictionary or query the API if it does not exist in the 
	 * dictionary. If no synonyms exist the input word will be returned.
	 * @param word - the word to get a replacement for
	 * @return String - the replacement word
	 */
	private String getReplacement(String word) {
		String lword = word.toLowerCase();

		if(dict.containsKey(lword)) {
			//dictionary has cached result, get from that
			if(DEBUG)System.out.println("APIHandler: `"+word+"` dictionary has cached result");
			if(dict.get(lword) == null) {
				//replacement doesn't exist, just return current "word"
				if(DEBUG)System.out.println("APIHandler: `"+word+"` no replacement exists");
				return word;
			} else {
				//return a random choice from dictionary results
				return pick(dict.get(lword));
			}
		} else {
			//get a result from API
			if(DEBUG)System.out.println("APIHandler: `"+word+"` getting a result from api");
			updateDict(word);
			if(dict.get(lword) == null) {
				return word;
			} else {
				return pick(dict.get(lword));
			}
		}
	}

	/**
	 * Returns a randomly chosen word from wordList
	 * @param wordList - list of words to choose from
	 * @return String - the chosen word
	 */
	private String pick(ArrayList<String> wordList) {
		int size = wordList.size();
		int choice = (int)(Math.random() * size);

		return wordList.get(choice);
	}

}
