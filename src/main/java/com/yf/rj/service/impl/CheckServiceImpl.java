package com.yf.rj.service.impl;

import com.yf.rj.cache.CategoryDb;
import com.yf.rj.cache.Mp3Db;
import com.yf.rj.common.FileConstants;
import com.yf.rj.common.GenreConstants;
import com.yf.rj.common.LrcConstants;
import com.yf.rj.common.Mp3AttrConstants;
import com.yf.rj.dto.BaseException;
import com.yf.rj.entity.CategoryT;
import com.yf.rj.entity.Mp3T;
import com.yf.rj.enums.CheckEnum;
import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.req.CheckReq;
import com.yf.rj.service.CheckService;
import com.yf.rj.service.FileUnify;
import com.yf.rj.util.FileUtil;
import com.yf.rj.util.RegexUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CheckServiceImpl implements CheckService, FileUnify<List<String>> {
    private static final Logger LOG = LoggerFactory.getLogger(CheckServiceImpl.class);
    private static final List<String> resultList = new ArrayList<>();

    @Override
    public Object common(CheckReq checkReq) throws BaseException {
        switch (CheckEnum.fromCode(checkReq.getCode())) {
            case MP3_ATTR:
                return checkMp3Attr();
            case SERIES:
                return checkSeries();
            case PICTURE:
                return checkPicture();
            case RJ:
                return checkRj();
            case MP3:
                return checkMp3();
            case DIR:
                return checkDir();
            case LRC:
                return checkLrc();
        }
        return null;
    }

    /**
     * 检查单字幕正确性
     */
    private List<String> checkLrc() {
        List<List<String>> resultList = handleThird(FileTypeEnum.DIR, dir -> {
            List<String> result = new ArrayList<>();
            Optional.ofNullable(dir.listFiles())
                    .map(Arrays::asList)
                    .orElse(new ArrayList<>())
                    .stream()
                    .filter(FileTypeEnum.LRC::match)
                    .forEach(lrcFile -> {
                        if (lrcFile.getAbsolutePath().contains("RJ01124582")) {
                            return;
                        }
                        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(lrcFile.toPath())))) {
                            String firstLine = bufferedReader.readLine();
                            String secondLine = bufferedReader.readLine();
                            if (StringUtils.isBlank(secondLine)) {
                                result.add(lrcFile.getAbsolutePath() + "少于两行");
                                return;
                            }
                            String thirdLine = bufferedReader.readLine();
                            if (StringUtils.isNotBlank(thirdLine)) {
                                return;
                            }
                            String word = firstLine.split("]", 2)[1];
                            if (LrcConstants.UN_CHINESE.equals(word)) {
                                return;
                            }
                            String name = lrcFile.getName().replaceAll(LrcConstants.H_LABEL, StringUtils.EMPTY);
                            name = RegexUtil.findFirst("(?<=\\.).*?(?=\\.lrc)", name);
                            if (!word.equals(name)) {
                                result.add(lrcFile.getAbsolutePath() + "单字幕不正确");
                                result.add("字幕：" + word);
                                result.add("标题：" + name);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("读取文件失败：" + lrcFile.getAbsolutePath());
                        }
                    });
            return result;
        });
        return resultList.stream().filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * 检查是否优化成图片文件夹
     */
    private List<String> checkDir() {
        resultList.clear();
        commonHandleDir();
        return resultList;
    }

    /**
     * 检查音频文件名正确性、序号正确性、和字幕文件对应
     */
    private List<String> checkMp3() {
        List<List<String>> resultList = handleThird(FileTypeEnum.DIR, dir -> {
            //检查优化为图片文件夹
            List<String> result = new ArrayList<>();
            Map<Boolean, List<String>> collect = Optional.ofNullable(dir.listFiles())
                    .map(Arrays::asList)
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(File::getName)
                    .filter(FileUtil::matchMp3Lrc)
                    .collect(Collectors.groupingBy(FileTypeEnum.MP3::match));
            List<String> mp3NameList = collect.get(Boolean.TRUE);
            List<String> lrcNameList = collect.get(Boolean.FALSE);
            String rj = FileUtil.getRj(dir);
            //检查mp3名称
            String invalidMp3 = mp3NameList.stream().filter(RegexUtil::invalidName).collect(Collectors.joining(", "));
            if (StringUtils.isNotBlank(invalidMp3)) {
                result.add(rj + "的音频名称非法：" + invalidMp3);
            }
            //mp3序号是否连续
            AtomicInteger counter = new AtomicInteger(1);
            AtomicBoolean needRecord = new AtomicBoolean(true);
            mp3NameList.stream().map(mp3Name -> mp3Name.split("\\.")[0])
                    .filter(StringUtils::isNumeric)
                    .map(Integer::parseInt).sorted()
                    .forEach(seq -> {
                        if (seq != counter.getAndIncrement() && needRecord.get()) {
                            result.add(rj + "的音频序号不连续：" + seq);
                            needRecord.set(false);
                        }
                    });
            //mp3和lrc是否对应
            mp3NameList = mp3NameList.stream().map(name -> name.replaceAll(FileTypeEnum.MP3.getSuffix(), StringUtils.EMPTY)).collect(Collectors.toList());
            lrcNameList = lrcNameList.stream().map(name -> name.replaceAll(FileTypeEnum.LRC.getSuffix(), StringUtils.EMPTY)).collect(Collectors.toList());
            String mp3More = String.join(", ", CollectionUtils.subtract(mp3NameList, lrcNameList));
            if (StringUtils.isNoneBlank(mp3More)) {
                result.add(rj + "的音频多出来：" + mp3More);
            }
            String lrcMore = String.join(", ", CollectionUtils.subtract(lrcNameList, mp3NameList));
            if (StringUtils.isNoneBlank(lrcMore)) {
                result.add(rj + "的字幕多出来：" + lrcMore);
            }
            return result;
        });
        return resultList.stream().filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    /**
     * 检查重复RJ和封面对应
     */
    private Set<String> checkRj() {
        Set<String> result = new HashSet<>();
        List<String> rjList = handleThird(FileTypeEnum.DIR, dir -> Collections.singletonList(FileUtil.getRj(dir)))
                .stream().filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream).map(rj -> rj + FileTypeEnum.JPG.getSuffix())
                .collect(Collectors.toList());
        Set<String> set = new HashSet<>();
        Iterator<String> iterator = rjList.iterator();
        //判断RJ是否有重复
        while (iterator.hasNext()) {
            String rj = iterator.next();
            if (!set.add(rj)) {
                result.add(rj + "重复了");
                iterator.remove();
            }
        }
        List<String> picRjList = Optional.of(new File(FileConstants.COVER_DIR))
                .map(File::listFiles).map(Arrays::asList)
                .orElse(new ArrayList<>())
                .stream()
                .map(File::getName)
                .filter(FileTypeEnum.INI::notMatch)
                .collect(Collectors.toList());
        //搜集封面文件
        Collection<String> rjMore = CollectionUtils.subtract(rjList, picRjList);
        String rjMoreMsg = rjMore.stream().collect(Collectors.joining(", ", "音声RJ多出来：", StringUtils.EMPTY));
        result.add(rjMoreMsg);

        Collection<String> picMore = CollectionUtils.subtract(picRjList, rjList);
        String picMoreMsg = picMore.stream().collect(Collectors.joining(", ", "图片RJ多出来：", StringUtils.EMPTY));
        result.add(picMoreMsg);
        return result;
    }


    /**
     * 检查图片
     */
    private List<String> checkPicture() {
        List<List<String>> resultList = handleThird(FileTypeEnum.DIR, dir -> {
            List<String> result = new ArrayList<>();
            List<String> fileNameList = Optional.ofNullable(dir.listFiles())
                    .map(Arrays::asList)
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(File::getName)
                    .filter(FileUtil::matchPic)
                    .filter(name -> !StringUtils.equalsAny(name, FileConstants.COVER_JPG, FileConstants.COVER_PNG))
                    .collect(Collectors.toList());
            String rj = FileUtil.getRj(dir);
            if (!fileNameList.contains(FileConstants.FOLDER_PIC)) {
                result.add(rj + "缺少" + FileConstants.FOLDER_PIC);
            } else {
                fileNameList.remove(FileConstants.FOLDER_PIC);
            }
            Iterator<String> iterator = fileNameList.iterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                if (!FileUtil.checkPicName(name)) {
                    result.add(rj + "图片名称不正确：" + name);
                    iterator.remove();
                }
            }
            List<Integer> seqList = fileNameList.stream().map(name -> name.split("\\.")[0].replace(FileConstants.PIC_PREFIX, StringUtils.EMPTY))
                    .map(name -> StringUtils.isBlank(name) ? 0 : Integer.parseInt(name))
                    .sorted()
                    .collect(Collectors.toList());
            AtomicInteger counter = new AtomicInteger(0);
            seqList.forEach(seq -> {
                if (seq != counter.getAndIncrement()) {
                    result.add(rj + "图片名称不连续");
                }
            });
            return result;
        });
        return resultList.stream().filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    /**
     * 检查错误或漏掉的系列
     */
    private Set<String> checkSeries() {
        Set<String> result = new HashSet<>();
        List<Mp3T> mp3List = Mp3Db.queryHasSeries();
        LOG.info("查询到包含系列的音频：{}", mp3List.size());

        //true-系列文件夹里的 false-分类文件夹里的
        Map<Boolean, List<Mp3T>> collect = mp3List.stream().collect(Collectors.groupingBy(mp3T -> {
            CategoryT categoryT = CategoryDb.queryById(mp3T.getCategoryId());
            return FileConstants.SERIES.equals(categoryT.getOneCategory());
        }));
        List<Mp3T> seriesMp3List = collect.get(Boolean.TRUE);
        LOG.info("查询到系列文件夹的音频：{}", seriesMp3List.size());
        List<String> seriesList = seriesMp3List.stream().map(Mp3T::getSeries).distinct().collect(Collectors.toList());
        List<Mp3T> classifyMp3List = collect.get(Boolean.FALSE);
        LOG.info("查询到分类文件夹的音频：{}", classifyMp3List.size());
        classifyMp3List.stream().filter(mp3T -> seriesList.contains(mp3T.getSeries()))
                .forEach(mp3T -> result.add(mp3T.getRj() + "请移动至已存在的系列文件夹"));
        //二级分类
        Map<Integer, List<Mp3T>> map = seriesMp3List.stream().collect(Collectors.groupingBy(Mp3T::getCategoryId));
        //判断是否有重复系列的文件夹
        List<String> seriesCollect = new ArrayList<>();
        map.forEach((key, value) -> {
            List<String> onlyOneSeries = value.stream().map(Mp3T::getSeries).distinct().collect(Collectors.toList());
            if (onlyOneSeries.size() > 1) {
                CategoryT categoryT = CategoryDb.queryById(key);
                result.add(categoryT.getUniKey() + "存在系列不一致的情况");
            }
            String series = onlyOneSeries.get(0);
            if (seriesCollect.contains(series)) {
                result.add(series + "系列存在重复文件夹");
            }
            seriesCollect.addAll(onlyOneSeries);
        });
        return result;
    }

    /**
     * 检查mp3的属性
     */
    private Set<String> checkMp3Attr() {
        Set<String> result = new HashSet<>();
        List<Mp3T> mp3List = Mp3Db.queryAll();
        Map<String, List<Mp3T>> rjMap = mp3List.stream().collect(Collectors.groupingBy(Mp3T::getRj));
        rjMap.forEach((key, value) -> {
            Map<String, String> map = new HashMap<>();
            value.forEach(mp3T -> {
                //标题
                checkEle(mp3T.getTitle(), result, Mp3AttrConstants.TITLE, mp3T, map);
                //声优
                checkEle(mp3T.getArtist(), result, Mp3AttrConstants.ARTIST, mp3T, map);
                //唱片集
                checkEle(mp3T.getAlbum(), result, Mp3AttrConstants.ALBUM, mp3T, map);
                //年份
                checkEle(mp3T.getYear(), result, Mp3AttrConstants.YEAR, mp3T, map);
                //标签
                checkEle(mp3T.getComment(), result, Mp3AttrConstants.COMMENT, mp3T, map);
                //汉化组
                checkEle(mp3T.getGenre(), result, Mp3AttrConstants.GENRE, mp3T, map);
                //系列
                checkEle(mp3T.getSeries(), result, Mp3AttrConstants.SERIES, mp3T, map);
                //社团
                checkEle(mp3T.getComposer(), result, Mp3AttrConstants.COMPOSER, mp3T, map);
                //比特率
                checkEle(String.valueOf(mp3T.getBitRate()), result, Mp3AttrConstants.BIT_RATE, mp3T, map);
            });
        });
        return result;
    }

    @Override
    public boolean handleRootDir(File dir) {
        checkSetPicDir(dir);
        return true;
    }

    @Override
    public boolean handleSecondDir(File dir) {
        checkSetPicDir(dir);
        return true;
    }

    @Override
    public boolean handleThirdDir(File dir) {
        checkSetPicDir(dir);
        return true;
    }

    @Override
    public void handleFourthDir(File dir) {
        checkSetPicDir(dir);
    }

    private static void checkSetPicDir(File dir) {
        Boolean check = Optional.ofNullable(dir.listFiles())
                .map(Arrays::asList)
                .orElse(new ArrayList<>())
                .stream()
                .filter(FileTypeEnum.INI::match)
                .findFirst()
                .map(file -> {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            if (str.contains("FolderType=Pictures")) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("读取文件失败：" + file.getAbsolutePath());
                    }
                    return true;
                }).orElse(true);
        if (check) {
            resultList.add(dir.getAbsolutePath() + "没有优化成图片文件夹");
        }
    }

    private static void checkSame(Map<String, String> map, String key, String value, Set<String> result, String rj) {
        if (map.containsKey(key)) {
            if (!StringUtils.equals(map.get(key), value)) {
                result.add(rj + "的" + key + "不一致");
            }
        } else {
            map.put(key, value);
        }
    }

    private static void checkEle(String ele, Set<String> result, String colName, Mp3T mp3T, Map<String, String> map) {
        switch (colName) {
            case Mp3AttrConstants.TITLE:
                checkBlank(ele, result, colName, mp3T);
                checkSpace(ele, result, colName, mp3T);
                break;
            case Mp3AttrConstants.ARTIST:
                checkBlank(ele, result, colName, mp3T);
                checkSpace(ele, result, colName, mp3T);
                if (StringUtils.containsAny(ele, "紅月ことね", "乙倉由依")) {
                    result.add(mp3T.getRj() + "声优不正确");
                }
                checkSame(map, colName, ele, result, mp3T.getRj());
                break;
            case Mp3AttrConstants.ALBUM:
            case Mp3AttrConstants.YEAR:
            case Mp3AttrConstants.COMMENT:
            case Mp3AttrConstants.COMPOSER:
                checkBlank(ele, result, colName, mp3T);
                checkSpace(ele, result, colName, mp3T);
                checkSame(map, colName, ele, result, mp3T.getRj());
                break;
            case Mp3AttrConstants.GENRE:
                checkBlank(ele, result, colName, mp3T);
                checkSpace(ele, result, colName, mp3T);
                if (!StringUtils.equalsAny(ele, GenreConstants.OFFICIAL, GenreConstants.WIND_SNOW
                        , GenreConstants.BEI_JI, GenreConstants.CHENG_ZI)) {
                    result.add(mp3T.getRj() + "汉化组不正确");
                }
                checkSame(map, colName, ele, result, mp3T.getRj());
                break;
            case Mp3AttrConstants.SERIES:
                checkSpace(ele, result, colName, mp3T);
                checkSame(map, colName, ele, result, mp3T.getRj());
                break;
            case Mp3AttrConstants.BIT_RATE:
                if (!FileConstants.BIT_RATE.equals(ele)) {
                    result.add(mp3T.getUnionName() + Mp3AttrConstants.BIT_RATE + ele);
                }
        }
    }

    private static void checkBlank(String ele, Set<String> result, String colName, Mp3T mp3T) {
        if (StringUtils.isBlank(ele)) {
            result.add(mp3T.getRj() + colName + "为空");
        }
    }

    private static void checkSpace(String ele, Set<String> result, String colName, Mp3T mp3T) {
        if (ele != null && ele.trim().length() != ele.length()) {
            result.add(mp3T.getRj() + colName + "有多余空格");
        }
    }
}