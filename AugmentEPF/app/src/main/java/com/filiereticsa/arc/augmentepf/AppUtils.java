package com.filiereticsa.arc.augmentepf;

/**
 * Created by Harpe-e on 11/06/2017.
 */

public class AppUtils {

    public static String[] concateneStringsArrays(String[] firstArray, String[] secondArray) {
        int firstArrayLength = firstArray.length;
        int secondArrayLength = secondArray.length;
        String[] concatenatedArray = new String[firstArrayLength + secondArrayLength];
        System.arraycopy(firstArray, 0, concatenatedArray, 0, firstArrayLength);
        System.arraycopy(secondArray, 0, concatenatedArray, firstArrayLength, secondArrayLength);
        return concatenatedArray;
    }
}
