package com.medicom.search.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medicom.search.domain.CprMain;
import com.medicom.search.mapper.CprMainMapper;
import com.medicom.search.vo.CprVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Service
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




