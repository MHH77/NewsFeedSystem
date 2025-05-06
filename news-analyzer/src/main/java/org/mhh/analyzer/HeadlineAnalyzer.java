package org.mhh.analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @auther:MHEsfandiari
 */

public class HeadlineAnalyzer {
    private final Set<String> positiveKeywords;
    private final Set<String> negativeKeywords;

    public HeadlineAnalyzer() {
        this.positiveKeywords = new HashSet<>(Arrays.asList(
                "good", "great", "excellent", "positive", "success", "improvement", "advancement", "benefit", "win", "happy"
        ));
        this.negativeKeywords = new HashSet<>(Arrays.asList(
                "bad", "terrible", "poor", "negative", "failure", "problem", "loss", "decline", "lose", "sad", "crisis"));
    }

    public AnalysisResult analyze(String headline) {
        if (headline == null || headline.trim().isEmpty()) {
            return AnalysisResult.NEUTRAL;
        }

        String lowerCaseHeadline = headline.toLowerCase();
        boolean foundPositive = false;
        boolean foundNegative = false;

        for (String keyword : positiveKeywords) {
            if (lowerCaseHeadline.contains(keyword)) {
                foundPositive = true;
                break;
            }
        }

        for (String keyword : negativeKeywords) {
            if (lowerCaseHeadline.contains(keyword)) {
                foundNegative = true;
                break;
            }
        }

        if (foundPositive && !foundNegative) {
            return AnalysisResult.POSITIVE;
        } else if (foundNegative && !foundPositive) {
            return AnalysisResult.NEGATIVE;
        } else {
            return AnalysisResult.NEUTRAL; // Includes cases where both or neither are found
        }
    }

    public enum AnalysisResult {
        POSITIVE, NEGATIVE, NEUTRAL
    }
}