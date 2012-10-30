package project1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class Project1Main {

	public static void main(String[] args) {

		//firstPass();
		//secondPass();
		thirdPass();

	}

	public static void firstPass(){
		HashMap<Integer, ArrayList<String>> data = 
				ProjUtils.parseFolds("spambase.data");		


		//System.out.println("SIZE OF DATA : "+data.size());
		Set<Integer> keys = data.keySet();
		//System.out.println(keys.size());
		int size = 0;
		ArrayList<String> trainingRows = new ArrayList<String>();
		ArrayList<String> testingRows = new ArrayList<String>();
		int k = 9;
		for(Integer key: keys) {
			size += data.get(key).size();


			if(k!=0) {
				trainingRows.addAll(data.get(key));
			} else {
				testingRows.addAll(data.get(key));
			}
			k--;
		}
		//System.out.println(testingRows.size());
		//printList(trainingRows);

		HashMap<Integer, ArrayList<Double>> contents =  
				ProjUtils.convert(trainingRows);

		//System.out.println(contents.keySet().size());

		double[] meansArr = ProjUtils.returnAllMeans(trainingRows);
		ArrayList<Double> means = getArrayListFromArray(meansArr);

		//System.out.println(means);

		double probspam = (double)getSpamRows(contents).size()/contents.size();
		double probnonspam = 1.0 - probspam;

		ArrayList<Double> p_fi_lessorequal_givenspam = probFiLessThanOrEqualMeanGivenSpamWithSmoothing(contents, means);
		ArrayList<Double> p_fi_greater_givenspam = probFiGreaterThanMeanGivenSpamWithSmoothing(contents, means);
		ArrayList<Double> p_fi_lessorequal_givennonspam = probFiLessThanOrEqualMeanGivenNonSpamWithSmoothing(contents, means);
		ArrayList<Double> p_fi_greater_givennonspam = probFiGreaterThanMeanGivenSpamWithSmoothing(contents, means);


		HashMap<Integer, ArrayList<Double>> contentsTestingRows =  
				ProjUtils.convert(testingRows);

		Set<Integer> allTestData = contentsTestingRows.keySet();

		int numberCorrectPred = 0;
		int truePositive = 0;
		int trueNegative = 0;
		int falsePositive = 0;
		int falseNegative = 0;

		int i=0;
		ArrayList<Double> logRatio = new ArrayList<Double>();
		//HashMap<Double,Double> ratioToSpam = new HashMap<Double, Double>(); 
		double thres = 0.01;
		HashMap<Double,ArrayList<Double>> ratioToSpam = new HashMap<Double, ArrayList<Double>>();
		for(Integer t: allTestData) {
			ArrayList<Double> testData = contentsTestingRows.get(t);
			double predict = calculateTheProbabilityRatio(
					p_fi_lessorequal_givenspam, 
					p_fi_greater_givenspam, 
					p_fi_lessorequal_givennonspam, 
					p_fi_greater_givennonspam, 
					means, 
					probspam,
					probnonspam,
					testData);


			//ratioToSpam.put(predict, testData.get(numberOfAttributes));
			if(ratioToSpam.containsKey(predict)) {
				ArrayList<Double> spms = ratioToSpam.get(predict);
				spms.add(testData.get(numberOfAttributes));
				ratioToSpam.put(predict, spms);
			} else {
				ArrayList<Double> spms = new ArrayList<Double>();
				spms.add(testData.get(numberOfAttributes));
				ratioToSpam.put(predict, spms);
			}


			// Predict spam and is spam

			if(predict >= thres && testData.get(numberOfAttributes)==1.0){
				truePositive++;
				// Predict spam and is not spam
			} else if (predict >= thres && testData.get(numberOfAttributes)!=1.0) {
				falsePositive++;
				// Predict not spam and is Spam
			} else if (predict < thres && testData.get(numberOfAttributes)==1.0) {
				falseNegative++;
				// Predict not spam and not spam
			} else {
				trueNegative++;
			}
			//System.out.println(predict+" --> "+testData.get(numberOfAttributes));

			/*if((predict>= 1 && testData.get(numberOfAttributes)==1.0) ||
					(predict< 1 && testData.get(numberOfAttributes)==0.0)) {
				numberCorrectPred++;
				System.out.println(predict+" --> "+testData.get(numberOfAttributes));
			} else {
				System.out.println(predict+" --> "+testData.get(numberOfAttributes));
			}*/
		}

		/*System.out.println("Number of correct predictions = "+(truePositive+trueNegative) +" Out of "+allTestData.size());
		System.out.println("True Positive : "+truePositive);
		System.out.println("False Positive : "+falsePositive);
		System.out.println("True Negative : "+trueNegative);
		System.out.println("False Negative : "+falseNegative);*/

		//System.out.println("**************************************************************************");

		int truePositive1 = 0;
		int trueNegative1 = 0;
		int falsePositive1 = 0;
		int falseNegative1 = 0;

		Set<Double> vals = ratioToSpam.keySet();
		ArrayList<Double> sortedVals = new ArrayList<Double>(vals);
		Collections.sort(sortedVals);
		System.out.println(sortedVals);
		ArrayList<Double> inverseSort = new ArrayList<Double>(sortedVals);
		int F = inverseSort.size();
		for(int m=0 ; m < F ; m++) {
			sortedVals.set(m, inverseSort.get(F-m-1));
		}
		System.out.println(sortedVals);
		ArrayList<Double> xks = new ArrayList<Double>();
		ArrayList<Double> yks = new ArrayList<Double>();
		for(Double val: sortedVals) {
			//double thres = 0.09;
			if(val >= thres && ratioToSpam.get(val).get(0)==1.0){
				//truePositive1++;
				truePositive1 += ratioToSpam.get(val).size();
				//System.out.println("TP");
				// Predict spam and is not spam
			} else if (val >= thres && ratioToSpam.get(val).get(0)!=1.0) {
				//falsePositive1++;
				falsePositive1 += ratioToSpam.get(val).size();
				//System.out.println("FP");
				// Predict not spam and is Spam
			} else if (val < thres && ratioToSpam.get(val).get(0)==1.0) {
				//falseNegative1++;
				falseNegative1 += ratioToSpam.get(val).size();
				//System.out.println("FN");
				// Predict not spam and not spam
			} else {
				//trueNegative1++;
				trueNegative1 += ratioToSpam.get(val).size();
				//System.out.println("TN");
			}
			/*System.out.println(val+" --> "+ratioToSpam.get(val).get(0));
			System.out.print("True Positive : "+truePositive1);
			System.out.print("   False Positive : "+falsePositive1);
			System.out.print("   True Negative : "+trueNegative1);
			System.out.print("    False Negative : "+falseNegative1);
			System.out.print("     TPR(Y-axis) = "+truePositive1/460.0);
			System.out.println("     FPR(X-axis) = "+falsePositive1/460.0);*/

			/*double res = truePositive1/460.0;
			res = res/0.2847826086956522;*/
			//System.out.println(truePositive1/460.0);

			if((falsePositive1+trueNegative1)!=0) {
				double fpr = (double)falsePositive1/(double)(falsePositive1+trueNegative1);
				double tpr = (double)truePositive1/(double)(truePositive1+falseNegative1);
				System.out.println(fpr +"\t"+tpr);
				
				//System.out.println(tpr + );
				//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			}

			double res = falsePositive1/460.0;
			res = res/0.02608695652173913;
			yks.add(res);
			double res1 = truePositive1/460.0;
			res = res/ 0.2847826086956522  ;
			yks.add(res1);
			//System.out.println();


			//System.out.println(res);
		}

		/*double area = 0.0;
		for(int j=2; j <xks.size(); j++) {
			area += (xks.get(j)-xks.get(j-1))*(yks.get(j)+yks.get(j-1));
		}
		System.out.println(area/2);

		System.out.println(sortedVals.size());*/
	}


	public static void secondPass(){
		HashMap<Integer, ArrayList<String>> data = 
				ProjUtils.parseFolds("spambase.data");

		//System.out.println(data);		
		Set<Integer> keys = data.keySet();
		System.out.println(keys.size());
		int size = 0;
		ArrayList<String> trainingRows = new ArrayList<String>();
		ArrayList<String> testingRows = new ArrayList<String>();
		int k = 9;
		for(Integer key: keys) {
			size += data.get(key).size();

			if(k!=9) {
				trainingRows.addAll(data.get(key));
			} else {
				testingRows.addAll(data.get(key));
			}
			k--;
		}
		System.out.println(testingRows.size());
		//printList(trainingRows);

		HashMap<Integer, ArrayList<Double>> contents =  
				ProjUtils.convert(trainingRows);

		HashMap<Integer, ArrayList<Double>> contentsSpam =
				getSpamRows(contents);

		HashMap<Integer, ArrayList<Double>> contentsNonSpam =
				getNonSpamRows(contents);

		//System.out.println(contents.keySet().size());

		double[] meansArr = ProjUtils.returnAllMeans(trainingRows);
		ArrayList<Double> means = getArrayListFromArray(meansArr);

		ArrayList<Double> meansSpam = ProjUtils.returnAllMeansAsList(contentsSpam);
		ArrayList<Double> meansNonSpam = ProjUtils.returnAllMeansAsList(contentsNonSpam);

		//System.out.println("Means of Spam :"+meansSpam);
		//System.out.println("Means of Non-Spam :"+meansNonSpam);

		//System.out.println(means.size());

		double probspam = (double)getSpamRows(contents).size()/contents.size();
		double probnonspam = 1.0 - probspam;

		ArrayList<Double> sdsSpam = 
				getAllStandardDeviations(contentsSpam, meansSpam);
		ArrayList<Double> sdsNonSpam = 
				getAllStandardDeviations(contentsNonSpam, meansNonSpam);

		//System.out.println("Standard deviations Spam:"+sdsSpam);
		//System.out.println("Standard deviations NON Spam:"+sdsNonSpam);

		HashMap<Integer, ArrayList<Double>> contentsTestingRows =  
				ProjUtils.convert(testingRows);
		Set<Integer> allTestData = contentsTestingRows.keySet();
		//System.out.println(sds);

		ArrayList<Double> gausProbSpam = new ArrayList<Double>();
		ArrayList<Double> gausProbNonSpam = new ArrayList<Double>();

		/*gaussianProbabilityCalc(
				contentsTestingRows.get(1), meansSpam, sdsSpam);*/
		int index = 0;
		for(Integer key: allTestData) {
			double dSpm = 
					gaussianLogProbabilityCalc(
							contentsTestingRows.get(key), meansSpam, sdsSpam);

			double denominator = dSpm + Math.log(probspam);
			gausProbSpam.add(denominator);
			index++;

		}
		ArrayList<Double> spamOrNot = new ArrayList<Double>();
		index = 0;
		for(Integer key: allTestData) {
			double dNonSpm = 
					gaussianLogProbabilityCalc(
							contentsTestingRows.get(key), meansNonSpam, sdsNonSpam);

			double numerator = dNonSpm + Math.log(probnonspam);
			gausProbNonSpam.add(numerator);
			int lastElement = contentsTestingRows.get(key).size() - 1;
			spamOrNot.add(contentsTestingRows.get(key).get(lastElement));
			index++;
		}

		//System.out.println(gausProbSpam);
		//System.out.println(gausProbNonSpam);

		ArrayList<Double> result = new ArrayList<Double>();
		int numberCorrectPred = 0;
		int truePositive = 0;
		int trueNegative = 0;
		int falsePositive = 0;
		int falseNegative = 0;

		double thres = 0.1;
		HashMap<Double,ArrayList<Double>> ratioToSpam = new HashMap<Double, ArrayList<Double>>();
		for(int i=0; i < gausProbSpam.size(); i++) {
			double res = gausProbSpam.get(i)/gausProbNonSpam.get(i);
			result.add(res);

			///if(spamOrNot.get(i) == 0.0)
			//System.out.println("i"+i+" --> "+res +" <--->"+spamOrNot.get(i));


			if(ratioToSpam.containsKey(res)) {
				ArrayList<Double> spms = ratioToSpam.get(res);
				spms.add(spamOrNot.get(i));
				ratioToSpam.put(res, spms);
			} else {
				ArrayList<Double> spms = new ArrayList<Double>();
				spms.add(spamOrNot.get(i));
				ratioToSpam.put(res, spms);
			}


			if(res <= thres && spamOrNot.get(i)==1.0){
				truePositive++;
				// Predict spam and is not spam
			} else if (res <= thres && spamOrNot.get(i)!=1.0) {
				falsePositive++;
				// Predict not spam and is Spam
			} else if (res > thres && spamOrNot.get(i)==1.0) {
				falseNegative++;
				// Predict not spam and not spam
			} else {
				trueNegative++;
			}			

		}
		/*System.out.println(gausProbSpam.size());
		System.out.println(gausProbNonSpam.size());
		System.out.println(spamOrNot.size());
		//System.out.println("RESULT : "+result);
		System.out.println("Number of correct predictions = "+(truePositive+trueNegative) +" Out of "+allTestData.size());
		System.out.println("True Positive : "+truePositive);
		System.out.println("False Positive : "+falsePositive);
		System.out.println("True Negative : "+trueNegative);
		System.out.println("False Negative : "+falseNegative);*/

		System.out.println("**************************************************************************");

		int truePositive1 = 0;
		int trueNegative1 = 0;
		int falsePositive1 = 0;
		int falseNegative1 = 0;

		Set<Double> vals = ratioToSpam.keySet();
		ArrayList<Double> sortedVals = new ArrayList<Double>(vals);
		Collections.sort(sortedVals);
		ArrayList<Double> inverseSort = new ArrayList<Double>(sortedVals);
		int F = inverseSort.size();
		for(int m=0 ; m < F ; m++) {
			sortedVals.set(m, inverseSort.get(F-m-1));
		}
		System.out.println(sortedVals);
		ArrayList<Double> xks = new ArrayList<Double>();
		ArrayList<Double> yks = new ArrayList<Double>();
		for(Double val: sortedVals) {
			//double thres = 0.09;
			if(val <= thres && ratioToSpam.get(val).get(0)==1.0){
				//truePositive1++;
				truePositive1 += ratioToSpam.get(val).size();
				// Predict spam and is not spam
			} else if (val <= thres && ratioToSpam.get(val).get(0)!=1.0) {
				//falsePositive1++;
				falsePositive1 += ratioToSpam.get(val).size();
				// Predict not spam and is Spam
			} else if (val > thres && ratioToSpam.get(val).get(0)==1.0) {
				//falseNegative1++;
				falseNegative1 += ratioToSpam.get(val).size();
				// Predict not spam and not spam
			} else {
				//trueNegative1++;
				trueNegative1 += ratioToSpam.get(val).size();
			}
			//System.out.println(val+" --> "+ratioToSpam.get(val).get(0));
			/*System.out.print("True Positive : "+truePositive1);
			System.out.print("   False Positive : "+falsePositive1);
			System.out.print("   True Negative : "+trueNegative1);
			System.out.print("    False Negative : "+falseNegative1);
			System.out.print("     TPR(Y-axis) = "+truePositive1/460.0);
			System.out.println("     FPR(X-axis) = "+falsePositive1/460.0);*/

			/*double res = falsePositive1/460.0;
			res = res/0.12608695652173912;*/
			if((falsePositive1+trueNegative1)!=0) {
				double fpr = (double)falsePositive1/(double)(falsePositive1+trueNegative1);
				double tpr = (double)truePositive1/(double)(truePositive1+falseNegative1);
				System.out.println(fpr +"\t"+tpr);
				
				//System.out.println(tpr + );
				//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			}
			double res = falsePositive1/460.0;
			res = res/0.13695652173913042;
			xks.add(res);
			double res1 = truePositive1/460.0;
			res = res/0.3456521739130435  ;
			yks.add(res1);
			//System.out.println();

			//System.out.println(truePositive1/460.0);

			//System.out.println(res);
		}

		/*double area = 0.0;
		for(int j=2; j <xks.size(); j++) {
			area += (xks.get(j)-xks.get(j-1))*(yks.get(j)+yks.get(j-1));
		}
		System.out.println(area/2);

		System.out.println(sortedVals.size());*/
	}

	public static void thirdPass() {
		HashMap<Integer, ArrayList<String>> data = 
				ProjUtils.parseFolds("spambase.data");

		//System.out.println(data);		
		Set<Integer> keys = data.keySet();
		System.out.println(keys.size());
		int size = 0;
		ArrayList<String> trainingRows = new ArrayList<String>();
		ArrayList<String> testingRows = new ArrayList<String>();
		int k = 9;
		for(Integer key: keys) {
			size += data.get(key).size();

			if(k!=9) {
				trainingRows.addAll(data.get(key));
			} else {
				testingRows.addAll(data.get(key));
			}
			k--;
		}
		System.out.println(testingRows.size());
		//printList(trainingRows);

		HashMap<Integer, ArrayList<Double>> contents =  
				ProjUtils.convert(trainingRows);

		System.out.println(contents.keySet().size());

		double[] meansArr = ProjUtils.returnAllMeans(trainingRows);
		ArrayList<Double> means = getArrayListFromArray(meansArr);

		System.out.println(means.size());

		double probspam = (double)getSpamRows(contents).size()/contents.size();
		double probnonspam = 1.0 - probspam;

		HashMap<Integer,ArrayList<Double>> bin = 
				getTheBinParameters(contents, means);

		HashMap<Integer, ArrayList<Double>> contentsSpam =
				getSpamRows(contents);

		HashMap<Integer, ArrayList<Double>> contentsNonSpam =
				getNonSpamRows(contents);

		HashMap<Integer, ArrayList<Double>> contentsTesting =  
				ProjUtils.convert(testingRows);

		Set<Integer> testDataSpamKeys = 
				contentsTesting.keySet();

		HashMap<Integer, ArrayList<Double>> probBinSpam =
				probabibilitiesFiBin(bin, contentsSpam);
		HashMap<Integer, ArrayList<Double>> probBinNonSpam =
				probabibilitiesFiBin(bin, contentsNonSpam);
		//System.out.println(probBinNonSpam);
		int numberCorrectPred = 0;
		int truePositive = 0;
		int trueNegative = 0;
		int falsePositive = 0;
		int falseNegative = 0;

		HashMap<Double,ArrayList<Double>> ratioToSpam = new HashMap<Double, ArrayList<Double>>();
		double thres  = 2.0;
		for(Integer key: testDataSpamKeys) {
			ArrayList<Double> testingRow = 
					contentsTesting.get(key);
			//System.out.println(testingRow);
			//System.out.println(probBinSpam);			
			double condProbSpam = calculateBinProbability(
					testingRow, bin, probBinSpam);
			double condProbNonSpam = calculateBinProbability(
					testingRow, bin, probBinNonSpam);

			/*System.out.println(condProbSpam);
			System.out.println(condProbNonSpam);*/

			double num = condProbSpam * probspam;
			double den = condProbNonSpam * probnonspam;

			double ratio = num/den;
			System.out.println(ratio);

			if(ratioToSpam.containsKey(ratio)) {
				ArrayList<Double> spms = ratioToSpam.get(ratio);
				spms.add(testingRow.get(numberOfAttributes));
				ratioToSpam.put(ratio, spms);
			} else {
				ArrayList<Double> spms = new ArrayList<Double>();
				spms.add(testingRow.get(numberOfAttributes));
				ratioToSpam.put(ratio, spms);
			}

			if(ratio >= thres && testingRow.get(numberOfAttributes)==1.0){
				truePositive++;
				// Predict spam and is not spam
			} else if (ratio >= thres && testingRow.get(numberOfAttributes)!=1.0) {
				falsePositive++;
				// Predict not spam and is Spam
			} else if (ratio < thres && testingRow.get(numberOfAttributes)==1.0) {
				falseNegative++;
				// Predict not spam and not spam
			} else {
				trueNegative++;
			}

			/*if((ratio>= 1 && testingRow.get(numberOfAttributes)==1.0) ||
					(ratio< 1 && testingRow.get(numberOfAttributes)==0.0)) {
				numberCorrectPred++;
			} else {
				System.out.println(ratio+" --> "+testingRow.get(numberOfAttributes));
			}*/

		}

		System.out.println("Number of correct predictions = "+(truePositive+trueNegative) +" Out of "+testingRows.size());
		System.out.println("True Positive : "+truePositive);
		System.out.println("False Positive : "+falsePositive);
		System.out.println("True Negative : "+trueNegative);
		System.out.println("False Negative : "+falseNegative);

		System.out.println("**************************************************************************");

		int truePositive1 = 0;
		int trueNegative1 = 0;
		int falsePositive1 = 0;
		int falseNegative1 = 0;

		Set<Double> vals = ratioToSpam.keySet();
		ArrayList<Double> sortedVals = new ArrayList<Double>(vals);
		Collections.sort(sortedVals);
		System.out.println(sortedVals);
		ArrayList<Double> inverseSort = new ArrayList<Double>(sortedVals);
		int F = inverseSort.size();
		for(int m=0 ; m < F ; m++) {
			sortedVals.set(m, inverseSort.get(F-m-1));
		}
		System.out.println(sortedVals);
		ArrayList<Double> xks = new ArrayList<Double>();
		ArrayList<Double> yks = new ArrayList<Double>();
		for(Double val: sortedVals) {
			//double thres = 0.09;
			if(val >= thres && ratioToSpam.get(val).get(0)==1.0){
				//truePositive1++;
				truePositive1 += ratioToSpam.get(val).size();
				// Predict spam and is not spam
			} else if (val >= thres && ratioToSpam.get(val).get(0)!=1.0) {
				//falsePositive1++;
				falsePositive1 += ratioToSpam.get(val).size();
				// Predict not spam and is Spam
			} else if (val < thres && ratioToSpam.get(val).get(0)==1.0) {
				//falseNegative1++;
				falseNegative1 += ratioToSpam.get(val).size();
				// Predict not spam and not spam
			} else {
				//trueNegative1++;
				trueNegative1 += ratioToSpam.get(val).size();
			}
			//System.out.println(val+" --> "+ratioToSpam.get(val).get(0));
			/*System.out.print("True Positive : "+truePositive1);
			System.out.print("   False Positive : "+falsePositive1);
			System.out.print("   True Negative : "+trueNegative1);
			System.out.print("    False Negative : "+falseNegative1);
			System.out.print("     TPR(Y-axis) = "+truePositive1/460.0);
			System.out.println("     FPR(X-axis) = "+falsePositive1/460.0);*/
			
			if((falsePositive1+trueNegative1)!=0) {
				double fpr = (double)falsePositive1/(double)(falsePositive1+trueNegative1);
				double tpr = (double)truePositive1/(double)(truePositive1+falseNegative1);
				System.out.println(fpr +"\t"+tpr);
				
				//System.out.println(tpr + );
				//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			}
			double res = falsePositive1/460.0;
			res = res/0.021739130434782608;
			xks.add(res);
			double res1 = truePositive1/460.0;
			res = res/0.33260869565217394;
			yks.add(res1);
			//System.out.println();

			//System.out.println(res);
		}
		double area = 0.0;
		for(int j=2; j <xks.size(); j++) {
			area += (xks.get(j)-xks.get(j-1))*(yks.get(j)+yks.get(j-1));
		}
		System.out.println(area/2);
	}

	public static double calculateBinProbability(
			ArrayList<Double> testDate,
			HashMap<Integer, ArrayList<Double>> bin,
			HashMap<Integer, ArrayList<Double>> probabilities
			){
		ArrayList<Double> condProbs = 
				new ArrayList<Double>();
		for(int i=0; i < numberOfAttributes; i++) {
			double element = testDate.get(i);
			//System.out.println(element);
			double p = 0.0;
			if(element <= bin.get(2).get(i) ) {				
				p = probabilities.get(1).get(i);
				//System.out.println("selecting 1st bin : "+p+" at "+i);
			} else if(element <= bin.get(3).get(i) ) {
				p = probabilities.get(2).get(i);
				//System.out.println("selecting 2nd bin: "+p+" at "+i);
			} else if((element < bin.get(4).get(i) )) {
				p = probabilities.get(3).get(i);
				//System.out.println("selecting 3rd bin: "+p+" at "+i);
			} else {
				p = probabilities.get(4).get(i);
				//System.out.println("selecting 4th bin: "+p+" at "+i);
			}
			condProbs.add(p);
		}
		double prodP = 1.0;
		for(double p: condProbs) {
			prodP = prodP * p;
		}
		return prodP;
	}

	public static HashMap<Integer, ArrayList<Double>> probabibilitiesFiBin(
			HashMap<Integer,ArrayList<Double>> bin,
			HashMap<Integer,ArrayList<Double>> content) {
		ArrayList<Double> mins_lowmean = getInitializedDoubleAttributeList(0.0);
		ArrayList<Double> lowmean_mean = getInitializedDoubleAttributeList(0.0);
		ArrayList<Double> mean_highmean = getInitializedDoubleAttributeList(0.0);
		ArrayList<Double> highmean_max = getInitializedDoubleAttributeList(0.0);

		int N = content.size();

		Set<Integer> keys = content.keySet();

		for(Integer key: keys) {
			ArrayList<Double> row = content.get(key);
			for(int i=0; i < numberOfAttributes; i++) {
				double element = row.get(i);
				if(element <= bin.get(2).get(i) ) {
					double val = mins_lowmean.get(i);
					mins_lowmean.set(i, val+1.0);
				} else if(element <= bin.get(3).get(i) ) {
					double val = lowmean_mean.get(i);
					lowmean_mean.set(i, val+1.0);
				} else if((element < bin.get(4).get(i) )) {
					double val = mean_highmean.get(i);
					mean_highmean.set(i, val+1.0);
				} else {
					double val = highmean_max.get(i);
					highmean_max.set(i, val+1.0);
				}
			}
		}

		ArrayList<Double> probBin1 = updateProbability(mins_lowmean, (double)N);
		ArrayList<Double> probBin2 = updateProbability(lowmean_mean, (double)N);
		ArrayList<Double> probBin3 = updateProbability(mean_highmean, (double)N);
		ArrayList<Double> probBin4 = updateProbability(highmean_max, (double)N);

		HashMap<Integer, ArrayList<Double>> ret = new HashMap<Integer, ArrayList<Double>>();
		ret.put(1, probBin1);
		ret.put(2, probBin2);
		ret.put(3, probBin3);
		ret.put(4, probBin4);

		return ret;

	}

	public static ArrayList<Double> updateProbability(
			ArrayList<Double> row,
			double N) {
		for(int i=0; i < numberOfAttributes; i++) {
			double val = row.get(i);
			val = (val+1) / (N+2);
			row.set(i, val);
		}
		return row;
	}

	// 1. Min value 
	// 2. Low mean value
	// 3. Overall mean value
	// 4. High mean value
	// 5. Max value
	public static HashMap<Integer,ArrayList<Double>> getTheBinParameters(
			HashMap<Integer, ArrayList<Double>> contents,
			ArrayList<Double> mean) {
		Set<Integer> keys = contents.keySet();
		ArrayList<Double> mins = getInitializedDoubleAttributeList(99999999.0);
		ArrayList<Double> maxs = getInitializedDoubleAttributeList(0.0);
		for(Integer key: keys) {
			ArrayList<Double> list = contents.get(key);
			int index = 0;
			for(double val: list) {
				if (mins.get(index) > val) {
					mins.set(index, val);
				}

				if(maxs.get(index) < val) {
					maxs.set(index, val);
				}
			}
		}

		ArrayList<Double> lowMeans = getInitializedDoubleAttributeList(0.0);
		ArrayList<Double> highMeans = getInitializedDoubleAttributeList(0.0);

		HashMap<Integer, ArrayList<Double>> sortedMap = 
				returnSortedList(contents);	

		System.out.println(sortedMap.get(1));
		System.out.println(mean.get(1));
		for(int i=0; i < mean.size(); i++) {
			/*lowMeans.set(i, (mins.get(i)+mean.get(i))/2.0);
			highMeans.set(i, (maxs.get(i)+mean.get(i))/2.0);*/
			lowMeans.set(i, mean.get(i)-0.15);
			highMeans.set(i, mean.get(i)-0.15);
		}
		HashMap<Integer, ArrayList<Double>> bin = 
				new HashMap<Integer, ArrayList<Double>>();

		bin.put(1, mins);
		bin.put(2, lowMeans);
		bin.put(3, mean);
		bin.put(4, highMeans);
		bin.put(5, maxs);

		return bin;
	}

	public static HashMap<Integer, ArrayList<Double>> returnSortedList(
			HashMap<Integer, ArrayList<Double>> contents) {
		HashMap<Integer, ArrayList<Double>> sortedList =
				new HashMap<Integer, ArrayList<Double>>();
		Set<Integer> keys = contents.keySet();
		for(Integer key: keys) {
			ArrayList<Double> contentRows = contents.get(key);
			for(int i=0; i<contentRows.size(); i++) {
				ArrayList<Double> sortedCols = sortedList.get(i);
				if(sortedCols == null) {
					ArrayList<Double> col = new ArrayList<Double>();
					col.add(contentRows.get(i));
					sortedList.put(i, col);
				} else {
					sortedCols.add(contentRows.get(i));
					sortedList.put(i, sortedCols);
				}
			}
		}

		keys = sortedList.keySet();
		for(Integer key: keys) {
			ArrayList<Double> list= sortedList.get(key);
			Collections.sort(list);
			sortedList.put(key, list);
		}

		return sortedList;
	}

	public static double gaussianProbabilityCalc(
			ArrayList<Double> row,
			ArrayList<Double> mean,
			ArrayList<Double> sds) {
		ArrayList<Double> prob = getInitializedDoubleAttributeList(0.0);

		for(int i=0; i < numberOfAttributes; i++) {
			double xn = row.get(i);
			double num = Math.pow((xn-mean.get(i)), 2);
			double exp = num/(2*sds.get(i));			
			double pinv = Math.exp(exp) * Math.sqrt((2*Math.PI*sds.get(i)));
			double p = 1.0/pinv;
			prob.set(i,p);
			if(p > 1.0) {
				//System.out.println(i);
				/*System.out.println("P = "+p+
						" : pinv = "+pinv+
						" : SD = "+sds.get(i)+
						" : xn = "+xn + 
						" : num = "+num + 
						" : exp = "+exp + 
						"\n : 1/Root(2*Pi*SD) = "+(1.0/Math.sqrt((2*Math.PI*sds.get(i))))+
						" : Math.exp(exp) = "+Math.exp(exp)
						);*/
			}
		}

		double fP = 0.0;
		/*for(Double d: prob) {
			fP = fP * d;
		}*/
		//System.out.println(prob);
		for(Double d: prob) {
			fP += Math.log(d);
			if(Double.isNaN(fP) || Double.isInfinite(fP))
				System.out.println(d + " LOG ->"+fP);
		}

		return fP;
	}


	public static double gaussianLogProbabilityCalc(
			ArrayList<Double> row,
			ArrayList<Double> mean,
			ArrayList<Double> sds) {
		ArrayList<Double> prob = getInitializedDoubleAttributeList(0.0);
		double ans = 0.0;
		for(int i=0; i < numberOfAttributes; i++) {
			double xn = row.get(i);
			double num = Math.pow((xn-mean.get(i)), 2);
			double rightVar = num/(2*sds.get(i));			

			double leftVar = Math.log(Math.sqrt((2*Math.PI*sds.get(i))));
			ans += (-1* leftVar) - rightVar;		
			if(Double.isInfinite(ans) || Double.isNaN(ans)) {
				System.out.println(ans);
				System.out.println(						
						" : SD = "+sds.get(i)+
						" : xn = "+xn + 
						" : num = "+num + 
						" : leftVar = "+leftVar						
						);
			}
		}
		return ans;
	}

	public static double getSumOfLogs(ArrayList<Double> nums) {
		double fP = 0.0;
		for(Double d: nums) {
			fP += Math.log10(d);
			System.out.println(d + " LOG ->"+fP);
		}
		return fP;
	}

	//System.out.println(now +"= "+row.get(i) +" - "+means.get(i));
	//System.out.println(i +" : "+means.get(i));
	/*if(i>55) {
		System.out.println(now +"= "+row.get(i) +" - "+means.get(i));
		System.out.println(i +" : "+row.get(i));
	}*/
	private static ArrayList<Double> getAllStandardDeviations(
			HashMap<Integer, ArrayList<Double>> contents,
			ArrayList<Double> means) {
		Set<Integer> keys = contents.keySet();
		int N = contents.size();
		ArrayList<Double> sds = getInitializedDoubleAttributeList(0.0);
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
				sds.set(i, 1/(double)N);
			} else {
				sds.set(i, sd/(double)N);
			}
		}
		//System.out.println(sds);
		return sds;
	}


	public static double calculateTheProbabilityRatio(
			ArrayList<Double> p_fi_lessorequal_givenspam,
			ArrayList<Double> p_fi_greater_givenspam,
			ArrayList<Double> p_fi_lessorequal_givennonspam,
			ArrayList<Double> p_fi_greater_givennonspam,
			ArrayList<Double> means,
			double probspam,
			double probnonspam,
			ArrayList<Double> testData){
		ArrayList<Double> cond_prob_givenspam = getInitializedDoubleAttributeList(0.0);
		ArrayList<Double> cond_prob_givennonspam = getInitializedDoubleAttributeList(0.0);

		double ratioProbLogs = Math.log(probspam/probnonspam);

		for(int i=0; i < numberOfAttributes; i++) {
			if(testData.get(i)>means.get(i)){
				cond_prob_givenspam.set(i, p_fi_greater_givenspam.get(i));
				cond_prob_givennonspam.set(i, p_fi_greater_givennonspam.get(i));
			} else {
				cond_prob_givenspam.set(i, p_fi_lessorequal_givenspam.get(i));
				cond_prob_givennonspam.set(i, p_fi_lessorequal_givennonspam.get(i));
			}
		}

		double condProbRatio = 1.0;
		double condProbNum = 0.0;
		double condProbDen = 0.0;
		for(int i=0; i < numberOfAttributes; i++) {
			/*condProbNum += Math.log(cond_prob_givenspam.get(i));
			condProbDen += Math.log(cond_prob_givennonspam.get(i));*/
			condProbRatio = condProbRatio *  cond_prob_givenspam.get(i) / cond_prob_givennonspam.get(i);
		}

		//double logProb = ratioProbLogs + (condProbNum/condProbDen);
		double logProb = (probspam / probnonspam) * condProbRatio;
		return logProb;
	}

	public static ArrayList<Integer> extractSpam(
			HashMap<Integer, ArrayList<Double>> contents){
		ArrayList<Integer> spamIds = new ArrayList<Integer>();
		Set<Integer> ids = contents.keySet();
		int end = contents.get(0).size()-1;
		for(Integer id: ids) {
			if(contents.get(id).get(end).equals(1.0)){
				spamIds.add(id);
			}
		}
		return spamIds;
	}


	public static ArrayList<Double> probFiLessThanOrEqualMeanGivenSpamWithSmoothing(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means)  {
		ArrayList<Double> probs = new ArrayList<Double>();
		//System.out.println("Total number of test data : "+trainingData.keySet().size());
		HashMap<Integer, ArrayList<Double>> trainingDataSpam =
				getSpamRows(trainingData);
		//System.out.println("Number of spam mails in training set: "+trainingDataSpam.keySet().size());
		ArrayList<Integer> spamRowsLessThanOrEqual = 
				numberOfRowsLesserThanOrEqualToMean(trainingDataSpam, means);

		Set<Integer> keys = trainingDataSpam.keySet();		
		double totalRows = (double)keys.size();
		//System.out.println("Number of Rows Lesser than mean :"+spamRowsLessThanOrEqual.size());
		//System.out.println("Total number of rows :"+totalRows);
		for(Integer i: spamRowsLessThanOrEqual) {
			//System.out.println("Feature division : "+i+"/"+totalRows);
			double p = (double)(i+1)/(totalRows+2);
			//System.out.println("Probability values : "+p);
			probs.add(p);
		}
		//System.out.println("Final probability size : "+probs.size());
		return probs;
	}

	public static ArrayList<Double> probFiGreaterThanMeanGivenSpamWithSmoothing(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means)  {
		ArrayList<Double> probs = new ArrayList<Double>();

		HashMap<Integer, ArrayList<Double>> trainingDataSpam =
				getSpamRows(trainingData);

		ArrayList<Integer> spamRowsGreaterThanMeans = 
				numberOfRowsGreaterThanMean(trainingDataSpam, means);

		Set<Integer> keys = trainingDataSpam.keySet();		
		double totalRows = (double)keys.size();

		for(Integer i: spamRowsGreaterThanMeans) {
			double p = (double)(i+1)/(totalRows+2);
			probs.add(p);
		}

		return probs;
	}

	public static ArrayList<Double> probFiLessThanOrEqualMeanGivenNonSpamWithSmoothing(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means)  {
		ArrayList<Double> probs = new ArrayList<Double>();

		HashMap<Integer, ArrayList<Double>> trainingDataNSpam =
				getNonSpamRows(trainingData);

		ArrayList<Integer> nonSpamRowsLessThanOrEqual = 
				numberOfRowsLesserThanOrEqualToMean(trainingDataNSpam, means);

		Set<Integer> keys = trainingDataNSpam.keySet();		
		double totalRows = (double)keys.size();

		for(Integer i: nonSpamRowsLessThanOrEqual) {
			double p = (double)(i+1)/(totalRows+2);
			probs.add(p);
		}

		return probs;
	}

	public static ArrayList<Double> probFiGreaterThanMeanGivenNonSpamWithSmoothing(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means)  {
		ArrayList<Double> probs = new ArrayList<Double>();

		HashMap<Integer, ArrayList<Double>> trainingDataNSpam =
				getSpamRows(trainingData);

		ArrayList<Integer> nonSpamRowsGreaterThanMeans = 
				numberOfRowsGreaterThanMean(trainingDataNSpam, means);

		Set<Integer> keys = trainingDataNSpam.keySet();		
		double totalRows = (double)keys.size();

		for(Integer i: nonSpamRowsGreaterThanMeans) {
			double p = (double)(i+1)/(totalRows+2);
			probs.add(p);
		}

		return probs;
	}

	public static ArrayList<Double> probFiLessThanOrEqualMeanGivenSpam(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means)  {
		ArrayList<Double> probs = new ArrayList<Double>();

		HashMap<Integer, ArrayList<Double>> trainingDataSpam =
				getSpamRows(trainingData);

		ArrayList<Integer> spamRowsLessThanOrEqual = 
				numberOfRowsLesserThanOrEqualToMean(trainingDataSpam, means);

		Set<Integer> keys = trainingDataSpam.keySet();		
		double totalRows = (double)keys.size();

		for(Integer i: spamRowsLessThanOrEqual) {
			double p = (double)i/totalRows;
			probs.add(p);
		}

		return probs;
	}

	public static ArrayList<Double> probFiGreaterThanMeanGivenSpam(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means)  {
		ArrayList<Double> probs = new ArrayList<Double>();

		HashMap<Integer, ArrayList<Double>> trainingDataSpam =
				getSpamRows(trainingData);

		ArrayList<Integer> spamRowsGreaterThanMeans = 
				numberOfRowsGreaterThanMean(trainingDataSpam, means);

		Set<Integer> keys = trainingDataSpam.keySet();		
		double totalRows = (double)keys.size();

		for(Integer i: spamRowsGreaterThanMeans) {
			double p = (double)i/totalRows;
			probs.add(p);
		}

		return probs;
	}

	public static ArrayList<Double> probFiLessThanOrEqualMeanGivenNonSpam(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means)  {
		ArrayList<Double> probs = new ArrayList<Double>();

		HashMap<Integer, ArrayList<Double>> trainingDataNSpam =
				getNonSpamRows(trainingData);

		ArrayList<Integer> nonSpamRowsLessThanOrEqual = 
				numberOfRowsLesserThanOrEqualToMean(trainingDataNSpam, means);

		Set<Integer> keys = trainingDataNSpam.keySet();		
		double totalRows = (double)keys.size();

		for(Integer i: nonSpamRowsLessThanOrEqual) {
			double p = (double)i/totalRows;
			probs.add(p);
		}

		return probs;
	}

	public static ArrayList<Double> probFiGreaterThanMeanGivenNonSpam(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means)  {
		ArrayList<Double> probs = new ArrayList<Double>();

		HashMap<Integer, ArrayList<Double>> trainingDataNSpam =
				getNonSpamRows(trainingData);

		ArrayList<Integer> nonSpamRowsGreaterThanMeans = 
				numberOfRowsGreaterThanMean(trainingDataNSpam, means);

		Set<Integer> keys = trainingDataNSpam.keySet();		
		double totalRows = (double)keys.size();

		for(Integer i: nonSpamRowsGreaterThanMeans) {
			double p = (double)i/totalRows;
			probs.add(p);
		}

		return probs;
	}

	public static HashMap<Integer, ArrayList<Double>> getSpamRows(
			HashMap<Integer, ArrayList<Double>> trainingData){

		HashMap<Integer, ArrayList<Double>> trainingDataSpam = 
				new HashMap<Integer, ArrayList<Double>>();

		Set<Integer> keys = trainingData.keySet();

		for(Integer key: keys) {
			ArrayList<Double> row = trainingData.get(key);
			int size = row.size() - 1;
			if(row.get(size).intValue() == 1){
				trainingDataSpam.put(key, row);
			}
		}

		return trainingDataSpam;
	}

	public static HashMap<Integer, ArrayList<Double>> getNonSpamRows(
			HashMap<Integer, ArrayList<Double>> trainingData){

		HashMap<Integer, ArrayList<Double>> trainingDataNSpam = 
				new HashMap<Integer, ArrayList<Double>>();

		Set<Integer> keys = trainingData.keySet();

		for(Integer key: keys) {
			ArrayList<Double> row = trainingData.get(key);
			int size = row.size() - 1;
			if(row.get(size).intValue() == 0){
				trainingDataNSpam.put(key, row);
			}
		}

		return trainingDataNSpam;
	}	

	public static int numberOfAttributes = 57;

	public static ArrayList<Integer> numberOfRowsGreaterThanMean(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means) {
		Set<Integer> keys = trainingData.keySet();
		ArrayList<Integer> totals = getInitializedAttributeList(0);
		for(Integer key: keys) {
			ArrayList<Double> presentRow = trainingData.get(key);
			for(int i=0; i<presentRow.size()-1; i++) {
				if(presentRow.get(i).compareTo(means.get(i)) > 0) {
					Integer d = totals.get(i);
					totals.set(i, d+1);
				}
			}
		}
		return totals;
	}

	public static ArrayList<Integer> numberOfRowsLesserThanOrEqualToMean(
			HashMap<Integer, ArrayList<Double>> trainingData,
			ArrayList<Double> means) {
		Set<Integer> keys = trainingData.keySet();
		ArrayList<Integer> totals = getInitializedAttributeList(0);
		for(Integer key: keys) {
			ArrayList<Double> presentRow = trainingData.get(key);
			for(int i=0; i<presentRow.size()-1; i++) {
				if(presentRow.get(i).compareTo(means.get(i)) <= 0) {
					Integer d = totals.get(i);
					totals.set(i, d+1);
				}
			}
		}		
		return totals;
	}

	public static ArrayList<Integer> getInitializedAttributeList(int initVal){
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for(int i=0; i < numberOfAttributes; i++) {
			arr.add(i, initVal);
		}
		return arr;
	}

	public static ArrayList<Double> getInitializedDoubleAttributeList(double initVal){
		ArrayList<Double> arr = new ArrayList<Double>();
		for(int i=0; i < numberOfAttributes; i++) {
			arr.add(i, initVal);
		}
		return arr;
	}

	public static ArrayList<Double> getInitializedDoubleAttributeList(int size, double initVal){
		ArrayList<Double> arr = new ArrayList<Double>();
		for(int i=0; i < size; i++) {
			arr.add(i, initVal);
		}
		return arr;
	}

	public static void printDoubleArray(double[] arr) {
		for(int i=0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
	}		


	public static ArrayList<Double> getArrayListFromArray(double[] arr) {
		ArrayList<Double> list = new ArrayList<Double>();
		for(int i=0; i < arr.length; i++) {			
			list.add(arr[i]);
		}
		return list;
	}

	public static void printList(ArrayList list){
		for(Object o: list){
			System.out.println(o);
		}
	}
}
