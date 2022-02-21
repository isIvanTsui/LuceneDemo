package com.medicom.search.component;

import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 我自定义分供应商
 *
 * @author cuiyingfan
 * @date 2022/02/18
 */
public class MyCustomScoreProvider extends CustomScoreProvider {
    /**
     * 域
     */
    private Fields fields = null;

    public MyCustomScoreProvider(LeafReaderContext context) {
        super(context);
        LeafReader reader = context.reader();
        try {
            Fields fields = reader.fields();
            this.fields = fields;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
        System.out.println("doc:" + doc + ", subQueryScore:" + subQueryScore + ", valsrcScore:" + valSrcScore);
        //算分修改为：原始分值+自定义算分列的分值
        return subQueryScore + valSrcScore;
    }
}
