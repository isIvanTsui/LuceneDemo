package com.ivan;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.dynamic.datasource.toolkit.CryptoUtils;
import com.ivan.search.component.MyIkAnalyzer;
import com.ivan.search.component.MyTokenFilter;
import com.ivan.search.service.CprContentService;
import com.ivan.search.service.CprMainService;
import com.ivan.search.vo.CprVo;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.sqlite.mc.SQLiteMCConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@SpringBootTest
class LuceneDemoApplicationTests {
    @Value("${sqlite.indexpath}")
    private String luceneIndexPath;


    @Autowired
    private CprContentService contentService;

    @Autowired
    private CprMainService cprService;


    /**
     * 创建索引
     *
     * @throws IOException ioexception
     */
    @Test
    void createIndex() throws IOException {
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

    /**
     * 分词
     *
     * @throws IOException ioexception
     */
    @Test
    void participle() throws IOException {
        String text = "慕课网是一个网站，我在西安火车站和咸阳飞机场游玩";
        StringReader sr = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(sr, true);
        Lexeme lex = null;
        while ((lex = ik.next()) != null) {
            System.out.println(lex.getLexemeText());
        }
    }

    /**
     * 分析仪
     *
     * @throws IOException ioexception
     */
    @Test
    void analyzer() throws IOException {
        MyIkAnalyzer analyzer = new MyIkAnalyzer();
        TokenStream stream = analyzer.tokenStream("content", new StringReader("慕课网是一个非常好的网站"));//这就是一个词汇流
        PositionIncrementAttribute pia = stream.addAttribute(PositionIncrementAttribute.class);
        OffsetAttribute oa = stream.addAttribute(OffsetAttribute.class);
        CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
        TypeAttribute ta = stream.addAttribute(TypeAttribute.class);
        BoostAttribute bs = stream.addAttribute(BoostAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            System.out.print("位置增量： " + pia.getPositionIncrement());//词与词之间的空格
            System.out.print("，单词： " + cta + "[" + oa.startOffset() + "," + oa.endOffset() + "]");
            System.out.print("，类型： " + ta.type());
            System.out.print("，权重： " + bs.getBoost());
            System.out.println();
        }
        System.out.println();
    }


    @Test
    public void tt() throws Exception {
        //创建Directory 指定索引位置
        Directory directory = FSDirectory.open(Paths.get(luceneIndexPath));
        //创建indexReader
        IndexReader indexReder = DirectoryReader.open(directory);
        //创建indexSearcher
        IndexSearcher searcher = new IndexSearcher(indexReder);
        //设置权重
        Map<String, Float> map = new HashMap<>();
        map.put("cpr_phname", 10F);
        map.put("cpr_content", 1F);
        //创建查询Query
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"cpr_phname", "cpr_content"}, new MyIkAnalyzer(), map);
//        TermQuery termQuery = new TermQuery(new Term("cpr_content", "柴胡"));
//        BoostQuery boostQuery = new BoostQuery(new TermQuery(new Term("cpr_phname", "柴胡")), 10f);
        Query query = queryParser.parse("药物");
        System.out.println("查询SQL语句：" + query);
        //执行查询
        TopDocs topDocs = searcher.search(query, 100);
        System.out.println("----------查询结果总条数：" + topDocs.totalHits + "------------");
        //获取索引id
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc doc : scoreDocs) {
            Document document = indexReder.document(doc.doc);
            System.out.print("name:" + document.get("cpr_phname"));
            System.out.println("content:" + document.get("cpr_content"));
        }
        indexReder.close();
        directory.close();
    }

    @Test
    public void fileter() throws IOException {
        String text = "Hi, Dr Wang, Mr Liu asks if you stay with Mrs Liu yesterday!";
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        MyTokenFilter myTokenFilter = new MyTokenFilter(standardAnalyzer.tokenStream("text", text));
        CharTermAttribute charTermAttribute = myTokenFilter.addAttribute(CharTermAttribute.class);
        myTokenFilter.reset();
        while (myTokenFilter.incrementToken()) {
            System.out.print(charTermAttribute + " ");
        }
    }


    /**
     * 加密数据基础
     */
    @Test
    public void encryptDataBase() {
        Connection connection = null;
        try {
            String dbPath = "D:\\Users\\cuiyingfan\\Desktop\\MyDemo2\\src\\main\\resources\\mcdex_raw.db";
            SQLiteMCConfig.Builder builder = new SQLiteMCConfig.Builder();
            builder.withKey(""); //连接已加密库
            connection = builder.createConnection("jdbc:sqlite:" + dbPath);


            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            //第一次加密
            statement.executeUpdate("PRAGMA rekey='123456'");

            ResultSet rs = statement.executeQuery("select count(*) from cpr_main");

            while (rs.next()) {
                // read the result set
                System.out.println("count = " + rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    @Test
    public void encryptPassword() throws Exception {
        String[] str = CryptoUtils.genKeyPair(512);
        System.out.println("privateKey:" + str[0]);
        System.out.println("publicKey:" + str[1]);
        String username = CryptoUtils.encrypt(str[0], "medicom");
        System.out.println("username:" + username);
    }

}
