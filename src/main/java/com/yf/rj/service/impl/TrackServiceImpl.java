package com.yf.rj.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.yf.rj.cache.Mp3Db;
import com.yf.rj.common.FileConstants;
import com.yf.rj.dto.BaseException;
import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.enums.TrackEnum;
import com.yf.rj.handler.TrackHandler;
import com.yf.rj.req.TrackReq;
import com.yf.rj.service.FileUnify;
import com.yf.rj.service.TrackService;
import com.yf.rj.util.FileUtil;
import com.yf.rj.util.XmlUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jaudiotagger.tag.FieldKey;
import org.jsoup.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

@Service
public class TrackServiceImpl implements TrackService, FileUnify<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(TrackServiceImpl.class);
    private static final String[] EXCLUDE_WORDS = new String[]{"乙女", "女性向"};
    private static final String[] INCLUDE_WORDS = new String[]{"失禁", "放尿"};

    @Resource
    private TrackHandler trackHandler;

    @Override
    public Object common(TrackReq trackReq) throws BaseException {
        switch (TrackEnum.fromCode(trackReq.getCode())) {
            case CHECK_SERIES:
                return checkSeries();
            case CHECK_DL_TREE:
                return checkDlTree();
        }
        return null;
    }

    /**
     * 检查DL tree.xlsx里未整理的
     */
    private Object checkDlTree() throws BaseException {
        StringBuilder result = new StringBuilder("原版\t中文版\n");
        try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new File(FileConstants.DL_TREE_PATH))) {
            XSSFSheet sheet = xssfWorkbook.getSheet("Sheet1");
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //备注
                String cell6 = XmlUtil.getCellVal(row.getCell(6));
                if (!StringUtils.contains(cell6, "官方汉化")) {
                    continue;
                }
                //TAG
                String cell3 = XmlUtil.getCellVal(row.getCell(3));
                if (StringUtils.containsAny(cell3, EXCLUDE_WORDS)) {
                    continue;
                }
                //RJ
                String rj = FileUtil.getRj(cell6);
                //是否已整理
                if (StringUtil.isBlank(rj) || CollectionUtils.isNotEmpty(Mp3Db.queryByRj(rj))) {
                    continue;
                }
                if (result.toString().contains(rj)) {
                    continue;
                }
                //查询是否是全年龄
                if (!StringUtils.containsAny(cell3, INCLUDE_WORDS)) {
                    String workInfo = trackHandler.getWorkInfo(rj);
                    if (StringUtils.containsAny(workInfo, EXCLUDE_WORDS)) {
                        continue;
                    }
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(workInfo);
                        String age = (String) JSONPath.eval(jsonObject, "$.age_category_string");
                        if ("general".equals(age)) {
                            System.out.println("generral");
                            continue;
                        }
                    } catch (Exception ignore) {
                    }
                }
                //汉化音声的RJ
                String cell0 = XmlUtil.getCellVal(row.getCell(0));
                result.append(rj).append('\t').append(cell0.trim()).append('\n');
            }
        } catch (Exception e) {
            LOG.warn("读取文件失败", e);
            throw new BaseException("读取" + FileConstants.DL_TREE_PATH + "失败");
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(
                Paths.get(FileConstants.DESKTOP_DIR + "\\" + FileConstants.CHECK_DL_TREE_OUT))))) {
            bufferedWriter.write(result.toString());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new BaseException("创建文件失败");
        }
        return "检查DLTree成功";
    }

    /**
     * 检查系列实时性
     */
    private Object checkSeries() throws BaseException {
        StringBuilder result = new StringBuilder();
        result.append("rj号").append("\t").append("当前系列")
                .append("\t").append("爬取系列").append("\n");
        handleThird(FileTypeEnum.DIR, dir -> {
            Optional.ofNullable(dir.listFiles())
                    .map(Arrays::asList)
                    .orElse(new ArrayList<>())
                    .stream()
                    .filter(FileTypeEnum.MP3::match)
                    .findFirst()
                    .ifPresent(mp3File -> {
                        String rj = FileUtil.getRj(dir);
                        String curSeries = FileUtil.getField(mp3File, FieldKey.ALBUM_ARTIST);
                        String newSeries = trackHandler.getSeries(rj);
                        if (StringUtils.isAllBlank(curSeries, newSeries)) {
                            return;
                        }
                        if (!StringUtils.equals(curSeries, newSeries)) {
                            result.append(rj).append("\t").append(curSeries)
                                    .append("\t").append(newSeries).append("\n");
                        }
                    });
            return null;
        });
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(
                Paths.get(FileConstants.DESKTOP_DIR + "\\" + FileConstants.CHECK_SERIES_OUT))))) {
            bufferedWriter.write(result.toString());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new BaseException("创建文件失败");
        }
        return "检查系列实时性成功";
    }
}