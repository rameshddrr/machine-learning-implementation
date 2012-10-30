package fileutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import project1.ProjUtils;


public class FileUtils {

	public static void main(String[] args) {
		createPossibleFiles();
	}

	public static void createPossibleFiles(){
		String file = "spambase.data";
		
		ArrayList<ArrayList<String>> allPerms =
				new ArrayList<ArrayList<String>>();
		
		HashMap<Integer, ArrayList<String>> map = ProjUtils.parseFolds(file);
		ProjUtils.returnAllPossibleCombos(map);
		
		
	}
	
	public static ArrayList<String> paragraphsToSentencesLingpipe(String para) {
		return null;
	}

	public static ArrayList<String> paragraphsToSentences(String para) {
		String[] sentence = para.split("[\\.\\!\\?]");
		ArrayList<String> neatSentences = new ArrayList<String>();
		for (int i=0; i<sentence.length; i++) {
			if (cleanSentences(sentence[i]) != null) {
				neatSentences.add(sentence[i]);
			}
		}

		return neatSentences;
	}

	/*
	 * The function consumes the URL and returns the list of all the files in the Directory
	 * 
	 * @param url 	Url of the folder
	 * @return 		The list of all the file names in the directory 
	 */
	public static ArrayList<String> listAllFilesInDirectory(String url){

		String  files;
		ArrayList<String> allFiles = new ArrayList<String>();
		File  folder = new File(url);
		String[] listOfFiles = folder.list(); 

		if (listOfFiles == null) {
			return null;
		}

		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if(listOfFiles[i].contains(".")) {
				files = listOfFiles[i];
				allFiles.add(files);
			} else {
				listAllFilesInDirectory(url+"\\"+listOfFiles[i]);

			}			
		}		
		return allFiles;
	}


	public static HashMap<String, Integer> removeStopWordsFromHashMap(HashMap<String, Integer> wordList, ArrayList<String> stopWords) {
		HashMap<String, Integer> cleanedMap = new HashMap<String, Integer>(); 
		Set<String> words = wordList.keySet();

		for (String word: words) {
			if (!stopWords.contains(word)) {				
				cleanedMap.put(word, wordList.get(word));
			} else {
				System.out.println("Cleaned  ...... " + word);
			}
		}

		return cleanedMap;
	}


	/*
	 * Consumes the String content and returns the hash map containing the words as the
	 * Key and it's count as the key. This method also cleans the String before returning
	 * the HashMap
	 * 
	 * @param content	String content to be analysed
	 * @param stopURL	List of Stop Words
	 * @return 			The HashMap of words and it's counts
	 */	
	public static HashMap<String, Integer> getWordCountSorted(String content) {

		String cleanedContent = cleanString(content);
		ArrayList<String> cleanedCut = cutString(cleanedContent);

		return sortWordsByCounts(getWordCounts(cleanedCut));
	}


	public static ArrayList<String> contentsOfTheFileAsList(String url){
		ArrayList<String> completeString= new ArrayList<String>();				

		try{
			FileInputStream fstream = new FileInputStream(url);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;			
			while ((strLine = br.readLine()) != null)   {				
				completeString.add(strLine);
			}
			in.close();

		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return completeString;		
	}

	/*
	 * Returns the contents of the file as a String.
	 * 
	 * @param url			The url of the file
	 * @param wantCleaning	True if the String needs to be cleaned or not
	 * @param stopWordsURL	URL of the file containing the Stop words
	 * @return				The content of the file
	 */

	public static String contentsOfTheFile(String url, boolean wantCleaning, 
			String stopWordsURL){
		String completeString="";				
		ArrayList<String> stopWords = FileUtils.simpleCutString(
				FileUtils.contentsOfTheFile(stopWordsURL));
		try{
			FileInputStream fstream = new FileInputStream(url);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;			
			while ((strLine = br.readLine()) != null)   {
				if (wantCleaning) {
					strLine = cleanString(strLine);
				}
				completeString = completeString + strLine;
			}
			in.close();

		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return completeString;		
	}

	/*
	 * Returns the contents of the file as a String.
	 * 
	 * @param url			The url of the file
	 * @param wantCleaning	True if the String needs to be cleaned or not
	 * @param stopWords		List of stopWords used for cleaning if the wantCleaning flag is true
	 * @return				The content of the file
	 */

	public static String contentsOfTheFile(String url, boolean wantCleaning){
		String completeString="";
		try{
			FileInputStream fstream = new FileInputStream(url);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;			
			while ((strLine = br.readLine()) != null)   {
				if (wantCleaning) {
					strLine = cleanString(strLine);
				}
				completeString = completeString + " " + strLine;
			}
			in.close();

		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return completeString;		
	}


	public static String contentsOfTheFile(String url){
		String completeString="";
		try{
			FileInputStream fstream = new FileInputStream(url);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;			
			while ((strLine = br.readLine()) != null)   {				
				completeString = completeString + " " + strLine;
			}
			in.close();

		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return completeString;		
	}

	/*
	 * Converts the file into a list of words and returns the ArrayList of words. No cleaning
	 * happens is stopWords is null
	 * 
	 * @param url	Url of the file
	 * @param stopWords		List of stopWords used for cleaning if the wantCleaning flag is true

	 * @return		The contents of the file as a list
	 */
	public static ArrayList<String> contentsOfTheFileInWords(String url, ArrayList<String> stopWords) {

		return cutString(contentsOfTheFile(url, true));
	}


	public static String cleanSentences(String sentence) {

		sentence = sentence.toLowerCase();				
		return sentence;

	}

	/*
	 * Consumes a String and returns the cleaned String without any punctuations
	 * 
	 * @param dirty		The string to be cleaned
	 * @return 			Cleaned String without the punctuation and the stopWords
	 */
	public static String cleanString(String dirty) {

		System.out.println("Start Cleaning ...");

		dirty = dirty.toLowerCase();

		dirty=dirty.replace("?", "");
		dirty=dirty.replace("[", "");
		dirty=dirty.replace("]", "");
		dirty=dirty.replace("1", "");
		dirty=dirty.replace("2", "");
		dirty=dirty.replace("3", "");
		dirty=dirty.replace("4", "");
		dirty=dirty.replace("5", "");
		dirty=dirty.replace("6", "");
		dirty=dirty.replace("7", "");
		dirty=dirty.replace("8", "");
		dirty=dirty.replace("9", "");
		dirty=dirty.replace("0", "");
		dirty=dirty.replace("+", "");
		dirty=dirty.replace("\\", "");
		dirty=dirty.replace(".", "");
		dirty=dirty.replace(",", "");
		dirty=dirty.replace("%", "");
		dirty=dirty.replace(":", "");
		dirty=dirty.replace("'s", "");
		dirty=dirty.replace("(", "");
		dirty=dirty.replace(")", "");
		dirty=dirty.replace(";", "");
		dirty=dirty.replace("/", "");
		dirty=dirty.replace("^", "");
		dirty=dirty.replace("-", "");
		dirty=dirty.replace("\"", "");
		dirty=dirty.replace("|", "");
		dirty=dirty.replace(" ·", "");
		dirty=dirty.replace("•", "");
		dirty=dirty.replace("&", "");
		dirty=dirty.replace("http", "");
		dirty=dirty.replace("–", "");
		dirty=dirty.replace("—", " ");
		dirty=dirty.replace("?", "");
		dirty=dirty.replace("“","");
		dirty=dirty.replace("”","");

		return dirty;

	}


	/*
	 * Consumes the String and cuts the String by the space and returns the list of words
	 * 
	 * @param str	The String to be cut
	 * @return		The list of the words after cutting
	 */
	public static ArrayList<String> cutString(String str) {
		System.out.println("Start Cutting ....");
		ArrayList<String> strings = new ArrayList<String>();
		String[] cutStrs = str.split(" ");

		for (int i=0; i < cutStrs.length; i++) {
			if (cutStrs[i].length() > 1 && cutStrs[i].length() < 15)
				strings.add(cutStrs[i]);
		}
		System.out.println("Done Cutting ....");
		return strings;		
	}


	public static ArrayList<String> simpleCutString(String str) {

		ArrayList<String> strings = new ArrayList<String>();
		String[] cutStrs = str.split(" ");

		for (int i=0; i < cutStrs.length; i++) {
			if (cutStrs[i] != null)
				strings.add(cutStrs[i]);
		}

		return strings;		
	}

	/*
	 * Consumes all the words and converts them into a HashMap containing the words and
	 * the wordCounts
	 * 
	 * @param allWords	List of words to be converted into HashMap
	 * @return			Map with words and it's count 
	 */
	public static HashMap<String, Integer>	getWordCounts(ArrayList<String> allWords) {
		System.out.println("Word Counting Starts .....");
		HashMap<String, Integer> wordWordCount = new HashMap<String, Integer>();

		for(String word : allWords) {
			Set<String> wordsSeen = wordWordCount.keySet();
			if (wordsSeen.contains(word)) {
				Integer count = wordWordCount.get(word);
				count = count + 1; 
				wordWordCount.put(word, count);
			} else {
				wordWordCount.put(word, 1);
			}
		}
		System.out.println("Word Counting finished .....");
		return wordWordCount;
	}


	/*
	 * Function consumes the HashMap with the words and it's counts and returns the HashMap
	 * with the same words and counts but inserted in the increasing order of their frequency.
	 * 
	 * @param wwc 	Map with word and word count
	 * @return		Map with the word and word count but insertion in increasing order of frequency
	 */
	public static HashMap<String, Integer> sortWordsByCounts(HashMap<String, Integer> wwc) {
		System.out.println("Start Sorting ....");
		Set<String> words = wwc.keySet();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		for (String word : words) {
			counts.add(wwc.get(word));
		}

		Collections.sort(counts);
		System.out.println("Sorting completes. Building the HashMap ....");
		HashMap<String, Integer> sortedMap = new HashMap<String, Integer>();
		for (Integer c : counts) {
			ArrayList<String> wordsWithTheCount = getWordsForCountValues(wwc, c);
			for (String w : wordsWithTheCount) {
				System.out.println(w + " ---> "+ c);
				sortedMap.put(w, c);
			}
		}
		System.out.println("HashMap Built ...");
		return sortedMap;
	}

	/*
	 * Returns the list of words in the HashTable with the word frequency equal to the count.
	 * 
	 * @param wwc	Map with the word and word frequency
	 * @param count	Frequency
	 * @return		List of the all the words with the count equal to frequency
	 */
	public static ArrayList<String> getWordsForCountValues(HashMap<String, Integer> wwc, Integer count) {
		Set<String> words = wwc.keySet();
		ArrayList<String> toRet = new ArrayList<String>();
		for (String word : words) {
			if (wwc.get(word).equals(count)) {
				toRet.add(word);
			}
		}

		return toRet;
	}


	public static void writeToFileNew(String url, String content) {
		try{			
			FileWriter fstream = new FileWriter(url);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(content);			
			out.close();
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}

	}

	public static void writeToFileAlreadyExisting(String url, String content) {

		try { 
			File dest;
			dest = new File(url);
			if(!dest.exists()){
				dest.createNewFile();
			} 

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(url), true));
			bw.write(content);		
			bw.newLine();
			bw.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/*
	 * Print the HashMap to the console neatly.
	 */
	public static void printWordCounts(HashMap<String, Integer> wWc) {
		Set<String> keys = wWc.keySet();

		for (String key: keys) {
			System.out.println(key + " ---> "+ wWc.get(key));
		}
	}


	/*
	 * Print the List to the console neatly
	 */
	public static void printArrayList(List arr) {
		for (Object o: arr) {
			System.out.println(o);
		}
	}

	public static HashMap<String, Integer> termTermId = new HashMap<String, Integer>();
	public static Integer presentTermId = 0;

	public static HashMap<String, Integer> constructTermTermIDMapping(){
		ArrayList<String> fContents = contentsOfTheFileAsList("termIds");
		termTermId = new HashMap<String, Integer>();
		for(String words: fContents) {
			String[] map = words.split(" ");
			termTermId.put(map[0], Integer.parseInt(map[1]));
		}
		return termTermId;
	}

	public static int getPresentTermID(){
		ArrayList<String> terms = contentsOfTheFileAsList("termids");
		int lastId = terms.size()-1;
		return lastId;
	}


	public static HashMap<String, ArrayList<String>> constructDocTermMap(){
		ArrayList<String> fC = contentsOfTheFileAsList("termdocIds");
		HashMap<String, ArrayList<String>> mapTermDocidTf = new HashMap<String, ArrayList<String>>();
		for(String fs: fC) {
			String[] s = fs.split(":");
			String[] dts = s[1].split("\\|");
			ArrayList<String> dtsL = new ArrayList<String>();
			for(int i=0; i<dts.length; i++) {
				dtsL.add(dts[i]);
			}
			mapTermDocidTf.put(s[0], dtsL);
		}
		return mapTermDocidTf;
	}

	public static void constructTermDocIds(String docId, 
			HashMap<String, Integer> tTotid, 
			HashMap<String, Integer> tfs){

		HashMap<String, ArrayList<String>> termDocs = constructDocTermMap();
		Set<String> wordsToAdd = tfs.keySet();

		for(String word: wordsToAdd) {
			String termId = tTotid.get(word).toString();
			if(termDocs.containsKey(termId)) {
				ArrayList<String> docsAndFreqs = termDocs.get(termId);
				docsAndFreqs.add(docId+" "+tfs.get(word));
				termDocs.put(termId, docsAndFreqs);
			} else {
				ArrayList<String> docsAndFreqs = new ArrayList<String>();
				docsAndFreqs.add(docId+" "+tfs.get(word));
				termDocs.put(termId, docsAndFreqs);
			}						
		}
		Set<String> termsToInsert = termDocs.keySet();
		HashMap<String, String> termIdsMappingToPrint = 
				new HashMap<String, String>();
		writeToFileNew("termdocIds", "");
		for(String term: termsToInsert) { 
			ArrayList<String> tfsNdocIds = termDocs.get(term);
			String content = "";
			for(String tf : tfsNdocIds) {
				content = content+tf+"|";
			}
			termIdsMappingToPrint.put(term, content);
		}
		
		System.out.println(termIdsMappingToPrint);
		printHashMapToFile("termdocIds", termIdsMappingToPrint);
	}
	
	public static ArrayList<Integer> extractDocId(String url) {
		ArrayList<String> docIdsName= contentsOfTheFileAsList(url);
		ArrayList<Integer> docids = new ArrayList<Integer>();
		for(String din : docIdsName) {
			String[] map = din.split(" ");
			docids.add(Integer.parseInt(map[0]));
		}
		return docids;
	}
	
	public static void printHashMapToFile(String url, HashMap<String, String> map){
		Set<String> termIds = map.keySet();
		for(String term: termIds) {
			writeToFileAlreadyExisting(url, term+":"+map.get(term));
		}
	}

}
