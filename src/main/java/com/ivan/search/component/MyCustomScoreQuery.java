package com.ivan.search.component;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.search.Query;

import java.io.IOException;

/**
 * 我自定义分数查询
 *
 * @author cuiyingfan
 * @date 2022/02/18
 */
public class MyCustomScoreQuery extends CustomScoreQuery {
    public MyCustomScoreQuery(Query subQuery, FunctionQuery scoringQuery) {
        super(subQuery, scoringQuery);
    }

    @Override
    protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) throws IOException {
        return new MyCustomScoreProvider(context);
    }
}
