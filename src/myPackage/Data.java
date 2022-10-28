package myPackage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;



public class Data {
    public static boolean createFile(String fileName) {
        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                return true;
            } else {
                System.out.println("File already exists.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeJSONFile(String fileName, String write){
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(write);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
    }

    public static void writeStringToFile(String s){

    }

    public static String[] readFileForJson(String fileName){
        Scanner scan;
        File file = new File(fileName);
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<String> list = new ArrayList<>();
        while(scan.hasNextLine()){
            list.add(scan.nextLine());
        }
        String[] result = new String[list.size()];
        for(int i = 0; i < list.size(); i++)
            result[i] = list.get(i);
        return result;
    }

    public static JSONObject[] getJSON(String fileName){
        String myData[] = readFileForJson(fileName);
        Object obj = null;
        JSONObject jsons[] = new JSONObject[myData.length];
        for(int i = 0; i < myData.length; i++){
            try {
                obj = new JSONParser().parse(myData[i]);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            JSONObject jo = (JSONObject) obj;
            jsons[i] = jo;
        }
        return jsons;
    }

    public static void writeToFile(String s) {
        try {
            FileWriter myWriter = new FileWriter("filename.txt");
            myWriter.write(s);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
