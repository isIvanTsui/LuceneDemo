package com.medicom.search.vo;

import lombok.Data;

/**
 *
 *
 * @author cuiyingfan
 * @date 2022/02/18
 */
@Data
public class CprVo {
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

    private Float score;

    /**
     * 最佳摘要
     */
    private String bestFragment;
}
