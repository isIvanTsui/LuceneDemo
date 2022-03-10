package com.ivan.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ivan.search.domain.CprMain;
import com.ivan.search.vo.CprVo;

import java.util.List;

/**
 * @Entity generator.domain.CprMain
 */
public interface CprMainMapper extends BaseMapper<CprMain> {

    /**
     * 获取所有cpr
     *
     * @return {@link List}<{@link CprVo}>
     */
    List<CprVo> listCpr();
}




