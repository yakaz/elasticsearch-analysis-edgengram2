/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGram2TokenFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;


/**
 *
 */
public class EdgeNGram2TokenFilterFactory extends AbstractTokenFilterFactory {

    private final int minGram;

    private final int maxGram;

    private final EdgeNGram2TokenFilter.Side side;

    private final boolean preservePositions;

    @Inject
    public EdgeNGram2TokenFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        this.minGram = settings.getAsInt("min_gram", EdgeNGram2TokenFilter.DEFAULT_MIN_GRAM_SIZE);
        this.maxGram = settings.getAsInt("max_gram", EdgeNGram2TokenFilter.DEFAULT_MAX_GRAM_SIZE);
        this.preservePositions = settings.getAsBoolean("preserve_positions", EdgeNGram2TokenFilter.DEFAULT_PRESERVE_POSITIONS);
        this.side = EdgeNGram2TokenFilter.Side.getSide(settings.get("side", EdgeNGram2TokenFilter.DEFAULT_SIDE.getLabel()));
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new EdgeNGram2TokenFilter(tokenStream, side, minGram, maxGram, preservePositions);
    }
}
