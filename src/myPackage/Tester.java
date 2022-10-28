package myPackage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;

import java.util.*;


public class Tester {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setup() {
        //rate = new LinearRate1();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getThesaurusWord(){
        String word = "police";
        String[] synonyms = Thesaurus.searchSynonyms(word);
        for(String s: synonyms)
        System.out.println(s);
    }

    @Test
    public void testRemoveSameWord(){
        String s1 = "Local police authorities are treating the explosion as a criminal matter and nothing has been ruled out .";
        String s2 = "Acting New Haven Police Chief Francisco Ortiz said police were treating the explosion as a criminal matter .";
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        String[] words1 = FeatureMaker.stringToWords(s1);
        String[] words2 = FeatureMaker.stringToWords(s2);
        List<String> list1 = new ArrayList<>(), list2 = new ArrayList<>();
        for(String s: words1)
            list1.add(s);
        for(String s: words2)
            list2.add(s);
        FeatureMaker.removeSameWords(list1, list2);
        /*
        for(String s: words1)
            System.out.print(s + ", ");
        System.out.println();
        for(String s: words2)
            System.out.print(s + ", ");
         */
        System.out.println(list1);
        System.out.println(list2);
    }
    @Test
    public void testAbbreviations(){
        String s1 = "Stout previously worked for General Electric subsidiary GE Capital Service Inc . , where he was vice president and chief technology and information officer .";
        String s2 = "Stout comes to Sprint from GE Capital , where he served as chief technology and information officer .";
        double result = FeatureMaker.searchAbbreviations(s1, s2);
        System.out.println(result);
    }
}