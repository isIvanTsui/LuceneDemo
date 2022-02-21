package com.medicom.search.controller;

import cn.hutool.core.util.NumberUtil;
import com.medicom.search.component.MyCustomScoreQuery;
import com.medicom.search.component.MyIkAnalyzer;
import com.medicom.search.component.MyTokenFilter;
import com.medicom.search.service.CprContentService;
import com.medicom.search.vo.CprVo;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 *
 * @author cuiyingfan
 * @date 2022/02/15
 */
@Controller
public class SearchController {
    @Value("${lucene.indexpath}")
    private String luceneIndexPath;

    @Autowired
    private CprContentService contentService;

    /**
     * 搜索
     *
     * @param keyword   关键字
     * @param pageIndex 当前页
     * @param pageSize  每页显示条数
     * @return {@link List}
     * @throws Exception 异常
     */
    @PostMapping("/search")
    @ResponseBody
    public List search(String keyword, Integer pageIndex, Integer pageSize) throws Exception {
        if (pageIndex == null) {
            pageIndex = 1;
        }
        if (pageSize == null) {
            pageSize = 100;
        }
        Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(luceneIndexPath));
        // 索引读取工具
        IndexReader reader = DirectoryReader.open(directory);
        // 索引搜索工具
        IndexSearcher searcher = new IndexSearcher(reader);
        String[] str = {"title", "content"};
        //设置权重
        Map<String, Float> boosts = new HashMap<>();
        boosts.put("title", 10F);
        boosts.put("content", 1F);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(str, new MyIkAnalyzer(), boosts);
        // 创建查询对象
        Query query = parser.parse(keyword);
        System.out.println("查询语句为:" + query);
        //自定义影响算分的设置
        MyCustomScoreQuery scoreQuery = new MyCustomScoreQuery(query, new FunctionQuery(new FloatFieldSource("score_sort")));
        // 获取前100条记录
        TopDocs topDocs = searcher.search(scoreQuery, 100);
        //执行查询
        List list = executeSearch(scoreQuery, pageIndex, pageSize);
        return list;
    }

    private List executeSearch(Query query, int pageIndex, int pageSize) throws Exception {
        Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(luceneIndexPath));
        // 索引读取工具
        IndexReader reader = DirectoryReader.open(directory);
        //获取某个域分词后的terms
        Terms terms = MultiFields.getTerms(reader, "content");
        TermsEnum iterator = terms.iterator();
        BytesRef bRef = null;
        int rank = 1;
        while ((bRef = iterator.next()) != null) {
            String oneTerm = new String(bRef.bytes, bRef.offset, bRef.length, Charset.forName("utf-8"));
            //docFreq：当前这个term在几个document里面出现了
            System.out.println("第" + rank + "个term是:" + oneTerm + ",共在:" + iterator.docFreq()
                    + "个文档出现,该term在所有文档中共出现:"
                    + reader.totalTermFreq(new Term("content", bRef)) + "次");
            rank++;
        }
        // 索引搜索工具
        IndexSearcher searcher = new IndexSearcher(reader);
        // 设置自定义索引中分词的权重及打分情况
//        searcher.setSimilarity(new MySimilarity());
        // 分页查询
        ScoreDoc lastSd = getLaScoreDoc(pageIndex, pageSize, query, searcher);
        TopDocs topDocs = searcher.searchAfter(lastSd, query, pageSize);
        // 获取总条数
        System.out.println("本次搜索共找到" + topDocs.totalHits + "条数据");

        //高亮显示
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
        //高亮后的段落范围在100字内
        Fragmenter fragmenter = new SimpleFragmenter(100);
        highlighter.setTextFragmenter(fragmenter);

        // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        List<CprVo> list = new ArrayList<>();
        for (ScoreDoc scoreDoc : scoreDocs) {
            // 取出文档编号
            int docID = scoreDoc.doc;
            // 根据编号去找文档
            Document doc = reader.document(docID);
            CprVo cpr = new CprVo();
            String content = doc.get("content");
            cpr.setCprid(doc.get("cprid"));
            cpr.setCprTitle(doc.get("title"));
            cpr.setCprContent(content);
            cpr.setScore(scoreDoc.score);

            //搜索过滤content
            /*StringBuilder builder = new StringBuilder();
            MyTokenFilter myTokenFilter = new MyTokenFilter(new MyIkAnalyzer().tokenStream("xxx", content));
            CharTermAttribute charTermAttribute = myTokenFilter.addAttribute(CharTermAttribute.class);
            myTokenFilter.reset();
            while (myTokenFilter.incrementToken()) {
                builder.append(charTermAttribute);
            }
            cpr.setCprContent(builder.toString());*/
            //处理高亮字段显示
            String title = highlighter.getBestFragment(new MyIkAnalyzer(), "title", doc.get("title"));
            if (title == null) {
                title = cpr.getCprTitle();
            }
            cpr.setCprTitle(title);
            content = highlighter.getBestFragment(new MyIkAnalyzer(), "content", content);
            if (content == null) {
                content = cpr.getCprContent();
            }
            cpr.setCprContent(content);
            //根据高亮配置获取最佳摘要
            TokenStream tStream = new MyIkAnalyzer().tokenStream("xxx", new StringReader(content));
            String bestFragment = highlighter.getBestFragment(tStream, content);
            System.out.println("最佳摘要:" + bestFragment);
            cpr.setBestFragment(bestFragment);
            list.add(cpr);
            System.out.println(docID + ":(score:" + scoreDoc.score + ")[cprid:" + doc.get("cprid") + "][title:" + doc.get("title")
                    + "][content:" + doc.get("content") + "]");
        }
        return list;
    }

    private ScoreDoc getLaScoreDoc(int pageIndex, int pageSize, Query query, IndexSearcher searcher) throws IOException {
        if(pageIndex == 1){
            return null;
        }
        //这里我们不能像mysql中那样每次都只取pageSize条数据，这里第一次取pageSize条，第二次取2*pageSize条，只能这样
        int num = pageSize * (pageIndex - 1);
        TopDocs tds = searcher.search(query, num);
        return tds.scoreDocs[num - 1];
    }

    @GetMapping("/home")
    public String home() {
        return "index.html";
    }
}
