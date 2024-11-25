package com.yf.rj.service;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.CheckReq;
import com.yf.rj.req.DailyReq;

import java.util.Set;

public interface CheckService {
    Object common(CheckReq checkReq) throws BaseException;
}