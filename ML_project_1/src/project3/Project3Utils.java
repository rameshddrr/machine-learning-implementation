package project3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Project3Utils {

	public static HashMap<Integer, ArrayList<Double>> getThresholds(
			HashMap<Integer, ArrayList<Double>> map) {
		HashMap<Integer, ArrayList<Double>> uniques = getAllUniqueValuesSorted(map);
		HashMap<Integer, ArrayList<Double>> thresholds = new HashMap<Integer, ArrayList<Double>>();
		
		Set<Integer> keys = uniques.keySet();
		
		for(Integer key: keys) {
			thresholds.put(key, getThresholdForColumn(uniques.get(key)));
		}
		
		return thresholds;
	}
	
	public static ArrayList<Double> getThresholdForColumn(ArrayList<Double> col) {
		ArrayList<Double> thres = new ArrayList<Double>();
		for(int i=0; i < col.size()-1; i++) {
			double mean = (col.get(i) +col.get(i+1))/2.0;
			thres.add(mean);
		}
		
		return thres;
	}
	
	public static HashMap<Integer, ArrayList<Double>> getAllUniqueValuesSorted(
			HashMap<Integer, ArrayList<Double>> map) {
		HashMap<Integer, ArrayList<Double>> cols = 
				getFeaturesFromRows(map);
		
		HashMap<Integer, ArrayList<Double>> uniques =
				new HashMap<Integer, ArrayList<Double>>();
		
		Set<Integer> keys = cols.keySet();
		
		for(Integer key: keys) {
			ArrayList<Double> feature = cols.get(key);
			HashSet<Double> featureDoubles = new HashSet<Double>(feature);
			ArrayList<Double> featuresSorted = new ArrayList<Double>(featureDoubles);
			Collections.sort(featuresSorted);
			uniques.put(key, featuresSorted);
		}
		return uniques;
	}
	
	public static HashMap<Integer, ArrayList<Double>> getFeaturesFromRows(
			HashMap<Integer, ArrayList<Double>> map) {
		Set<Integer> keys = map.keySet();
		HashMap<Integer, ArrayList<Double>> res = 
				new HashMap<Integer, ArrayList<Double>>();
		for(Integer key: keys) {
			ArrayList<Double> row = map.get(key);
			for(int i=0; i < row.size(); i++) {
				if(res.get(i)==null){
					ArrayList<Double> col = new ArrayList<Double>();
					col.add(row.get(i));
					res.put(i, col);
				} else {
					ArrayList<Double> col = res.get(i);
					col.add(row.get(i));
					res.put(i, col);
				}
			}
		}
		return res;
	}
}
