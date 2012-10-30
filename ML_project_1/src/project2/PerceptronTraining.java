package project2;

import java.util.ArrayList;

import fileutils.FileUtils;

public class PerceptronTraining {

	public static ArrayList<Double> WEIGHTVECTOR = 
			new ArrayList<Double>();

	public static void main(String[] args) {

		perceptronAlgo();

	}

	public static void perceptronAlgo() {
		setInitialWeightVector();
		ArrayList<ArrayList<Double>> trainingRows = getTrainingSet();

		int iter = 30;
		int n = 0;
		printClassificationStat(trainingRows, WEIGHTVECTOR, ++n);
		int MISTAKE = 1;
		while(MISTAKE != 0) {
			 MISTAKE = 0;
			for(ArrayList<Double> row: trainingRows) {
				
				for(int i=0; i<row.size(); i++){
					double s = row.get(i);
					s = s * row.get(row.size()-1);
					row.set(i, s);
				}
				
				double prod = dotProduct(WEIGHTVECTOR, row);
				double scale =0.0;
				/*if(prod<0.5) {
					prod = 1.0;
				} else {
					prod = -1.0;
				}*/
				if(prod<=0){
					WEIGHTVECTOR = trainWeights(WEIGHTVECTOR, row, 1);
					MISTAKE++;
				}
				//scale = row.get(5) - prod;
				//WEIGHTVECTOR = trainWeights(WEIGHTVECTOR, row, scale);				

			}
			iter--;
			
			System.out.println(n +": MISTAKES : " +MISTAKE+" : WEIGHT VECTOR : "+WEIGHTVECTOR);
			n++;
			//printClassificationStat(trainingRows, WEIGHTVECTOR, n);
		}
		for(double d: WEIGHTVECTOR){
		System.out.println(d/-14.0);
		}
		//verifyValidity(trainingRows, WEIGHTVECTOR);
	}

	public static void verifyValidity(
			ArrayList<ArrayList<Double>> data,
			ArrayList<Double> weight) {
		for(ArrayList<Double> row: data) {
			
			double dp = dotProduct(weight, row);
			String s="WRONG";
			if((dp>0 && row.get(5) == 1.0) ||
					(dp<0 && row.get(5) == -1.0)){
				s="CORRECT";
			}

			System.out.println(dp+" DP <- : -> OP "+row.get(5)+" : "+s);

		}
	}

	public static void printClassificationStat(
			ArrayList<ArrayList<Double>> data, 
			ArrayList<Double> weights,
			int iteration) {
		int END = data.get(0).size();
		int CORRECT = 0;
		int WRONG = 0;
		for(ArrayList<Double> row: data) {
			double d = dotProduct(weights, row);
			if(d<=0.5 && row.get(END-1) == -1.0) {
				CORRECT++;
			} else if (d<=0.5 && row.get(END-1) != -1.0) {
				WRONG++;
			} else if (d>0.5 && row.get(END-1) == 1.0) {
				CORRECT++;
			}  else {
				WRONG++;
			}
		}
		System.out.println("AT ITERATION : "+iteration+" : CORRECT : "+CORRECT +" : WRONG : "+ WRONG);
	}

	public static double LEARNINGRATE = 1.0;

	public static ArrayList<Double> trainWeights(
			ArrayList<Double> w,
			ArrayList<Double> x,
			double scale) {
		ArrayList<Double> newWeights = new ArrayList<Double>();
		//System.out.println(x);
		//newWeights.add(w.get(0));		
		for(int i=0 ; i < w.size(); i++) {
			//System.out.println(w.get(i) +" : "+ x.get(i));
			double nw = w.get(i) + (LEARNINGRATE* x.get(i)*scale);
			newWeights.add(nw);
		}
		return newWeights;
	}

	public static ArrayList<ArrayList<Double>> getTrainingSet(){

		ArrayList<ArrayList<Double>> trainingSet = 
				new ArrayList<ArrayList<Double>>();

		ArrayList<String> lines = 
				FileUtils.contentsOfTheFileAsList("perceptronData.txt");

		for(String line: lines) {
			String[] nums = line.split("\\t");
			ArrayList<Double> row = new ArrayList<Double>();
			row.add(1.0);
			for (int i=0; i < nums.length; i++) {
				double num = Double.parseDouble(nums[i]);
				row.add(num);
			}
			trainingSet.add(row);
		}

		return trainingSet;
	}

	public static double dotProduct(ArrayList<Double> w,
			ArrayList<Double> x){
		double res = 0.0;
		for(int i=0; i<w.size(); i++) {
			res = res + (w.get(i)*x.get(i));
		}
		return res;
	}

	public static void setInitialWeightVector() {

		WEIGHTVECTOR =  new ArrayList<Double>();
		WEIGHTVECTOR.add(0.0);
		WEIGHTVECTOR.add(0.0);
		WEIGHTVECTOR.add(0.0);
		WEIGHTVECTOR.add(0.0);
		WEIGHTVECTOR.add(0.0);

	}
}
