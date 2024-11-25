package com.yf.rj.service;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.TrackReq;

public interface TrackService {
    Object common(TrackReq trackReq) throws BaseException;
}