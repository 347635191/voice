package com.yf.rj.handler;

import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.enums.ResultType;
import com.yf.rj.req.SearchReq;
import com.yf.rj.service.FileUnify;
import com.yf.rj.vo.SearchVo;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

@Component
public class SearchLrcHandler implements FileUnify<SearchVo> {
    public List<SearchVo> keyWord(SearchReq req) {
        return handleFourth(FileTypeEnum.LRC, file -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    if (str.toLowerCase().contains(req.getKeyWord().toLowerCase())) {
                        SearchVo searchVo = new SearchVo();
                        if (req.getGetColumn()) {
                            searchVo.setColumn(str);
                        }
                        if (ResultType.MAME.getCode().equals(req.getType())) {
                            searchVo.setPath(file.getName());
                        } else {
                            searchVo.setPath(file.getAbsolutePath());
                        }
                        return searchVo;
                    }
                }
            } catch (Exception e) {
                LOG.error("读取lrc文件失败：{}", file.getAbsolutePath());
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}