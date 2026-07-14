package com.shopease.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * URL slug helpers.
 */
public final class SlugUtil {

    private SlugUtil() {}

    public static String slugify(String input) {
        if (input == null || input.isBlank()) {
            return "item-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return normalized.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
