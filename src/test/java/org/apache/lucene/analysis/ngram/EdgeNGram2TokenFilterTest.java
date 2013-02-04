package org.apache.lucene.analysis.ngram;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.io.StringReader;

/**
 * Tests {@link EdgeNGram2TokenFilter} for correctness.
 */
public class EdgeNGram2TokenFilterTest extends BaseTokenStreamTestCase {
    private TokenStream input;

    /*
     * Set a large position increment gap of 10 if the token is "largegap" or "/"
     */
    private final class LargePosIncTokenFilter extends TokenFilter {
        private CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
        private PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);

        protected LargePosIncTokenFilter(TokenStream input) {
            super(input);
        }

        @Override
        public boolean incrementToken() throws IOException {
            if (input.incrementToken()) {
                if (termAtt.toString().equals("largegap") || termAtt.toString().equals("/"))
                    posIncAtt.setPositionIncrement(10);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        input = new LargePosIncTokenFilter(new MockTokenizer(new StringReader("abcde / ABCDE"), MockTokenizer.WHITESPACE, false));
    }

    public void testInvalidInput() throws Exception {
        boolean gotException = false;
        try {
            new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 0, 0);
        } catch (IllegalArgumentException e) {
            gotException = true;
        }
        assertTrue(gotException);
    }

    public void testInvalidInput2() throws Exception {
        boolean gotException = false;
        try {
            new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 2, 1);
        } catch (IllegalArgumentException e) {
            gotException = true;
        }
        assertTrue(gotException);
    }

    public void testInvalidInput3() throws Exception {
        boolean gotException = false;
        try {
            new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, -1, 2);
        } catch (IllegalArgumentException e) {
            gotException = true;
        }
        assertTrue(gotException);
    }

    public void testFrontUnigram() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 1, 1);
        assertTokenStreamContents(tokenizer, new String[]{"a", "/", "A"}, new int[]{0, 6, 8}, new int[]{1, 7, 9}, new int[]{1, 1, 1});
    }

    public void testFrontUnigramPreserve() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 1, 1, true);
        assertTokenStreamContents(tokenizer, new String[]{"a", "/", "A"}, new int[]{0, 6, 8}, new int[]{1, 7, 9}, new int[]{1, 10, 1});
    }

    public void testBackUnigram() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.BACK, 1, 1);
        assertTokenStreamContents(tokenizer, new String[]{"e", "/", "E"}, new int[]{4, 6, 12}, new int[]{5, 7, 13}, new int[]{1, 1, 1});
    }

    public void testBackUnigramPreserve() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.BACK, 1, 1, true);
        assertTokenStreamContents(tokenizer, new String[]{"e", "/", "E"}, new int[]{4, 6, 12}, new int[]{5, 7, 13}, new int[]{1, 10, 1});
    }

    public void testOversizedNgrams() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 6, 6);
        assertTokenStreamContents(tokenizer, new String[0], new int[0], new int[0], new int[0]);
    }

    public void testOversizedNgramsPreserve() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 6, 6, true);
        assertTokenStreamContents(tokenizer, new String[0], new int[0], new int[0], new int[0]);
    }

    public void testFrontRangeOfNgrams() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 1, 3);
        assertTokenStreamContents(tokenizer, new String[]{"a","ab","abc", "/", "A", "AB", "ABC"}, new int[]{0,0,0, 6, 8,8,8}, new int[]{1,2,3, 7, 9,10,11}, new int[]{1,1,1, 1, 1,1,1});
    }

    public void testFrontRangeOfNgramsPreserve() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 1, 3, true);
        assertTokenStreamContents(tokenizer, new String[]{"a","ab","abc", "/", "A", "AB", "ABC"}, new int[]{0,0,0, 6, 8,8,8}, new int[]{1,2,3, 7, 9,10,11}, new int[]{1,0,0, 10, 1,0,0});
    }

    public void testBackRangeOfNgrams() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.BACK, 1, 3);
        assertTokenStreamContents(tokenizer, new String[]{"e","de","cde", "/", "E", "DE", "CDE"}, new int[]{4,3,2, 6, 12,11,10}, new int[]{5,5,5, 7, 13,13,13}, new int[]{1,1,1, 1, 1,1,1});
    }

    public void testBackRangeOfNgramsPreserve() throws Exception {
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.BACK, 1, 3, true);
        assertTokenStreamContents(tokenizer, new String[]{"e","de","cde", "/", "E", "DE", "CDE"}, new int[]{4,3,2, 6, 12,11,10}, new int[]{5,5,5, 7, 13,13,13}, new int[]{1,0,0, 10, 1,0,0});
    }

    public void testSmallTokenInStream() throws Exception {
        input = new MockTokenizer(new StringReader("abc de fgh"), MockTokenizer.WHITESPACE, false);
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 3, 3);
        assertTokenStreamContents(tokenizer, new String[]{"abc","fgh"}, new int[]{0,7}, new int[]{3,10}, new int[]{1,1});
    }

    public void testSmallTokenInStreamPreserve() throws Exception {
        input = new MockTokenizer(new StringReader("abc de fgh"), MockTokenizer.WHITESPACE, false);
        EdgeNGram2TokenFilter tokenizer = new EdgeNGram2TokenFilter(input, EdgeNGram2TokenFilter.Side.FRONT, 3, 3, true);
        assertTokenStreamContents(tokenizer, new String[]{"abc","fgh"}, new int[]{0,7}, new int[]{3,10}, new int[]{1,2});
    }

    public void testReset() throws Exception {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer(TEST_VERSION_CURRENT, new StringReader("abcde"));
        EdgeNGram2TokenFilter filter = new EdgeNGram2TokenFilter(tokenizer, EdgeNGram2TokenFilter.Side.FRONT, 1, 3);
        assertTokenStreamContents(filter, new String[]{"a","ab","abc"}, new int[]{0,0,0}, new int[]{1,2,3}, new int[]{1,1,1});
        tokenizer.reset(new StringReader("ABCDE"));
        assertTokenStreamContents(filter, new String[]{"A","AB","ABC"}, new int[]{0,0,0}, new int[]{1,2,3}, new int[]{1,1,1});
    }

    public void testResetPreserve() throws Exception {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer(TEST_VERSION_CURRENT, new StringReader("abcde"));
        EdgeNGram2TokenFilter filter = new EdgeNGram2TokenFilter(tokenizer, EdgeNGram2TokenFilter.Side.FRONT, 1, 3, true);
        assertTokenStreamContents(filter, new String[]{"a","ab","abc"}, new int[]{0,0,0}, new int[]{1,2,3}, new int[]{1,0,0});
        tokenizer.reset(new StringReader("ABCDE"));
        assertTokenStreamContents(filter, new String[]{"A","AB","ABC"}, new int[]{0,0,0}, new int[]{1,2,3}, new int[]{1,0,0});
    }
}
