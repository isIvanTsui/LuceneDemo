package com.medicom.search.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName cpr_content
 */
@TableName(value ="cpr_content")
@Data
public class CprContent implements Serializable {
    /**
     * 
     */
    private Object cprid;

    /**
     * 
     */
    private String cprPhname;

    /**
     * 
     */
    private Object cprSeqnum;

    /**
     * 
     */
    private Object cprSeqnum2;

    /**
     * 
     */
    private String cprContent;

    @TableField(exist = false)
    private Float score;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}