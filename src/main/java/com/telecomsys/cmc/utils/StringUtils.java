package com.telecomsys.cmc.utils;

import java.util.List;

/**
 * Utility for string processing.
 */
public final class StringUtils {

    /**
     * Constructor - private to prevent creation.
     */
    private StringUtils() {
    }

    /**
     * Utility to convert List of Strings to CSV string.
     *
     * @param inputStringList Input List of strings
     * @return CSV string or null if null or empty list.
     */
    public static String convertStringListToCSV(List<String> inputStringList) {
        if (inputStringList == null || inputStringList.size() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (String inputString : inputStringList) {
            sb.append(inputString).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

}
