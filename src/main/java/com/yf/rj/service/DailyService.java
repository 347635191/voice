package com.yf.rj.service;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.DailyReq;

public interface DailyService {
    String common(DailyReq dailyReq) throws BaseException;
}