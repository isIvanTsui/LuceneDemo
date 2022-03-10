package com.ivan.search.component;

import org.apache.lucene.analysis.Analyzer;

/**
 * 我动力学分析仪
 *
 * @author cuiyingfan
 * @date 2022/02/15
 */
public class MyIkAnalyzer extends Analyzer {
    private boolean useSmart = false;

    public MyIkAnalyzer() {
        this(false);
    }

    public MyIkAnalyzer(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }

    public boolean isUseSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        MyIkTokenizer myIKTokenizer = new MyIkTokenizer(this.useSmart);
        return new TokenStreamComponents(myIKTokenizer);
    }
}
