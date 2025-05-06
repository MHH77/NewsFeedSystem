package org.mhh.analyzer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @auther:MHEsfandiari
 */

public class HeadlineAnalyzer {

    private static final Set<String> POSITIVE_WORDS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("up", "rise", "good", "success", "high"))
    );

    public boolean isPositive(String headline) {
        if (headline == null || headline.trim().isEmpty()) {
            return false; // Or handle as an error, but false seems reasonable
        }

        // Split headline into words, converting to lowercase for case-insensitive comparison
        String[] words = headline.toLowerCase().split("\\s+"); // Split by whitespace

        if (words.length == 0) {
            return false;
        }

        int positiveWordCount = 0;
        for (String word : words) {
            // Remove potential punctuation if needed, although the example words are simple
            // String cleanWord = word.replaceAll("[^a-zA-Z]", ""); // Optional cleanup
            if (POSITIVE_WORDS.contains(word)) {
                positiveWordCount++;
            }
        }

        // Check if more than 50% of words are positive
        // Using multiplication avoids floating-point arithmetic
        return positiveWordCount * 2 > words.length;
    }

    // Optional: Add a main method for quick testing
    public static void main(String[] args) {
        HeadlineAnalyzer analyzer = new HeadlineAnalyzer();

        String test1 = "UP rise good"; // 3 positive / 3 total -> true
        String test2 = "good success DOWN fall"; // 2 positive / 4 total -> false (exactly 50%)
        String test3 = "HIGH success rise fall low"; // 3 positive / 5 total -> true
        String test4 = "bad failure low"; // 0 positive / 3 total -> false
        String test5 = "up"; // 1 positive / 1 total -> true
        String test6 = "down success"; // 1 positive / 2 total -> false (exactly 50%)
        String test7 = "High Rise Success"; // Case-insensitive check -> true
        String test8 = ""; // Empty -> false
        String test9 = "   "; // Whitespace only -> false

        System.out.printf("'%s' is positive: %s%n", test1, analyzer.isPositive(test1));
        System.out.printf("'%s' is positive: %s%n", test2, analyzer.isPositive(test2));
        System.out.printf("'%s' is positive: %s%n", test3, analyzer.isPositive(test3));
        System.out.printf("'%s' is positive: %s%n", test4, analyzer.isPositive(test4));
        System.out.printf("'%s' is positive: %s%n", test5, analyzer.isPositive(test5));
        System.out.printf("'%s' is positive: %s%n", test6, analyzer.isPositive(test6));
        System.out.printf("'%s' is positive: %s%n", test7, analyzer.isPositive(test7));
        System.out.printf("'%s' is positive: %s%n", test8, analyzer.isPositive(test8));
        System.out.printf("'%s' is positive: %s%n", test9, analyzer.isPositive(test9));
    }
}