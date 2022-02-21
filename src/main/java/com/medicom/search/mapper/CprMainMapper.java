package com.medicom.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medicom.search.domain.CprMain;
import com.medicom.search.vo.CprVo;

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




