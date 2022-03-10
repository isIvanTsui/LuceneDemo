package com.ivan.search.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ivan.search.domain.CprContent;
import com.ivan.search.mapper.CprContentMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@DS("sqlite")
public class CprContentService extends ServiceImpl<CprContentMapper, CprContent>
implements IService<CprContent> {

}




