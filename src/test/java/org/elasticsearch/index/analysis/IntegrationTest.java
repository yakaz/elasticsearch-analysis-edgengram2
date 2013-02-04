package org.elasticsearch.index.analysis;

import org.testng.annotations.Test;

@Test
public class IntegrationTest extends BaseESTest {

    public static final String ANALYZER = "configured_analyzer";

    @Test
    public void testAnalysis() {
        assertAnalyzesTo(ANALYZER, "abcde f ghi",
                new String[]{"de", "cde", "hi", "ghi"},
                new int[]{     3,      2,    9,     8},
                new int[]{     5,      5,   11,    11},
                null,
                new int[]{     1,      0,    2,     0});
    }

}
