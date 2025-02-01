package com.yf.rj.service.impl;

import com.yf.rj.cache.Mp3Db;
import com.yf.rj.common.FileConstants;
import com.yf.rj.common.RjConstants;
import com.yf.rj.config.DlSiteProperties;
import com.yf.rj.config.WordProperties;
import com.yf.rj.dto.BaseException;
import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.enums.TrackEnum;
import com.yf.rj.handler.TrackHandler;
import com.yf.rj.req.TrackReq;
import com.yf.rj.service.FileUnify;
import com.yf.rj.service.TrackService;
import com.yf.rj.util.ClipboardUtil;
import com.yf.rj.util.FileUtil;
import com.yf.rj.util.RegexUtil;
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

    @Resource
    private TrackHandler trackHandler;
    @Resource
    private DlSiteProperties dlSiteProperties;

    @Override
    public Object common(TrackReq trackReq) throws BaseException {
        switch (TrackEnum.fromCode(trackReq.getCode())) {
            case CHECK_SERIES:
                return checkSeries();
            case CHECK_DL_TREE:
                return checkDlTree();
            case DOWNLOAD_PIC:
                return downloadPic();
        }
        return null;
    }

    /**
     * 从DL SITE下载图片
     */
    private Object downloadPic() throws BaseException {
        String baseUrl = ClipboardUtil.read();
        if (!FileTypeEnum.WEBP.match(baseUrl)) {
            throw new BaseException("输入的图片网址不正确：" + baseUrl);
        }
        int seq = 1;
        int count = 0;
        if (baseUrl.contains(dlSiteProperties.getImgMain())) {
            String rj = RegexUtil.findLast("RJ\\d+", baseUrl);
            String result = trackHandler.downloadPic(baseUrl, rj + FileTypeEnum.JPG.getSuffix());
            if (StringUtils.isBlank(result)) {
                return "下载封面失败";
            }
            count++;
        } else if (baseUrl.contains(dlSiteProperties.getImgSmp())) {
            seq = Integer.parseInt(RegexUtil.middle(baseUrl, dlSiteProperties.getImgSmp(), FileTypeEnum.WEBP.getSuffix()));
        } else {
            throw new BaseException("输入的图片网址不正确：" + baseUrl);
        }
        String prefix = RegexUtil.lastBefore(baseUrl, dlSiteProperties.getImg()) + dlSiteProperties.getImgSmp();
        for (; ; seq++, count++) {
            String result = trackHandler.downloadPic(prefix + seq + FileTypeEnum.JPG.getSuffix(), seq + FileTypeEnum.JPG.getSuffix());
            if (StringUtils.isBlank(result)) {
                break;
            }
        }
        return "下载图片成功：" + count;
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
                if (StringUtils.containsAny(cell3, WordProperties.getTrackExclude())) {
                    continue;
                }
                //RJ
                String rj = FileUtil.getRj(cell6);
                if (RjConstants.SKIP_RJ.contains(rj)) {
                    continue;
                }
                //MEGA链接
                String megaUrl = XmlUtil.getCellVal(row.getCell(2));
                if (!StringUtils.contains(megaUrl, "mega.nz")) {
                    LOG.info("{}无mega档跳过，{}", rj, megaUrl);
                    continue;
                }
                //是否已整理
                if (StringUtil.isBlank(rj) || CollectionUtils.isNotEmpty(Mp3Db.queryByRj(rj))) {
                    continue;
                }
                if (result.toString().contains(rj)) {
                    continue;
                }
                //查询是否是全年龄
                String searchInfo = trackHandler.search(rj);
                if (StringUtils.containsAny(searchInfo, WordProperties.getTrackExclude())) {
                    LOG.info("{}触发关键字跳过：{}", rj, searchInfo);
                    continue;
                }
                if (!StringUtils.containsAny(cell3, WordProperties.getTrackInclude())) {
                    if (searchInfo.contains("\"age_category_string\":\"general\"")) {
                        LOG.info("{}全年龄跳过", rj);
                        continue;
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