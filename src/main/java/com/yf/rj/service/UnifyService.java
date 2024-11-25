package com.yf.rj.service;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.UnifyReq;

public interface UnifyService {
    Object common(UnifyReq unifyReq) throws BaseException;
}