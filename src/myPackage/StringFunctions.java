package myPackage;

import java.util.*;
public class StringFunctions {
    public static String[] listToString(List<String> list){
        Object[] array = list.toArray();
        String[] s = new String[array.length];
        s = new String[array.length];
        for(int i = 0; i < array.length; i++)
            s[i] = (String)array[i];
        return s;
    }
    public static List<String> stringToList(String[] s){
        List<String> list = new ArrayList<>();
        for(String string: s)
            list.add(string);
        return list;
    }
}
