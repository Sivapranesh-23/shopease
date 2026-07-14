package com.shopease.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugUtilTest {

    @Test
    void producesStableUrlSafeSlugs() {
        assertThat(SlugUtil.slugify("  Héritage Leather — No. 2  "))
                .isEqualTo("heritage-leather-no-2");
    }

    @Test
    void collapsesRepeatedWhitespaceAndDashes() {
        assertThat(SlugUtil.slugify("A   simple---thing"))
                .isEqualTo("a-simple-thing");
    }
}
