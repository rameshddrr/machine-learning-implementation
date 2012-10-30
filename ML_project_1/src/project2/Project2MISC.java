package project2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import fileutils.FileUtils;

import project1.ProjUtils;

public class Project2MISC {

	public static String file = "result_1.0.txt";
	public static void main(String[] args) {

		FileUtils.writeToFileNew(file, "");
		//linearRegression();
		logisticRegression();
	}

	public static double LEARNINGRATE = 0.1;

	public static ArrayList<Double> WEIGHTVECTORLINEARBATCHREG = 
			new ArrayList<Double>();

	public static ArrayList<Double> WEIGHTVECTORLINEARSTOCHASTICREG = 
			new ArrayList<Double>();

	public static ArrayList<Double> WEIGHTVECTORLOGISTICBATCHREG = 
			new ArrayList<Double>();

	public static ArrayList<Double> WEIGHTVECTORLOGISTICSTOCHASTICREG = 
			new ArrayList<Double>();

	public static void logisticRegression(){
		setInitialWeightVector();
		HashMap<Integer, ArrayList<Double>> map = getZScoredMap();

		HashMap<Integer, ArrayList<Double>> testingData = ProjUtils.getFold(map, 0);
		HashMap<Integer, ArrayList<Double>> trainingData = ProjUtils.getFoldOtherThan(map, testingData);

		//iterateLogisticRegressorBatchGradient(trainingData, testingData);
		iterateLogisticRegressorStochasticGradient(trainingData, testingData);
	}

	public static void iterateLogisticRegressorStochasticGradient(
			HashMap<Integer, ArrayList<Double>> trainingData,
			HashMap<Integer, ArrayList<Double>> testingData
			){
		Set<Integer> keys = trainingData.keySet();
		ArrayList<Integer> keysL = new ArrayList<Integer>(keys);

		Collections.shuffle(keysL);

		for(Integer key: keysL) {
			trainingData.get(key).add(0, 1.0);
		}

		int iter = 100;
		int k = 0;
		while(iter != 0) {
			iter--;
			//System.out.println(iter+" : BEFORE : " + WEIGHTVECTORLOGISTICSTOCHASTICREG);	
			for(int j = 0; j < keysL.size() ; j++) {

				Integer key = keysL.get(j);
				ArrayList<Double> trainingRow = trainingData.get(key);
				double h_w_x = sigmoidFunction(
						WEIGHTVECTORLOGISTICSTOCHASTICREG, 
						trainingRow);

				double y = trainingRow.get(58);	
				updateAllWeightsStochasticLogisticRegression(h_w_x, y, trainingRow);

			}

			//System.out.println(iter+" : AFTER : " + WEIGHTVECTORLOGISTICSTOCHASTICREG);

			double rms_e = 0.0;
			for(Integer key: keysL) {
				ArrayList<Double> trainingRow = trainingData.get(key);
				double h_w_x = sigmoidFunction(
						WEIGHTVECTORLOGISTICSTOCHASTICREG, 
						trainingRow);
				rms_e += Math.pow(h_w_x - trainingRow.get(58), 2.0);
			}
			//System.out.println(Math.pow(rms_e/4140.0, 0.5));
			rms_e = Math.pow(rms_e/4140.0, 0.5);
			System.out.println(k +"\t"+rms_e);
			FileUtils.writeToFileAlreadyExisting(file, k++ +"\t"+rms_e);
		}
		printERRORRATELOGISTIC(WEIGHTVECTORLOGISTICSTOCHASTICREG, testingData);
	}

	public static void iterateLogisticRegressorBatchGradient(
			HashMap<Integer, ArrayList<Double>> trainingData,
			HashMap<Integer, ArrayList<Double>> testingData) {

		Set<Integer> keys = trainingData.keySet();
		ArrayList<Integer> keysL = new ArrayList<Integer>(keys);
		Collections.shuffle(keysL);

		for(Integer key: keysL) {
			trainingData.get(key).add(0, 1.0);
		}

		int iter = 500;
		int k = 0;
		while(iter != 0) {
			iter--;
			ArrayList<Double> h_w_xs = new ArrayList<Double>();
			ArrayList<Double> ys = new ArrayList<Double>();
			ArrayList<ArrayList<Double>> xs = new ArrayList<ArrayList<Double>>();

			for(Integer key: keysL) {
				ArrayList<Double> trainingRow = trainingData.get(key);
				double h_w_x = sigmoidFunction(
						WEIGHTVECTORLOGISTICBATCHREG, 
						trainingRow);

				double y = trainingRow.get(58);

				h_w_xs.add(h_w_x);

				ys.add(y);
				xs.add(trainingRow);
			}
			//System.out.println(iter+" : BEFORE : " + WEIGHTVECTORLOGISTICBATCHREG);
			updateAllWeightsBatchLogisticRegression(h_w_xs, ys, xs);
			//System.out.println(iter+" : AFTER : " + WEIGHTVECTORLOGISTICBATCHREG);

			double rms_e = 0.0;
			for(Integer key: keysL) {
				ArrayList<Double> trainingRow = trainingData.get(key);
				double h_w_x = sigmoidFunction(
						WEIGHTVECTORLOGISTICBATCHREG, 
						trainingRow);
				rms_e += Math.pow(h_w_x - trainingRow.get(58), 2.0);
			}
			//System.out.println(Math.pow(rms_e/4140.0, 0.5));
			rms_e = Math.pow(rms_e/4140.0, 0.5);
			System.out.println(k +"\t"+rms_e);
			FileUtils.writeToFileAlreadyExisting(file, k++ +"\t"+rms_e);
		}
		printERRORRATELOGISTIC(WEIGHTVECTORLOGISTICBATCHREG, testingData);
	}

	public static void printERRORRATELOGISTIC(ArrayList<Double> w,
			HashMap<Integer, ArrayList<Double>> testingData) {
		Set<Integer> keysTest = testingData.keySet();
		int CORRECT = 0;
		int WRONG = 0;
		for(Integer key: keysTest) {
			ArrayList<Double> row = testingData.get(key);
			double dp = sigmoidFunction(w, row);
			//System.out.println(dp+" : "+row.get(57));
			if((dp < 0.9 && row.get(57)<=0.0) ||
					(dp>=0.9 && row.get(57)>1.0)) {
				CORRECT++;
			}  else {
				System.out.println(dp+" : "+row.get(57));
				WRONG++;
			}
		}
		System.out.println("ERROR RATE = "+((double)CORRECT/((double)CORRECT+(double)WRONG)));
	}
	
	public static void linearRegression() {
		setInitialWeightVector();
		HashMap<Integer, ArrayList<Double>> map = getZScoredMap();

		HashMap<Integer, ArrayList<Double>> testingData = ProjUtils.getFold(map, 0);
		HashMap<Integer, ArrayList<Double>> trainingData = ProjUtils.getFoldOtherThan(map, testingData);

		//iterateLinearRegressorBatchGradient(trainingData, testingData);
		iterateLinearRegressorStochasticGradient(trainingData, testingData);
	}

	public static void iterateLinearRegressorBatchGradient(
			HashMap<Integer, ArrayList<Double>> trainingData,
			HashMap<Integer, ArrayList<Double>> testingData) {

		HashMap<Integer, ArrayList<Double>> s = 
				new HashMap<Integer, ArrayList<Double>>(trainingData);
		Set<Integer> keys = trainingData.keySet();
		ArrayList<Integer> keysL = new ArrayList<Integer>(keys);
		//Collections.shuffle(keysL);

		for(Integer key: keysL) {
			trainingData.get(key).add(0, 1.0);
		}

		int iter = 500;
		int k = 0;
		while(iter != 0) {
			iter--;
			ArrayList<Double> h_w_xs = new ArrayList<Double>();
			ArrayList<Double> ys = new ArrayList<Double>();
			ArrayList<ArrayList<Double>> xs = new ArrayList<ArrayList<Double>>();

			for(Integer key: keysL) {
				ArrayList<Double> trainingRow = trainingData.get(key);
				double h_w_x = dotProduct(
						WEIGHTVECTORLINEARBATCHREG, 
						trainingRow);

				double y = trainingRow.get(58);

				h_w_xs.add(h_w_x);

				ys.add(y);
				xs.add(trainingRow);
			}
			//System.out.println(iter+" : BEFORE : " + WEIGHTVECTORLINEARBATCHREG);
			updateAllWeightsBatchLinearRegression(h_w_xs, ys, xs);
			//System.out.println(iter+" : AFTER : " + WEIGHTVECTORLINEARBATCHREG);

			double rms_e = 0.0;
			for(Integer key: keysL) {
				ArrayList<Double> trainingRow = trainingData.get(key);
				double h_w_x = dotProduct(
						WEIGHTVECTORLINEARBATCHREG, 
						trainingRow);
				rms_e += Math.pow(h_w_x - trainingRow.get(58), 2.0);
			}
			rms_e = Math.pow(rms_e/4140.0, 0.5);
			System.out.println(k +"\t"+rms_e);
			//System.out.println(k + " : "+rms_e+" : " + printERRORRATELINEAR(WEIGHTVECTORLINEARBATCHREG, testingData));
			FileUtils.writeToFileAlreadyExisting(file, k++ +"\t"+rms_e);
		}
		printERRORRATELINEAR(WEIGHTVECTORLINEARBATCHREG, testingData);
		//printERRORRATELINEAR(WEIGHTVECTORLINEARBATCHREG, s);
	}

	public static String printERRORRATELINEAR(ArrayList<Double> w,
			HashMap<Integer, ArrayList<Double>> testingData) {
		Set<Integer> keysTest = testingData.keySet();
		int CORRECT = 0;
		int WRONG = 0;
		for(Integer key: keysTest) {
			ArrayList<Double> row = testingData.get(key);
			double dp = dotProduct(w, row);
			System.out.println(dp+" : "+row.get(57));
			if((dp<=0.0 && row.get(57)<=0.0) ||
					(dp>=0.0 && row.get(57)> 0.0)) {
				CORRECT++;
			}  else {
				//System.out.println(dp+" : "+row.get(58));
				WRONG++;
			}
		}
		System.out.println("ERROR RATE = "+((double)CORRECT/((double)CORRECT+(double)WRONG)));
		return "ERROR RATE = "+((double)CORRECT/((double)CORRECT+(double)WRONG));
	}




	public static void updateAllWeightsBatchLinearRegression(
			ArrayList<Double> h_w_xs,
			ArrayList<Double> ys,
			ArrayList<ArrayList<Double>> xs){

		ArrayList<Double> newWeightVector = new ArrayList<Double>(WEIGHTVECTORLINEARBATCHREG);
		for(int j =0; j < WEIGHTVECTORLINEARBATCHREG.size(); j++) {
			double innerSum = 0.0;
			for(int i=0; i < xs.size()-1; i++) {
				innerSum += (h_w_xs.get(i)-ys.get(i))*xs.get(i).get(j);
			}
			innerSum = (innerSum * LEARNINGRATE)/xs.size();
			newWeightVector.set(j, (WEIGHTVECTORLINEARBATCHREG.get(j)-innerSum));
		}

		WEIGHTVECTORLINEARBATCHREG = newWeightVector;
	}

	public static void iterateLinearRegressorStochasticGradient(
			HashMap<Integer, ArrayList<Double>> trainingData,
			HashMap<Integer, ArrayList<Double>> testingData
			){
		Set<Integer> keys = trainingData.keySet();
		ArrayList<Integer> keysL = new ArrayList<Integer>(keys);

		Collections.shuffle(keysL);
		Collections.shuffle(keysL);
		Collections.shuffle(keysL);
		Collections.shuffle(keysL);
		Collections.shuffle(keysL);
		Collections.shuffle(keysL);

		for(Integer key: keysL) {
			trainingData.get(key).add(0, 1.0);
		}

		double prev = 0.0;
		int iter = 1000;
		int k =0;
		while(iter != 0) {
			iter--;

			for(int j = 0; j < keysL.size() ; j++) {

				Integer key = keysL.get(j);
				ArrayList<Double> trainingRow = trainingData.get(key);
				double h_w_x = dotProduct(
						WEIGHTVECTORLINEARSTOCHASTICREG, 
						trainingRow);

				double y = trainingRow.get(58);				
				updateAllWeightsStochasticLinearRegression(h_w_x, y, trainingRow);
			}						

			double rms_e = 0.0;
			for(Integer key: keysL) {
				ArrayList<Double> trainingRow = trainingData.get(key);
				double h_w_x = dotProduct(
						WEIGHTVECTORLINEARSTOCHASTICREG, 
						trainingRow);
				rms_e += Math.pow(h_w_x - trainingRow.get(58), 2);
			}

			rms_e = Math.pow(rms_e/4140.0, 0.5);

			prev = rms_e;
			System.out.println(k +"\t"+rms_e);
			FileUtils.writeToFileAlreadyExisting(file, k++ +"\t"+rms_e);
		}
		printERRORRATELINEAR(WEIGHTVECTORLINEARSTOCHASTICREG, testingData);
	}

	public static void updateAllWeightsStochasticLogisticRegression(
			Double predicted,
			Double actual,
			ArrayList<Double> xs){

		ArrayList<Double> newWeightVector = 
				new ArrayList<Double>(WEIGHTVECTORLOGISTICSTOCHASTICREG);

		for(int i=0; i < WEIGHTVECTORLOGISTICSTOCHASTICREG.size(); i++) {
			double w = WEIGHTVECTORLOGISTICSTOCHASTICREG.get(i);
			double x_i_j = xs.get(i);

			w = w - (LEARNINGRATE * (predicted - actual) * x_i_j);
			newWeightVector.set(i, w);
		}
		WEIGHTVECTORLOGISTICSTOCHASTICREG= newWeightVector;

		//System.out.println(WEIGHTVECTORLINEARSTOCHASTICREG);
	}

	public static void updateAllWeightsBatchLogisticRegression(
			ArrayList<Double> h_w_xs,
			ArrayList<Double> ys,
			ArrayList<ArrayList<Double>> xs){

		ArrayList<Double> newWeightVector = new ArrayList<Double>(WEIGHTVECTORLOGISTICBATCHREG);
		for(int j =0; j < WEIGHTVECTORLOGISTICBATCHREG.size(); j++) {
			double innerSum = 0.0;
			for(int i=0; i < xs.size()-1; i++) {
				innerSum += (h_w_xs.get(i)-ys.get(i))*xs.get(i).get(j);
			}
			innerSum = (innerSum * LEARNINGRATE)/xs.size();
			newWeightVector.set(j, (WEIGHTVECTORLOGISTICBATCHREG.get(j)-innerSum));
		}

		WEIGHTVECTORLOGISTICBATCHREG = newWeightVector;
	}

	public static void updateAllWeightsStochasticLinearRegression(
			Double predicted,
			Double actual,
			ArrayList<Double> xs){

		ArrayList<Double> newWeightVector = 
				new ArrayList<Double>(WEIGHTVECTORLINEARSTOCHASTICREG);

		for(int i=0; i < WEIGHTVECTORLINEARSTOCHASTICREG.size(); i++) {
			double w = WEIGHTVECTORLINEARSTOCHASTICREG.get(i);
			double x_i_j = xs.get(i);

			w = w - (LEARNINGRATE * (predicted - actual) * x_i_j);
			newWeightVector.set(i, w);
		}
		WEIGHTVECTORLINEARSTOCHASTICREG= newWeightVector;

		//System.out.println(WEIGHTVECTORLINEARSTOCHASTICREG);
	}

	public static HashMap<Integer, ArrayList<Double>> getZScoredMap(){
		HashMap<Integer, ArrayList<Double>> map = ProjUtils.parse2("spambase.data");



		ArrayList<Double> means = ProjUtils.returnAllMeansAsList(map,  58, 58);
		ArrayList<Double> sds = ProjUtils.getAllStandardDeviations(map, means, 58);		
		return ProjUtils.returnZScoredMap(map, means, sds, 58);
	}

	public static double dotProduct(ArrayList<Double> w,
			ArrayList<Double> x){
		double res = 0.0;
		for(int i=0; i<w.size(); i++) {
			res = res + (w.get(i)*x.get(i));
		}
		return res;
	}

	public static double sigmoidFunction(
			ArrayList<Double> w,
			ArrayList<Double> x
			) {
		double res = 0.0;

		double dotProd = dotProduct(w, x);
		dotProd = dotProd * -1.0 ;

		res = 1.0/(1.0 + Math.exp(dotProd));

		return res;
	}

	public static void setInitialWeightVector() {
		WEIGHTVECTORLINEARBATCHREG =  ProjUtils.getInitializedDoubleAttributeList(58, 0.0);
		WEIGHTVECTORLINEARSTOCHASTICREG =  ProjUtils.getInitializedDoubleAttributeList(58, 0.0);
		WEIGHTVECTORLOGISTICBATCHREG =  ProjUtils.getInitializedDoubleAttributeList(58, 0.0);
		WEIGHTVECTORLOGISTICSTOCHASTICREG =  ProjUtils.getInitializedDoubleAttributeList(58, 0.0);
	}

}
