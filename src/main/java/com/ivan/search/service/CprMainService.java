package com.ivan.search.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ivan.search.domain.CprMain;
import com.ivan.search.mapper.CprMainMapper;
import com.ivan.search.vo.CprVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Service
@DS("sqlite")
public class CprMainService extends ServiceImpl<CprMainMapper, CprMain>
        implements IService<CprMain> {

    @Resource
    private CprMainMapper cprMapper;

    /**
     * 获取所有cpr
     *
     * @return {@link List}<{@link CprVo}>
     */
    public List<CprVo> listCpr() {
        return cprMapper.listCpr();
    }
}




