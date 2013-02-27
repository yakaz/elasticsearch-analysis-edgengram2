Elasticsearch Edge NGram 2 Filter
=================================

The EdgeNGramFilter2 analysis plugin provides with an extension over the mainstream EdgeNGramTokenFilter.

Installation
------------

Simply run at the root of your ElasticSearch v0.20.2+ installation:

	bin/plugin -install com.yakaz.elasticsearch.plugins/elasticsearch-analysis-edgengram2/1.1.0

This will download the plugin from the Central Maven Repository.

For older versions of ElasticSearch, you can still use the longer:

	bin/plugin -url http://oss.sonatype.org/content/repositories/releases/com/yakaz/elasticsearch/plugins/elasticsearch-analysis-edgengram2/1.1.0/elasticsearch-analysis-edgengram2-1.1.0.zip install elasticsearch-analysis-edgengram2

In order to declare this plugin as a dependency, add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.yakaz.elasticsearch.plugins</groupId>
    <artifactId>elasticsearch-analysis-edgengram2</artifactId>
    <version>1.1.0</version>
</dependency>
```

Version matrix:

	-------------------------------------------------
	| HashSplitter Analysis Plugin | ElasticSearch  |
	-------------------------------------------------
	| master                       | 0.90 -> master |
	-------------------------------------------------
	| 1.1.0                        | 0.90 -> master |
	-------------------------------------------------
	| 1.0.0                        | 0.19 -> 0.20   |
	-------------------------------------------------

Description
-----------

This plugin exposes an extension over the Edge NGram filter, packaged as an ElasticSearch 0.19.0+ plugin.

See [Lucene EdgeNGramTokenFilter JavaDoc][ENGJavadoc] for more information about the base functionality.

Added features
--------------

Currently there is a single added feature:

* __Preserve input positions__

  When the filter splits the source token, it generates additional tokens, usually each of them takes a new position on its own.
  This breaks the structure of the source stream by making two sibling tokens far away, position-wise.
  Eg. `"foo bar"` becomes `0:f 1:fo 2:foo 3:b 4:ba 5:bar`.

  This new feature permits to output all grams tokens at the same position as the source token, hence `"foo bar"` will yield `0:f,fo,foo 1:b,ba,bar`.

  This is particularly useful when merging with other analysis, using the [Combo Analyzer][Combo], to prevent position jitter, or when using phrase queries for suggestions.

  Please always be aware of the impact of terms positions with regard to your queries.

Configuration
-------------

The plugin provides you with the `edge_ngram_2` token filter type.
It accepts the [same list of parameters as the `edge_ngram` token filter][ENGEsDoc], plus:

* `preserve_positions`: `false` by default.


See also
--------

[ElasticSearch EdgeNGramFilter doc][ENGEsDoc]

[Lucene EdgeNGramTokenFilter JavaDoc][ENGJavadoc]

[Combo Analyzer plugin][Combo]

[Word Delimiter Filter 2 plugin][WDF]



[ENGEsDoc]: http://www.elasticsearch.org/guide/reference/index-modules/analysis/edgengram-tokenfilter.html
    (ElasticSearch EdgeNGramFilter doc)

[ENGJavadoc]: http://lucene.apache.org/core/4_0_0/analyzers-common/org/apache/lucene/analysis/ngram/EdgeNGramTokenFilter.html
    (Lucene EdgeNGramTokenFilter JavaDoc)

[Combo]: https://github.com/yakaz/elasticsearch-analysis-combo/
    (Combo Analyzer plugin)

[WDF]: https://github.com/yakaz/elasticsearch-analysis-worddelimiter2/
    (Word Delimiter 2 plugin)
