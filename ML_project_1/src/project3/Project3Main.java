package project3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import project1.ProjUtils;

public class Project3Main {


	public static void main(String[] args) {

		/*HashMap<Integer, ArrayList<Double>> ROWS = ProjUtils.parse2("spambase.data");
		HashMap<Integer, ArrayList<Double>> COLS = 
				Project3Utils.getFeaturesFromRows(ROWS);*/


		HashMap<Integer, ArrayList<Double>> ROWS = ProjUtils.parse2("test_boost");
		System.out.println(ROWS);
		HashMap<Integer, ArrayList<Double>> COLS = 
				Project3Utils.getFeaturesFromRows(ROWS);
		System.out.println(COLS);		
		System.out.println(Project3Utils.getThresholdForColumn(COLS.get(0)));

		boosting(ROWS, COLS);
	}

	public static void boosting(HashMap<Integer, ArrayList<Double>> ROWS,
			HashMap<Integer, ArrayList<Double>> COLS){
		ArrayList<Double> weightsInitial= ProjUtils.getInitializedDoubleAttributeList(ROWS.size(), 1.0/ROWS.size());

		for(int i=0; i < 4 ;i++) {
			weightsInitial = ADABOOSTING(COLS, ROWS, COLS.get(0), COLS.get(1), 
					Project3Utils.getThresholdForColumn(COLS.get(0)), 
					weightsInitial, 
					1, 
					-1, 
					0) ;
					System.out.println("--->" + weightsInitial);
							
		}
		System.out.println("THRESHOLDS : "+thresholdsForPredictions);
		System.out.println("ALPHAS : " + alphasForPrediction);
	}

	public static double getBesthypothesis(
			ArrayList<Double> errorRates,
			ArrayList<Double> thresholds) {
		double min = Collections.min(errorRates);
		return thresholds.get(errorRates.indexOf(min));
	}

	public static ArrayList<Double> alphasForPrediction = new ArrayList<Double>();
	public static ArrayList<Double> thresholdsForPredictions = new ArrayList<Double>();
	
	public static ArrayList<Double> ADABOOSTING(
			HashMap<Integer, ArrayList<Double>> COLS,
			HashMap<Integer, ArrayList<Double>> ROWS,
			ArrayList<Double> x,
			ArrayList<Double> y,
			ArrayList<Double> thresholds,
			ArrayList<Double> w,
			double opLessThanThresholds,
			double opGreaterThanThresholds,
			int column) {

		ArrayList<Double> newPis = new ArrayList<Double>();
		setTheBestThresholdAndErrorRate(COLS, 
				thresholds, 
				x, 
				y, 
				w, 
				opLessThanThresholds, 
				opGreaterThanThresholds, 
				column);
		double alpha_t = getAlpha(errorRate);
		double qi_correct = DtNegativePower(alpha_t);
		double qi_incorrect = DtPositivePower(alpha_t);

		int index = 0;
		for(Double pi: w) {
			if(predictions.get(index)) {
				double new_p = pi * qi_correct;
				newPis.add(new_p);
			} else {
				double new_p = pi * qi_incorrect;
				newPis.add(new_p);
			}
			index++;
		}
		thresholdsForPredictions.add(thresholdSelected);
		alphasForPrediction.add(alpha_t);
		
		System.out.println(newPis);
		return normalize(newPis);
	}

	public static double thresholdSelected = 0.0;
	public static double errorRate = 0.0;
	public static ArrayList<Boolean> predictions = new ArrayList<Boolean>();

	public static ArrayList<Double> normalize(ArrayList<Double> weights) {
		double sum = 0.0;
		for(Double w: weights) {
			sum = sum + w;
		}
		ArrayList<Double> normalizedWeights = 
				new ArrayList<Double>();
		for(Double w: weights) {
			normalizedWeights.add(w/sum);
		}
		return normalizedWeights;
	}

	public static void setTheBestThresholdAndErrorRate(
			HashMap<Integer, ArrayList<Double>> map,			
			ArrayList<Double> thresholds, 
			ArrayList<Double> xs, 
			ArrayList<Double> ys,
			ArrayList<Double> probs,
			double opLessThanThresholds,
			double opGreaterThanThresholds,
			Integer column) {
		ArrayList<Double> errorRates = calculateErrorRatesForAllHypothesisInOrder(map, thresholds, 
				xs, ys, probs, 
				opLessThanThresholds, opGreaterThanThresholds, column);


		errorRate = getBestErrorRate(errorRates);
		thresholdSelected = thresholds.get(errorRates.indexOf(errorRate));		
		predictions = weakLearnerData(map, thresholdSelected, opLessThanThresholds, 
				opGreaterThanThresholds, column);
	}

	public static double getBestErrorRate(ArrayList<Double> errorRates) {
		ArrayList<Double> diffs = new ArrayList<Double>();
		for(Double e: errorRates) {
			diffs.add(Math.abs(0.5 - e));
		}
		double maxdiff = Collections.max(diffs);
		return errorRates.get(diffs.indexOf(maxdiff));
	}
	
	public static ArrayList<Double> calculateErrorRatesForAllHypothesisInOrder(
			HashMap<Integer, ArrayList<Double>> map,			
			ArrayList<Double> thresholds, 
			ArrayList<Double> xs, 
			ArrayList<Double> ys,
			ArrayList<Double> probs,
			double opLessThanThresholds,
			double opGreaterThanThresholds,
			Integer column) {
		ArrayList<Double> errorRates = new ArrayList<Double>();

		for(int i=0; i < thresholds.size(); i++) {
			ArrayList<Boolean> preds = 
					weakLearnerData(map, thresholds.get(i), opLessThanThresholds, opGreaterThanThresholds, column);
			errorRates.add(getErrorRate(probs, preds));
		}

		return errorRates;
	}	

	public static ArrayList<Boolean> weakLearnerData(
			HashMap<Integer, ArrayList<Double>> map,
			double threshold,
			double opLessThanThresholds,
			double opGreaterThanThresholds,
			Integer column) {
		Integer size = map.size();
		ArrayList<Double> xs = map.get(column);
		ArrayList<Double> op = map.get(size-1);
		ArrayList<Boolean> preds = new ArrayList<Boolean>();
		for(int i=0; i < xs.size(); i++) {
			if( (xs.get(i) <= threshold && op.get(i) == opLessThanThresholds) || 
					(xs.get(i) > threshold && op.get(i) == opGreaterThanThresholds)) {
				preds.add(true);
			} else {
				preds.add(false);
			}
		}

		return preds;
	}

	public static double getErrorRate(
			ArrayList<Double> weights, 
			ArrayList<Boolean> preds) {
		double errorRate = 0.0;
		for (int i=0; i < preds.size(); i++) {
			if (preds.get(i)==false) {
				errorRate = errorRate + weights.get(i);
			}
		}
		return errorRate;				
	}

	public static double getAlpha(double errorRate) {
		return Math.abs(0.5*Math.log((1.0-errorRate)/errorRate));
	}

	public static double DtPositivePower(double alpha) {
		return Math.exp(alpha);
	}

	public static double DtNegativePower(double alpha) {
		alpha = alpha * -1.0;
		return Math.exp(alpha);
	}

}
