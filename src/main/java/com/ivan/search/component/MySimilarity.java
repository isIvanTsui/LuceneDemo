package com.ivan.search.component;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

/**
 * 定义索引中分词的权重及打分情况
 *
 * @author cuiyingfan
 * @date 2022/02/16
 */
public class MySimilarity extends TFIDFSimilarity {


    @Override
    public float tf(float freq) {
        return freq > 0 ? 1.001f - (1.0F / (freq + 1000)) : 0;
    }

    @Override
    public float idf(long docFreq, long docCount) {
        return 1.0f;
    }

    @Override
    public float lengthNorm(FieldInvertState state) {
        return 0;
    }

    @Override
    public float decodeNormValue(long norm) {
        return 0;
    }

    @Override
    public long encodeNormValue(float f) {
        return 0;
    }


    @Override
    public float sloppyFreq(int distance) {
        return 1.0f / (distance + 1);
    }

    @Override
    public float scorePayload(int doc, int start, int end, BytesRef payload) {
        return 1.0f;
    }

    @Override
    public float coord(int overlap, int maxOverlap) {
        if (overlap > 1) {
            return 1000 * (overlap - 1);
        } else if (overlap == 1) {
            return 1f;
        } else {
            return 0f;
        }
    }

    @Override
    public float queryNorm(float sumOfSquaredWeights) {
        return 0;
    }
}
