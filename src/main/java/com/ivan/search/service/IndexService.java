package com.ivan.search.service;

import cn.hutool.core.collection.ListUtil;
import com.ivan.search.component.MyIkAnalyzer;
import com.ivan.search.vo.CprVo;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * 索引服务
 *
 * @author cuiyingfan
 * @date 2022/04/02
 */
@Service
public class IndexService {
    @Value("${sqlite.indexpath}")
    private String luceneIndexPath;

    @Autowired
    private CprMainService cprService;
    public void createIndex() throws IOException {
        List<String> noAnalyzedTitle = ListUtil.list(false, "【药理分类】", "【相关时讯】", "临床指南", "注射剂配伍", "检验值专论", "用药教育", "超说明书用药专论（Off-Label Drug Facts）");
        List<CprVo> list = cprService.listCpr();
        // 创建文档的集合
        Collection<Document> docs = new ArrayList<>();
        Float score;
        String content;
        Random random = new Random();
        for (CprVo cpr : list) {
            if (!noAnalyzedTitle.contains(cpr.getCprPhname())) {
                content = cpr.getCprContent();
                if ("其他临床应用参考".equals(cpr.getCprPhname())) {
//                    content = Regex.Replace(content, "([0-9]+)=", "")
                }
//
//                content.AppendFormat("{0} ", cpr.getCprPhname());
//                sbcontent.Append(content);
            }
            // 创建文档对象
            Document doc = new Document();
            //StringField会创建索引，但是不会被分词，TextField，即创建索引又会被分词。
            doc.add(new StoredField("cprid", cpr.getCprid() + ""));
            doc.add(new TextField("title", cpr.getCprTitle(), Field.Store.YES));
            doc.add(new TextField("content", cpr.getCprContent(), Field.Store.YES));
            score = Float.valueOf(random.nextInt(10) + "");
            //用于追加分值，实现自定义影响最后的计分结果
            doc.add(new NumericDocValuesField("score_sort", NumericUtils.floatToSortableInt(score)));
            //用于存储计分结果
            doc.add(new StoredField("score_store", score));
            docs.add(doc);
        }

        // 索引目录类,指定索引在硬盘中的位置，我的设置为D盘的indexDir文件夹
        Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(luceneIndexPath));
        // 引入IK分词器
        MyIkAnalyzer analyzer = new MyIkAnalyzer();
        // 索引写出工具的配置对象，这个地方就是最上面报错的问题解决方案
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        //自定义索引中分词的权重及打分情况；如果配置了这个，那么 MyCustomScoreQuery 就会失效
//        conf.setSimilarity(new MySimilarity());
        // 设置打开方式：OpenMode.APPEND 会在索引库的基础上追加新索引。OpenMode.CREATE会先清空原来数据，再提交新的索引
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        // 创建索引的写出工具类。参数：索引的目录和配置信息
        IndexWriter indexWriter = new IndexWriter(directory, conf);
        //删除之前索引
        indexWriter.deleteAll();
        // 把文档集合交给IndexWriter
        indexWriter.addDocuments(docs);
        // 提交
        indexWriter.commit();
    }
}
