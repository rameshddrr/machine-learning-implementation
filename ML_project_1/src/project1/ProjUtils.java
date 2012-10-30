package project1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import fileutils.FileUtils;

public class ProjUtils {


	public static HashMap<Integer, String> parse(String url) {
		ArrayList<String> lines = FileUtils.contentsOfTheFileAsList(url);
		HashMap<Integer, String> idToValues = new HashMap<Integer, String>();
		int i=0;
		for(String line: lines) {
			idToValues.put(i, line);
			i++;
		}
		return idToValues;
	}

	public static HashMap<Integer, ArrayList<Double>> parse2(String url) {
		ArrayList<String> lines = FileUtils.contentsOfTheFileAsList(url);
		return convert(lines);
	}
	
	public static HashMap<Integer, ArrayList<Double>> getFold(
			HashMap<Integer, ArrayList<Double>> map, 
			int k) {

		HashMap<Integer, ArrayList<Double>> fold =
				new HashMap<Integer, ArrayList<Double>>();
		
		for(int i=k; i < map.size(); i=i+10) {
				fold.put(i, map.get(i));
		}		
		return fold;
	}
	
	public static HashMap<Integer, ArrayList<Double>> getFoldOtherThan(
			HashMap<Integer, ArrayList<Double>> map,
			HashMap<Integer, ArrayList<Double>> toExc) {

		HashMap<Integer, ArrayList<Double>> fold =
				new HashMap<Integer, ArrayList<Double>>();
		
		Set<Integer> s = toExc.keySet();
		Set<Integer> in = map.keySet();
		for(Integer i: in) {
			if(!s.contains(i))
				fold.put(i, map.get(i));
			
		}		
		return fold;
	}
	
	public static ArrayList<Double> getInitializedDoubleAttributeList(
			int numberOfAttributes,
			double initVal){
		ArrayList<Double> arr = new ArrayList<Double>();
		for(int i=0; i < numberOfAttributes; i++) {
			arr.add(i, initVal);
		}
		return arr;
	}
	
	public static ArrayList<Double> getAllStandardDeviations(
			HashMap<Integer, ArrayList<Double>> contents,
			ArrayList<Double> means,
			int numberOfAttributes) {
		Set<Integer> keys = contents.keySet();
		int N = contents.size();
		ArrayList<Double> sds = getInitializedDoubleAttributeList(
				numberOfAttributes, 0.0);
		for(Integer key: keys) {
			ArrayList<Double> row = contents.get(key);
			for(int i=0; i<numberOfAttributes; i++) {
				double prev = sds.get(i);
				double now = Math.pow(row.get(i)-means.get(i), 2.0);
				sds.set(i, prev+now);
			}
		}
		for(int i=0; i<numberOfAttributes; i++) {
			double sd = sds.get(i);
			if(sd == 0.0) {
				sds.set(i,  Math.pow(1/(double)N, 0.5));
			} else {
				sds.set(i, Math.pow(sd/(double)N, 0.5));
			}
		}
		//System.out.println(sds);
		return sds;
	}

	public static HashMap<Integer, ArrayList<Double>> returnZScoredMap(
			HashMap<Integer, ArrayList<Double>> map,
			ArrayList<Double> means,
			ArrayList<Double> sds) {
		
		HashMap<Integer, ArrayList<Double>> zscore = 
				new HashMap<Integer, ArrayList<Double>>();
		Set<Integer> keys = map.keySet();
		
		for(Integer key: keys) {
			ArrayList<Double> xs = map.get(key);
			ArrayList<Double> zs = new ArrayList<Double>();
			int xSize = xs.size();
			for(int i=0; i < xSize-1; i++) {
				double z = (xs.get(i)- means.get(i))/sds.get(i);
				zs.add(z);
			}
			zs.add(xs.get(xSize-1));
			zscore.put(key, zs);
		}
		
		return zscore;
	}
	
	public static HashMap<Integer, ArrayList<Double>> returnZScoredMap(
			HashMap<Integer, ArrayList<Double>> map,
			ArrayList<Double> means,
			ArrayList<Double> sds,
			int size) {
		
		HashMap<Integer, ArrayList<Double>> zscore = 
				new HashMap<Integer, ArrayList<Double>>();
		Set<Integer> keys = map.keySet();
		
		for(Integer key: keys) {
			ArrayList<Double> xs = map.get(key);
			ArrayList<Double> zs = new ArrayList<Double>();
			int xSize = xs.size();
			for(int i=0; i < size; i++) {
				double z = (xs.get(i)- means.get(i))/sds.get(i);
				zs.add(z);
			}
			
			zscore.put(key, zs);
		}
		
		return zscore;
	}
	
	public static HashMap<Integer, ArrayList<Double>> convert(
			ArrayList<String> list) {
		HashMap<Integer, ArrayList<Double>> map = new HashMap<Integer, ArrayList<Double>>();
		int id=0;
		for(String a: list) {
			String[] numbers = a.split("\\,");
			ArrayList<Double> vals = new ArrayList<Double>();
			for(int i=0; i < numbers.length; i++) {
				numbers[i] = numbers[i].replaceAll("\\,", "");
				double d = Double.parseDouble(numbers[i]);
				vals.add(d);
			}
			map.put(id, vals);
			id++;
		}
		return map;
	}

	public static HashMap<Integer, ArrayList<String>> parseFolds(String url){
		return splitByKFolds(11, FileUtils.contentsOfTheFileAsList(url));
	}

	public static HashMap<Integer, ArrayList<String>> splitByKFolds(int k, ArrayList<String> data) {
		HashMap<Integer, ArrayList<String>> kFolds = new HashMap<Integer, ArrayList<String>>();
		int pres = 1;
		for(String line: data) {
			if(kFolds.containsKey(pres)) {
				ArrayList<String> lines = kFolds.get(pres);
				lines.add(line);
				kFolds.put(pres, lines);
				pres++;
			} else {
				ArrayList<String> lines = new ArrayList<String>();
				lines.add(line);
				kFolds.put(pres, lines);
				pres++;
			}
			if(pres == k) {
				pres = 1;
			}			
		}
		return kFolds;
	}

	public static double[] returnAllMeans(ArrayList<String> values) {

		double[] sums = getInitializedDoubleArray(57,0.0);
		for(String row: values) {
			String[] numbersString = row.split("\\,");
			for(int i=0; i < numbersString.length-1; i++) {
				numbersString[i] = numbersString[i].replaceAll("\\,", "");
				sums[i] += Double.parseDouble(numbersString[i]);
			}
		}

		return returnMeans(sums, values.size());
	}

	public static ArrayList<Double> returnAllMeansAsList(
			HashMap<Integer, ArrayList<Double>> map) {
		ArrayList<Double> sums = Project1Main.getInitializedDoubleAttributeList(0.0);
		Set<Integer> keys = map.keySet();
		for(Integer key: keys) {
			ArrayList<Double> list = map.get(key);
			for(int i=0; i < Project1Main.numberOfAttributes; i++) {
				double val = sums.get(i);
				val += list.get(i);
				sums.set(i, val);
			}
		}
		
		ArrayList<Double> mean = Project1Main.getInitializedDoubleAttributeList(0.0);
		int N = map.size();
		for(int i=0; i<sums.size(); i++) {
			mean.set(i, sums.get(i)/(double)N);
		}
		return mean;
	}

	public static ArrayList<Double> returnAllMeansAsList(
			HashMap<Integer, ArrayList<Double>> map, int size, int numberOfAttr) {
		ArrayList<Double> sums = Project1Main.getInitializedDoubleAttributeList(size, 0.0);
		Set<Integer> keys = map.keySet();
		for(Integer key: keys) {
			ArrayList<Double> list = map.get(key);
			for(int i=0; i < numberOfAttr; i++) {
				double val = sums.get(i);
				val += list.get(i);
				sums.set(i, val);
			}
		}
		
		ArrayList<Double> mean = Project1Main.getInitializedDoubleAttributeList(size, 0.0);
		int N = map.size();
		for(int i=0; i<sums.size(); i++) {
			mean.set(i, sums.get(i)/(double)N);
		}
		return mean;
	}
	
	public static double[] getInitializedDoubleArray(int size, double value){
		double[] arr = new double[size];
		for(int i=0; i < arr.length; i++) {
			arr[i] = 0.0;
		}
		return arr;
	}

	public static int[] getInitializedIntArray(int size, int value){
		int[] arr = new int[size];
		for(int i=0; i < arr.length; i++) {
			arr[i] = 0;
		}
		return arr;
	}

	public static double[] returnMeans(double[] arr, int size){
		double[] means = new double[arr.length];
		for(int i=0; i < arr.length; i++) {
			means[i] = arr[i]/size;
		}
		return means;
	}

	public static ArrayList<ArrayList<String>> returnAllPossibleCombos(HashMap<Integer, 
			ArrayList<String>> map){
		Set<Integer> keys = map.keySet();		
		ArrayList<ArrayList<String>> combos = new ArrayList<ArrayList<String>>();
		for(Integer toLeave: keys) {
			Set<Integer> toConsider =  map.keySet();
			toConsider.remove(toLeave);
			ArrayList<String> nowStrings = new ArrayList<String>();
			for(Integer i: toConsider) {
				nowStrings.addAll(map.get(i));
			}
			combos.add(nowStrings);
		}
		return combos;
	}
	
	public static void printHashMap(HashMap map) {
		Set s = map.keySet();
		for(Object o: s) {
			System.out.println(o +" --> "+map.get(o));
		}
		System.out.println(map.size());
	}
	/*ArrayList<Double> sortedVals = new ArrayList<Double>();
	public static HashMap<Integer, Double> returnSortedSpamIdMap(
			HashMap<Integer, Double> ratios,
			HashMap<Integer, Double> idSpam) {
		Set<Integer> keys = ratios.keySet();
		ArrayList<Double> vals = new ArrayList<Double>();
		
		for(Integer key: keys) {
			vals.add(ratios.get(key));
		}
		
	}*/
}
