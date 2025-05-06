package org.mhh.common;

/**
 * @auther:MHEsfandiari
 */

import org.mhh.analyzer.HeadlineAnalyzer;

import java.time.Instant;

public class AnalyzedNewsItem {
    private final NewsItem originalItem;
    private final HeadlineAnalyzer.AnalysisResult result;
    private final Instant analysisTimestamp;

    public AnalyzedNewsItem(NewsItem originalItem, HeadlineAnalyzer.AnalysisResult result) {
        this.originalItem = originalItem;
        this.result = result;
        this.analysisTimestamp = Instant.now();
    }

    public NewsItem getOriginalItem() {
        return originalItem;
    }

    public HeadlineAnalyzer.AnalysisResult getResult() {
        return result;
    }

    public Instant getAnalysisTimestamp() {
        return analysisTimestamp;
    }

    @Override
    public String toString() {
        return "AnalyzedItem @ " + analysisTimestamp +
                ": Result=" + result +
                ", Headline='" + (originalItem != null ? originalItem.getHeadline() : "N/A") + "'";
    }
}