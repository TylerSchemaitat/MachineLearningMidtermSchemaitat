package myPackage;

import java.util.*;

public class FeatureMaker {
    static int iteration = 0;
    public static double[] getFeatures(String s1, String s2, int it){
        iteration = it;
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        double[] features = new double[5];
        features[0] = lengthWords(s1, s2);
        features[1] = compareNumbers(s1, s2);
        features[4] = compareSynonyms(s1, s2);
        features[2] = searchQuotes(s1, s2);
        features[3] = avgWordLength(s1, s2);
        return features;
    }

    public static String getFeatures(String s1, String s2, String nullString){
        double[] features = getFeatures(s1, s2, -1);
        String result = "{";
        for(int i = 0; i < features.length; i++){
            result += String.format("%,.2f", features[i]);
            if(i != features.length)
                result += ", ";
        }
        result += "}";
        return result;
    }

    public static double lengthWords(String s1, String s2){
        int length1 = lengthWordsFromString(s1);
        int length2 = lengthWordsFromString(s2);
        double difference = Math.abs(length1 - length2) / 2.0;
        double result = sigmoid(difference) - 0.5;
        return result;
    }

    public static double avgWordLength(String s1, String s2){
        int length1 = lengthWordsFromString(s1);
        int length2 = lengthWordsFromString(s2);
        double wordSize1 = s1.length() / (length1 + 0.0);
        double wordSize2 = s2.length() / (length2 + 0.0);
        double difference = Math.abs(wordSize1 - wordSize2) / 2.0;
        double result = sigmoid(difference) - 0.50;
        return result;
    }

    public static int lengthWordsFromString(String s){
        int index = 0;
        int count = 0;
        index = s.indexOf(" ");
        while(index == 0){
            s = s.substring(1);
            index = s.indexOf(" ");
        }
        while(index != -1){
            s = s.substring(index + 1);
            index = s.indexOf(" ");
            count++;
        }
        return count;
    }

    public static double sigmoid(double num){
        double result = 1.0 / (1.0 + Math.pow(2, -1*num));
        return result;
    }

    public static boolean isNumber(String s){
        double num;
        try {
            num = Double.parseDouble(s);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static String[] stringToWords(String s){
        List<String> list = new ArrayList<>();
        int index = 0;
        int count = 0;
        index = s.indexOf(" ");
        while(index == 0){
            s = s.substring(1);
            index = s.indexOf(" ");
        }
        while(index != -1){
            list.add(s.substring(0, index + 1));
            s = s.substring(index + 1);
            index = s.indexOf(" ");
            count++;
        }
        String [] result = new String[list.size()];
        list.toArray(result);
        return result;
    }

    public static double compareNumbers(String s1, String s2) {
        String[] words1 = stringToWords(s1);
        String[] words2 = stringToWords(s2);
        List<String> number1 = new ArrayList<>();
        List<String> number2 = new ArrayList<>();

        for (String s : words1) {
            if (isNumber(s))
                number1.add(s);
        }
        for (String s : words2) {
            if (isNumber(s))
                number2.add(s);
        }
        int count = 0;
        boolean foundSame = false;
        for (int i = 0; i < number1.size(); i++) {
            foundSame = false;
            for (int j = 0; j < number2.size(); j++) {
                if(number1.get(i).equals(number2.get(j))) {
                    foundSame = true;
                    break;
                }
            }
            if(foundSame)
                count++;
        }
        int shorterLength = number1.size();
        if(shorterLength > number2.size() )
            shorterLength = number2.size();
        int numWrong = shorterLength - count;
        double result = sigmoid(numWrong) - 0.5;


        return result;
    }

    public static double compareSynonyms(String s1, String s2){
        String[] words1 = stringToWords(s1);
        String[] words2 = stringToWords(s2);
        List<String> list1 = StringFunctions.stringToList(words1), list2 = StringFunctions.stringToList(words2);
        removeSameWords(list1, list2);
        double num_diff_words = list1.size() + list2.size();
        double result = sigmoid(num_diff_words / 10) - 0.5;

        return result;
    }

    public static void removeSameWords(List<String> list1, List<String> list2){

        boolean removed_a_word = false;
        boolean finished_for_loops = false;
        while(!finished_for_loops){
            for(int i = 0; i < list1.size() && !removed_a_word; i++){
                for(int j = 0; j < list2.size() && !removed_a_word; j++){
                    String word1 = list1.get(i), word2 = list2.get(j);
                    if(list1.get(i).equals(list2.get(j))){
                        list1.remove(i);
                        list2.remove(j);
                        removed_a_word = true;
                    }
                    if((i == list1.size() - 1 && j == list2.size() - 1) || (list1.size() == 0 || list2.size() == 0))
                        finished_for_loops = true;
                }
            }
            removed_a_word = false;
        }

    }

    public static double searchAbbreviations(String s1, String s2){
        String[] words1 = stringToWords(s1), words2 = stringToWords(s2);
        List<String> list1 = StringFunctions.stringToList(words1), list2 = StringFunctions.stringToList(words2);
        //removeSameWords(list1, list2);
        words1 = StringFunctions.listToString(list1);
        words2 = StringFunctions.listToString(list2);
        int count = search1Abbreviation(words1, words2);
        count += search1Abbreviation(words2, words1);
        double result = sigmoid(count) - 0.5;
        return result;
    }

    public static int search1Abbreviation(String[] words1, String[] words2){
        int count = 0;
        for(int phraseLength = 1; phraseLength < words1.length; phraseLength++){
            for(int i = 0; i < words1.length - phraseLength; i++){
                int j;
                for(j = i; j < phraseLength + i; j++){
                    String letter = words1[j].substring(0, 1);
                    String abbrev_letter = words2[j].substring(j - i, j - i + 1);
                    if(!letter.equals(abbrev_letter))
                        break;
                }
                if(j == phraseLength + i - 1)
                    count++;
            }
        }
        return count;
    }

    public static double searchQuotes(String s1, String s2){
        String[] words1 = stringToWords(s1), words2 = stringToWords(s2);
        if(countQuotes(words1) == 0 || countQuotes(words2) == 0)
            return 0.0;
        if(countQuotes(words1) % 2 == 1){
            String[] temp = new String[words1.length + 1];
            for(int i = 0; i < words1.length; i++)
                temp[i] = words1[i];
            temp[temp.length - 1] = "\"";
            words1 = temp;
        }
        if(countQuotes(words2) % 2 == 1){
            String[] temp = new String[words2.length + 1];
            for(int i = 0; i < words2.length; i++)
                temp[i] = words2[i];
            temp[temp.length - 1] = "\"";
            words2 = temp;
        }
        String[] quote1 = getQuote(words1), quote2 = getQuote(words2);
        List<String> list1 = StringFunctions.stringToList(quote1), list2 = StringFunctions.stringToList(quote2);
        removeSameWords(list1, list2);
        int number = list1.size() + list2.size();
        double result = sigmoid(-1*number);
        return result;
    }

    public static String[] getQuote(String[] words){
        int i = 0;
        List<String> list = new ArrayList<>();
        String[] result;
        while(!words[i].substring(0, 1).contains("\"")){
            i++;
        }
        i++;
        while(!words[i].substring(0, 1).contains("\"")){
            list.add(words[i]);
            i++;
        }
        result = StringFunctions.listToString(list);
        return result;
    }

    public static int countQuotes(String[] words1){
        int count = 0;
        for(int i = 0; i < words1.length; i++){
            if(words1[i].contains("\"")){
                count++;
            }
        }
        return count;
    }
}
