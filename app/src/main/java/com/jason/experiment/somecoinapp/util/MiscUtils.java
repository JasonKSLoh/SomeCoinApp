package com.jason.experiment.somecoinapp.util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * MiscUtils
 * Created by jason.
 */
public class MiscUtils {

    public static ArrayList<String> separatedStringToArrayList(String s, String separator){
        ArrayList<String> returnedList = new ArrayList<>();
        if(s == null){
            return null;
        }
        if(s.isEmpty()){
            return returnedList;
        }
        String[] stringList = s.split(separator);
        Collections.addAll(returnedList, stringList);
        return returnedList;
    }

    public static String ArrayListToSeparatedString(ArrayList<String> stringList, String separator){
        StringBuilder sb = new StringBuilder();
        for(String s: stringList){
            sb.append(s).append(separator);
        }
        return sb.toString();
    }
}
