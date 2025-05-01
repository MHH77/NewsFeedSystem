package org.mhh.domain;

import lombok.Value;

import java.io.Serializable;
import java.util.Objects;

/**
 * @auther:MHEsfandiari
 */

@Value
public class NewsItem implements Serializable {

    private static final long serialVersionUID = 1L;

    String headline;
    int priority;

    public NewsItem(String headline, int priority) {
        if (headline == null || headline.trim().isEmpty()) {
            throw new IllegalArgumentException("Headline cannot be null or empty.");
        }
        if (priority < 0 || priority > 9) {
            throw new IllegalArgumentException("Priority must be between 0 and 9.");
        }
        this.headline = headline;
        this.priority = priority;
    }

}