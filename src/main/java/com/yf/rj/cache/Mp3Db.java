package com.yf.rj.cache;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.TransactionalIndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.googlecode.cqengine.resultset.ResultSet;
import com.yf.rj.common.FileConstants;
import com.yf.rj.entity.CategoryT;
import com.yf.rj.entity.Mp3T;
import com.yf.rj.enums.Mp3IndexEnum;
import com.yf.rj.enums.ResultType;
import com.yf.rj.req.SearchReq;
import com.yf.rj.vo.SearchVo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.googlecode.cqengine.query.QueryFactory.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mp3Db {
    private static final Logger LOG = LoggerFactory.getLogger(Mp3Db.class);
    private static final IndexedCollection<Mp3T> CACHE;

    static {
        //堆内持久化
        CACHE = new TransactionalIndexedCollection<>(Mp3T.class);
        //添加索引
        CACHE.addIndex(UniqueIndex.onAttribute(Index.ID));
        CACHE.addIndex(HashIndex.onAttribute(Index.RJ));
//        CACHE.addIndex(SuffixTreeIndex.onAttribute(Index.SERIES));
        //复合唯一索引
        CACHE.addIndex(UniqueIndex.onAttribute(Index.UNI_KEY));
    }

    public static final class Index {
        //创建属性的访问者对象
        public static final Attribute<Mp3T, Long> ID = attribute("ID", Mp3T::getId);
        public static final Attribute<Mp3T, String> RJ = attribute("RJ", Mp3T::getRj);
        public static final Attribute<Mp3T, String> ARTIST = attribute("ARTIST", Mp3T::getArtist);
        public static final Attribute<Mp3T, String> UNI_KEY = attribute("UNI_KEY", Mp3T::getUniKey);
        public static final Attribute<Mp3T, String> SERIES = nullableAttribute("SERIES", Mp3T::getSeries);
        public static final Attribute<Mp3T, String> COMPOSER = nullableAttribute("COMPOSER", Mp3T::getComposer);
        public static final Attribute<Mp3T, String> GENRE = nullableAttribute("GENRE", Mp3T::getGenre);
        public static final Attribute<Mp3T, String> COMMENT = nullableAttribute("COMMENT", Mp3T::getComment);
        public static final Attribute<Mp3T, String> RJ_NAME = attribute("RJ_NAME", Mp3T::getRjName);
        public static final Attribute<Mp3T, String> FILE_NAME = attribute("FILE_NAME", Mp3T::getFileName);
    }

    public static void upset(List<Mp3T> mp3TList, boolean delete) {
        if (CollectionUtils.isEmpty(mp3TList)) {
            return;
        }
        List<Mp3T> delList, insList;
        List<Long> idList = mp3TList.stream().map(Mp3T::getId).collect(Collectors.toList());
        try (ResultSet<Mp3T> result = CACHE.retrieve(in(Index.ID, idList))) {
            delList = result.stream().collect(Collectors.toList());
            insList = delete ? Collections.emptyList() : mp3TList;
        }
        CACHE.update(delList, insList);
    }

    public static List<Mp3T> queryAll() {
        try (ResultSet<Mp3T> result = CACHE.retrieve(all(Mp3T.class))) {
            return result.stream().collect(Collectors.toList());
        }
    }

    public static List<Mp3T> queryByRj(String rj) {
        try (ResultSet<Mp3T> result = CACHE.retrieve(equal(Index.RJ, rj))) {
            return result.stream().collect(Collectors.toList());
        }
    }

    /**
     * 查询包含系列的音声
     */
    public static List<Mp3T> queryHasSeries() {
        try (ResultSet<Mp3T> result = CACHE.retrieve(has(Index.SERIES))) {
            return result.stream().collect(Collectors.toList());
        }
    }

    public static List<SearchVo> commonSearch(SearchReq searchReq) {
        Mp3IndexEnum indexEnum = Mp3IndexEnum.fromCode(searchReq.getCode());
        try (ResultSet<Mp3T> result = CACHE.retrieve(contains(indexEnum.getIndex(), searchReq.getKeyWord()))) {
            return result.stream().map(mp3T -> buildSearchVo(mp3T, indexEnum, searchReq))
                    .distinct().collect(Collectors.toList());
        }
    }

    private static SearchVo buildSearchVo(Mp3T mp3T, Mp3IndexEnum indexEnum, SearchReq searchReq) {
        SearchVo searchVo = new SearchVo();
        if (searchReq.getGetColumn()) {
            searchVo.setColumn(indexEnum.getColumn().apply(mp3T));
        }
        if (ResultType.MAME.getCode().equals(searchReq.getType())) {
            searchVo.setPath(mp3T.getRjName());
        } else {
            searchVo.setPath(buildAbsoluteFile(mp3T));
        }
        return searchVo;
    }

    private static String buildAbsoluteFile(Mp3T mp3T) {
        CategoryT categoryT = CategoryDb.queryById(mp3T.getCategoryId());
        return FileConstants.ROOT_DIR + "\\" + categoryT.getUniKey() + "\\" +
                mp3T.getRjName() + "\\" + mp3T.getFileName();
    }

    public static void clear() {
        LOG.info("清空音频缓存：{}", CACHE.size());
        CACHE.clear();
    }
}