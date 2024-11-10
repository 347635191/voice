package com.yf.rj.service;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.SearchReq;
import com.yf.rj.vo.SearchVo;

import java.util.List;

public interface SearchService {
    List<SearchVo> common(SearchReq searchReq) throws BaseException;
}