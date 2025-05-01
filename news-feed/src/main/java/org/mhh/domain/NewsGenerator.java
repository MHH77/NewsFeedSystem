package org.mhh.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @auther:MHEsfandiari
 */

public class NewsGenerator {

    private static final List<String> WORDS = Collections.unmodifiableList(Arrays.asList(
            "up", "down", "rise", "fall", "good", "bad", "success", "failure", "high", "low"
    ));

    private static final int[] CUMULATIVE_PRIORITY_WEIGHTS = {
            30, 50, 60, 70, 78, 85, 91, 95, 98, 100
    };
    private static final int TOTAL_WEIGHT = CUMULATIVE_PRIORITY_WEIGHTS[CUMULATIVE_PRIORITY_WEIGHTS.length - 1];

    public NewsItem generateNewsItem() {
        String headline = generateRandomHeadline();
        int priority = generateRandomPriority();
        return new NewsItem(headline, priority);
    }

    private String generateRandomHeadline() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int numberOfWords = random.nextInt(3, 6);

        return IntStream.range(0, numberOfWords)
                .mapToObj(i -> WORDS.get(random.nextInt(WORDS.size())))
                .collect(Collectors.joining(" "));
    }

    private int generateRandomPriority() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomWeight = random.nextInt(TOTAL_WEIGHT);

        for (int i = 0; i < CUMULATIVE_PRIORITY_WEIGHTS.length; i++) {
            if (randomWeight < CUMULATIVE_PRIORITY_WEIGHTS[i]) {
                return i;
            }
        }
        // This fallback should be outside the loop
        return CUMULATIVE_PRIORITY_WEIGHTS.length - 1; // Should ideally not be reached
    }

    public static void main(String[] args) {
        NewsGenerator generator = new NewsGenerator();
        System.out.println("Generating 10 random news items:");
        for (int i = 0; i < 10; i++) {
            System.out.println(generator.generateNewsItem());
        }

        System.out.println("\nGenerating 1000 items to check priority distribution (approx):");
        int[] counts = new int[10];
        for (int i = 0; i < 1000; i++) {
            counts[generator.generateRandomPriority()]++;
        }
        for (int i = 0; i < counts.length; i++) {
            System.out.printf("Priority %d: %d times%n", i, counts[i]);
        }
    }
}