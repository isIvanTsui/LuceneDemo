package com.ivan.search;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * lucene配置
 *
 * @author cuiyingfan
 * @date 2022/03/01
 */
@ConfigurationProperties(prefix = "lucene")
@Component
@Data
public class LuceneConfig {
    /**
     * 索引存放路径
     */
    private String indexPath;

    /**
     * 权重文件
     */
    private String weightFile;
}
