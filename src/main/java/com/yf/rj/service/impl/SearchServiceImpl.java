package com.yf.rj.service.impl;

import com.yf.rj.cache.Mp3Db;
import com.yf.rj.dto.BaseException;
import com.yf.rj.enums.LrcSearchEnum;
import com.yf.rj.handler.SearchLrcHandler;
import com.yf.rj.req.SearchReq;
import com.yf.rj.service.SearchService;
import com.yf.rj.vo.SearchVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Resource
    private SearchLrcHandler handler;

    @Override
    public List<SearchVo> common(SearchReq searchReq) throws BaseException {
        if (StringUtils.isNumeric(searchReq.getCode())) {
            return Mp3Db.commonSearch(searchReq);
        }
        return LrcSearchEnum.fromCode(searchReq.getCode()).getSearch().apply(handler, searchReq);
    }
}