package com.yf.rj.handler;

import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.enums.ResultType;
import com.yf.rj.req.SearchReq;
import com.yf.rj.service.FileUnify;
import com.yf.rj.util.FileUtil;
import com.yf.rj.vo.SearchVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchDirHandler implements FileUnify<SearchVo> {
    private static final Logger LOG = LoggerFactory.getLogger(SearchDirHandler.class);

    /**
     * 查询指定日期后整理的音声
     */
    public List<SearchVo> createTime(SearchReq req) {
        return handleThird(FileTypeEnum.DIR, dir -> {
            String createTime = FileUtil.getCreateTime(dir);
            if (StringUtils.isBlank(createTime)) {
                LOG.warn("获取文件夹创建时间失败：{}", dir.getName());
                return null;
            }
            if (createTime.compareTo(req.getKeyWord()) >= 0) {
                SearchVo searchVo = new SearchVo();
                if (ResultType.MAME.getCode().equals(req.getType())) {
                    searchVo.setPath(createTime + "  " + FileUtil.getRj(dir));
                } else {
                    searchVo.setPath(createTime + "  " + dir.getAbsolutePath());
                }
                return searchVo;
            }
            return null;
        });
    }

    /**
     * RJ文件夹名查询关键字
     */
    public List<SearchVo> searchDir(SearchReq req) {
        if (StringUtils.isBlank(req.getKeyWord())) {
            return null;
        }
        return handleThird(FileTypeEnum.DIR, dir -> {
            if (StringUtils.containsIgnoreCase(dir.getName(), req.getKeyWord())) {
                SearchVo searchVo = new SearchVo();
                if (ResultType.MAME.getCode().equals(req.getType())) {
                    searchVo.setPath(FileUtil.getRj(dir));
                } else {
                    searchVo.setPath(dir.getAbsolutePath());
                }
                return searchVo;
            }
            return null;
        });
    }

    /**
     * 文件名搜索关键字
     */
    public List<SearchVo> searchFile(SearchReq req) {
        if (StringUtils.isBlank(req.getKeyWord())) {
            return null;
        }
        return handleFourth(FileTypeEnum.LRC, lrcFile -> {
            if (StringUtils.containsIgnoreCase(lrcFile.getName(), req.getKeyWord())) {
                SearchVo searchVo = new SearchVo();
                if (ResultType.MAME.getCode().equals(req.getType())) {
                    searchVo.setPath(FileUtil.getRj(lrcFile));
                } else {
                    searchVo.setPath(lrcFile.getAbsolutePath());
                }
                return searchVo;
            }
            return null;
        });
    }
}