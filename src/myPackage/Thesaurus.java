package myPackage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
public class Thesaurus {
    public static boolean is_setup = false;
    public static JSONObject[] json;
    public static void setup(){
        createJSON();
        is_setup = true;
    }

    public static void createJSON(){
        String file = "thesaurus-master/thesaurus-master/en_thesaurus.jsonl";
        json = Data.getJSON(file);
        String test_word = "battery";
        String key = test_word + "_1";
    }
    
    public static String[] searchSynonyms(String word){
        if(is_setup == false)
            setup();
        String my_word_key = word + "_1";
        Object temp;
        List<JSONArray> matches = new ArrayList<>();
        Object result;
        for(int i = 0; i < json.length; i++){

            temp = json[i].get("key");
            String tempString = (String) temp;
            if(i > 60 && i < 65)
                System.out.println("<"+temp+"><"+my_word_key+">");
            if(tempString.contains(word)){
                matches.add((JSONArray) json[i].get("synonyms"));
                System.out.println("found a match");
                break;
            }
        }
        String[][] setsOfSynonyms = new String[matches.size()][];
        for(int i = 0; i < matches.size(); i++){
            Object[] array = matches.get(i).toArray();
            setsOfSynonyms[i] = new String[array.length];
            for(int j = 0; j < array.length; j++)
                setsOfSynonyms[i][j] = (String)array[j];
        }

        String[] words;
        int length = 0;
        for(int i = 0; i < setsOfSynonyms.length; i++)
            length += setsOfSynonyms[i].length;
        words = new String[length];
        int index = 0;
        for(int i = 0; i < setsOfSynonyms.length; i++){
            for(int j = 0; j < setsOfSynonyms[i].length; j++, index++){
                words[index] = setsOfSynonyms[i][j];
            }
        }
        String splitWords[][] = new String[words.length][];
        for(int i = 0; i < words.length; i++)
        splitWords[i] = words[i].split(" ");
        length = 0;
        for(int i = 0; i < splitWords.length; i++)
            length += splitWords[i].length;
        words = new String[length];
        index = 0;
        for(int i = 0; i < splitWords.length; i++){
            for(int j = 0; j < splitWords[i].length; j++, index++){
                words[index] = splitWords[i][j];
            }
        }

        return words;
    }

    public static boolean isSynonym(String s1, String s2){
        if(s1.equals(s2))
            return false;
        String synonyms[] = searchSynonyms(s1);
        for(int i = 0; i < synonyms.length; i++){
            if(synonyms[i].equals(s2))
                return true;
        }
        synonyms = searchSynonyms(s2);
        for(int i = 0; i < synonyms.length; i++){
            if(synonyms[i].equals(s1))
                return true;
        }
        return false;
    }


}
