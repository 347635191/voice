package com.yf.rj.entity;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface Mp3CopyMapper {
    Mp3CopyMapper INSTANCE = Mappers.getMapper(Mp3CopyMapper.class);
    Mp3T covert(Mp3T mp3T);
}