package com.ivan.search.component;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义过滤器
 *
 * @author cuiyingfan
 * @date 2022/02/21
 */
public class MyTokenFilter extends TokenFilter {

    private Map<String, String> courtesyMap = new HashMap<>();
    private CharTermAttribute charTermAttribute;

    public MyTokenFilter(TokenStream input) {
        super(input);
        this.charTermAttribute = this.addAttribute(CharTermAttribute.class);
        courtesyMap.put("dr", "doctor");
        courtesyMap.put("mr", "mister");
        courtesyMap.put("mrs", "miss");
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }

        String term = this.charTermAttribute.toString();
        if (courtesyMap.containsKey(term)) {
            this.charTermAttribute.setEmpty().append(this.courtesyMap.get(term));
        }

        return true;
    }
}