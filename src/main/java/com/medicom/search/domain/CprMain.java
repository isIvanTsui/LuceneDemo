package com.medicom.search.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName cpr_main
 */
@TableName(value ="cpr_main")
@Data
public class CprMain implements Serializable {
    /**
     * 
     */
    @TableId
    private Object cprid;

    /**
     * 
     */
    private Object genid;

    /**
     * 
     */
    private Object drugNameid;

    /**
     * 
     */
    private String cprTitle;

    /**
     * 
     */
    private String firstCode;

    /**
     * 
     */
    private Object isWarndrug;

    /**
     * 
     */
    private Object sortcode;

    /**
     * 
     */
    private String launchDate;

    /**
     * 
     */
    private String cprEditTime;

    /**
     * 
     */
    private String cprUpdateTime;

    /**
     * 
     */
    private String searchName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}