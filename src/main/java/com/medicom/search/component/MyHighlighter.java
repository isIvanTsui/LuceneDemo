package com.medicom.search.component;

import cn.hutool.core.util.ArrayUtil;
import lombok.Data;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.vectorhighlight.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自定义
 *
 * @author cuiyingfan
 * @date 2022/02/16
 */
public class MyHighlighter extends FastVectorHighlighter {
    private final FragListBuilder fragListBuilder;
    private final FragmentsBuilder fragmentsBuilder;

    public MyHighlighter(boolean phraseHighlight, boolean fieldMatch, FragListBuilder fragListBuilder, FragmentsBuilder fragmentsBuilder) {
        super(phraseHighlight, fieldMatch);
        this.fragListBuilder = fragListBuilder;
        this.fragmentsBuilder = fragmentsBuilder;
    }


    public String GetBestFragment(FieldQuery fieldQuery, IndexReader reader, int docId, String fieldName, int fragCharSize, Map<String, Double> queryWords) throws IOException {
        FieldTermStack fieldTermStack = new FieldTermStack(reader, docId, fieldName, fieldQuery);
        FieldPhraseList fieldPhraseList = new FieldPhraseList(fieldTermStack, fieldQuery, Integer.MAX_VALUE);
        FieldFragList fieldFragList = fragListBuilder.createFieldFragList(fieldPhraseList, fragCharSize);
        //// 新增逻辑，对其词进行权重，选出权重最高的一个片段
        List<FieldFragList.WeightedFragInfo> fragInfos = fieldFragList.getFragInfos();
        if (queryWords != null && fragInfos.size() > 1) {
            List<MyWeightedFragInfo> list = new ArrayList<>();
            for (FieldFragList.WeightedFragInfo fragInfo : fragInfos) {
                MyWeightedFragInfo myWeighted = new MyWeightedFragInfo(fragInfo, queryWords);
                list.add(myWeighted);
            }
            //根据关键字权重及以长度排序来取得最匹配的片段
            list.sort(Comparator.comparing(MyWeightedFragInfo::getWeight).reversed().thenComparing(MyWeightedFragInfo::getLength));
            MyWeightedFragInfo firstInfo = list.get(0);
            fragInfos.clear();
            fragInfos.add(firstInfo.WeightedFrag);
        }

        return fragmentsBuilder.createFragment(reader, docId, fieldName, fieldFragList);
    }

    @Data
    class MyWeightedFragInfo {
        private FieldFragList.WeightedFragInfo WeightedFrag;
        private double weight;
        private int length;

        public MyWeightedFragInfo(FieldFragList.WeightedFragInfo wfinfo, Map<String, Double> queryWords) {
            List<String> words = queryWords.entrySet().stream().map(s -> s.getKey().toUpperCase()).collect(Collectors.toList());
            List<Double> weights = queryWords.entrySet().stream().map(s -> s.getValue()).collect(Collectors.toList());
            List<String> highWightWords = queryWords.entrySet().stream().filter(s -> s.getValue() >= 5).map(s -> s.getKey().toUpperCase()).collect(Collectors.toList());
            WeightedFrag = wfinfo;
//            String[] keys = wfinfo.toString().toUpperCase().split("=")[1].split("/")[0].split(new char[]{")"}, StringSplitOptions.RemoveEmptyEntries);
//            for (int i = 0; i < keys.length; i++) {
//                keys[i] = keys[i].split(new char[]{'('}, StringSplitOptions.RemoveEmptyEntries)[0];
//            }
//            keys = ArrayUtil.distinct(keys);
//            for (String key : keys) {
//                //如果片段关键字包含了高权重的关键字，那么此片段关键字也采用此高权重关键字的权重值。
//                string hword = highWightWords.FirstOrDefault(m = > key.Contains(m));
//                if (!string.IsNullOrEmpty(hword)) {
//                    length += key.Length;
//                    weight += queryWords[hword];
//                } else {
//                    int keyIndex = words.FindIndex(m = > m == key);
//                    if (keyIndex > -1) {
//                        length += words[keyIndex].Length;
//                        weight += weights[keyIndex];
//                    }
//                }
//            }
        }
    }
}
