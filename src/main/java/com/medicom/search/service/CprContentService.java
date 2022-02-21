package com.medicom.search.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medicom.search.domain.CprContent;
import com.medicom.search.mapper.CprContentMapper;
import com.medicom.search.service.CprContentService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class CprContentService extends ServiceImpl<CprContentMapper, CprContent>
implements IService<CprContent> {

}




