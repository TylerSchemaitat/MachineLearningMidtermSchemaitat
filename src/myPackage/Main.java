package myPackage;

import libsvm.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//https://shyr.io/blog/using-libsvm-in-java

public class Main {
    public static int iteration = 0;
    public static int testIndex = 0;
    public static void main(String[] args){
        double trainRatio, devRatio, testRatio;
        int amt0, amt1;
        Thesaurus.setup();
        List<String[]> sents = new ArrayList<>();
        svm_model obj = new svm_model();
        String trainFileString = "MLMidTerm-main/MLMidTerm-main/train_with_label.txt";
        String devFileString = "MLMidTerm-main/MLMidTerm-main/dev_with_label.txt";
        String testFileString = "MLMidTerm-main/MLMidTerm-main/test_without_label.txt";
        List<double[]> featureList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();
        extractData(trainFileString, featureList, yList, sents, true);
        double[] y_train_Array = new double[yList.size()];
        for(int i = 0; i < y_train_Array.length; i++)
            y_train_Array[i] = yList.get(i);
        DataSet trainingSet = new DataSet(yList, featureList);
        DataSet[] trainingSetSplit = split_test_actual(trainingSet);
        System.out.println("featureLength:"+featureList.size()+"yLength:"+yList.size());

        svm_model[] models = new svm_model[3];
        for(int i = 0; i < 3; i++){
            svm_node[][] nodes = buildPointArray(trainingSetSplit[i].features);
            System.out.println("building model");
            double[] actual_array = new double[trainingSetSplit[i].actual.size()];
            for(int j = 0; j < trainingSetSplit[i].actual.size(); j++)
                actual_array[j] = trainingSetSplit[i].actual.get(j);
            models[i] = buildModel(nodes, actual_array);
        }


        //doing dev
        List<double[]> devFeatureList = new ArrayList<>();
        List<Double> dev_actual_list = new ArrayList<>();
        extractData(devFileString, devFeatureList, dev_actual_list, sents, true);
        double[] dev_actual_array = new double[devFeatureList.size()];
        for(int i = 0; i < dev_actual_array.length; i++)
            dev_actual_array[i] = dev_actual_list.get(i);
        double[] dev_prediction = predictArray(models, buildPointArray(devFeatureList));
        swap(dev_prediction);


        int result = comparePrediction(dev_prediction, dev_actual_array);
        System.out.println();
        //printDataSet(devFeatureList, dev_actual_list, prediction,sents);
        System.out.println("result:"+(result/100) +"."+(result%100));
        //end dev

        List<double[]> testFeatureList = new ArrayList<>();
        extractData(testFileString, testFeatureList, dev_actual_list, sents, false);
        double[] test_prediction = predictArray(models, buildPointArray(testFeatureList));
        swap(test_prediction);
        printRatio("dev pred: ", dev_prediction);
        printRatio("test pred: ", test_prediction);
        printRatio("dev actual", dev_actual_array);
        printRatio("train actual: ", y_train_Array);
        for(double d: y_train_Array)
            System.out.print(d + ", ");
        System.out.println();
        for(int i = 0; i < 3; i++){
            printRatio("split train: "+i+": ", trainingSetSplit[i].actual);
        }


        String final_result = "";
        int num;
        for(int i = 0; i < test_prediction.length; i++){
            if(test_prediction[i] > 0.5)
                num = 1;
            else
                num = 0;
            final_result += "test_id_"+i + "\t" + num + "\n";
        }
        Data.writeToFile(final_result);




        //System.out.println("test size: " + prediction.length +"index: " + testIndex);
    }
    public static void printRatio(String title, double[] array){
        int amt0 = 0, amt1 = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] > 0.5)
                amt1++;
            else
                amt0++;
        }
        System.out.println(title+"1's: "+amt1+" 0's: "+amt0);
    }
    public static void printRatio(String title, List<Double> array){
        int amt0 = 0, amt1 = 0;
        for(int i = 0; i < array.size(); i++){
            if(array.get(i).doubleValue() > 0.5)
                amt1++;
            else
                amt0++;
        }
        System.out.println(title+"1's: "+amt1+" 0's: "+amt0);
    }
    public static void swap(double[] array){
        for(int i = 0; i < array.length; i++){
            if(array[i] > 0.5)
                array[i] = 0;
            else
                array[i] = 1;
        }
    }
    public static DataSet[] split_test_actual(DataSet set){
        int amt1 = 0, amt0 = 0;
        for(int i = 0; i < set.features.size(); i++){
            if(set.actual.get(i) > 0.5)
                amt1++;
            else
                amt0++;
        }
        DataSet set0 = new DataSet(), set1 = new DataSet();
        int index = 0, index0 = 0, index1 = 0;
        for(int i = 0; i < set.features.size(); i++){
            if(set.actual.get(i) > 0.5){
                set1.actual.add(set.actual.get(i));
                set1.features.add(set.features.get(i));
            }else{
                set0.actual.add(set.actual.get(i));
                set0.features.add(set.features.get(i));
            }
        }


        DataSet[] split_sets = new DataSet[3];
        int length = set1.features.size() / 3;
        for(int i = 0; i < 3; i++){
            split_sets[i] = new DataSet();
            for(int j = 0; j < length; j++){
                split_sets[i].actual.add(set1.actual.get(i*length + j));
                split_sets[i].features.add(set1.features.get(i*length + j));
            }
        }
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < set0.features.size(); j++){
                split_sets[i].actual.add(set0.actual.get(j));
                split_sets[i].features.add(set0.features.get(j));
            }
        }
        return split_sets;



    }

    public static void extractData(String path, List<double[]> featureList, List<Double> yList, List<String[]> sents, boolean has_label){
        File file = new File(path);
        Scanner scan;
        try{
            scan = new Scanner(file);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        while(scan.hasNextLine()){
            if(!has_label)
                testIndex++;
            String s = scan.nextLine();
            int start = 1 + s.indexOf("\t");
            s = s.substring(start);
            int endSentence = s.indexOf("\t");
            String firstSentence = s.substring(0,endSentence - 1);
            s = s.substring(endSentence + 1);
            String secondSentence;
            String output = "";
            endSentence = s.indexOf("\t");
            if(has_label){

                secondSentence = s.substring(0,endSentence - 1);
                output = s.substring(endSentence + 1);
                Double outputDouble = Double.parseDouble(output);
                yList.add(outputDouble);
            }
            else {
                secondSentence = s;
            }


            String features = FeatureMaker.getFeatures(firstSentence, secondSentence, null);
            featureList.add(FeatureMaker.getFeatures(firstSentence, secondSentence, featureList.size()));
            if(has_label)
            System.out.println((featureList.size() - 1)+" ("+output+")<"+firstSentence+"> (2)<"+secondSentence+"> (y)<" + output + ">"+ features);
            sents.add(new String[2]);
            sents.get(sents.size() - 1)[0] = firstSentence;
            sents.get(sents.size() - 1)[1] = secondSentence;
            iteration++;
        }
    }

    public static void printDataSet(List<double[]> featureList, List<Double> yList, double[] prediction, List<String[]> sents){
        int length = featureList.size();
        String sent1;
        String sent2;
        String[] a;
        for(int i = 0; i < length; i++){
            a = sents.get(i);
            sent1 = a[0];
            sent2 = a[1];
            System.out.println("(1)<"+sent1+"> (2)<"+sent2+"> (y)<" + yList.get(i) + "> (pred)<"+prediction[i]+">");
        }

    }

    public static String doubleArrayToString(double[] features){
        String result = "{";
        for(int i = 0; i < features.length; i++){
            result += String.format("%,.2f", features[i]);
            if(i != features.length)
                result += ", ";
        }
        result += "}";
        return result;
    }
    public static String intArrayToString(int[] num){
        String result = "{";
        for(int i = 0; i < num.length; i++){
            result += String.valueOf(num[i]);
            if(i != num.length)
                result += ", ";
        }
        result += "}";
        return result;
    }

    public static svm_node[] buildPoint(double[] features) {
        svm_node[] point = new svm_node[features.length];

        for(int i = 0; i < features.length; i++){
            point[i] = new svm_node();
            point[i].index = i + 1;
            point[i].value = features[i];
        }
        return point;
    }

    public static svm_node[][] buildPointArray(List<double[]> doubleList){
        double[] features = doubleList.get(0);
        svm_node[][] points = new svm_node[doubleList.size()][features.length];
        for(int i = 0; i < doubleList.size(); i++){
            points[i] = buildPoint(doubleList.get(i));
        }
        return points;
    }

    public static svm_model buildModel(svm_node[][] nodes, double[] yArray) {
        /*
        for(int i = 0; i < yArray.length; i++){
            if(yArray[i] < 0.5)
                yArray[i] = -1.0;
        }
        */
        // Build Parameters
        svm_parameter param = new svm_parameter();
        param.svm_type    = svm_parameter.ONE_CLASS;
        param.kernel_type =
                //svm_parameter.RBF;
                //svm_parameter.SIGMOID;
                svm_parameter.LINEAR;
        //param.degree = 5;
        //param.gamma       = 0.5;
        param.nu          = 0.5;
        param.cache_size  = 100;

        // Build Problem
        svm_problem problem = new svm_problem();
        problem.x = nodes;
        problem.l = nodes.length;
        System.out.println("(build model)num features:"+nodes.length);
        //problem.y = prepareY(nodes.length);
        problem.y = yArray;

        // Build Model
        return svm.svm_train(problem, param);
    }

    private static double[] prepareY(int size) {
        double[] y = new double[size];

        for (int i=0; i < size; i++)
            y[i] = 1;

        return y;
    }

    public static double predict(svm_model[] models, svm_node[] nodes) {
        double sum = 0;
        for(int i = 0; i < 3; i++){
            double prediction = svm.svm_predict(models[i], nodes);
            if(prediction > 0.5)
                sum += 1;
        }
        sum = sum / 3.0;
        if(sum > 0.5)
            sum = 1;
        else
            sum = 0;
        return sum;
    }

    public static double[] predictArray(svm_model[] models, svm_node[][] nodes){
        double[] result = new double[nodes.length];
        for(int i = 0; i < nodes.length; i++){
            result[i] = predict(models, nodes[i]);
        }
        return result;
    }

    public static int comparePrediction(double[] prediction, double[] actual){

        int length = prediction.length;
        int count = 0;
        int[] pred = new int[length];
        int[] act = new int[length];
        for(int i = 0; i < length; i++){
            pred[i] = 0;
            if(prediction[i] > 0.5)
                pred[i] = 1;
            act[i] = 0;
            if(actual[i] > 0.5)
                act[i] = 1;
            if(act[i] == pred[i])
                count++;
        }
        int result = count * 10000 / length;
        System.out.println(doubleArrayToString(prediction));
        System.out.println(doubleArrayToString(actual));
        System.out.println(intArrayToString(pred));
        System.out.println(intArrayToString(act));
        int count0 = 0, count1 = 0;
        for(int i = 0; i < act.length; i++){
            if(act[i] == 1)
                count1++;
            else
                count0++;
        }
        System.out.println("1's: " + count1 + "0's: "+count0);

        return result;
    }
}


