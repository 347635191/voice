package com.yf.rj.cache;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.TransactionalIndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.index.compound.CompoundIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.googlecode.cqengine.query.simple.Equal;
import com.googlecode.cqengine.resultset.ResultSet;
import com.yf.rj.entity.CategoryT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.googlecode.cqengine.query.QueryFactory.*;

@SuppressWarnings("unchecked")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDb {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryDb.class);
    private static final IndexedCollection<CategoryT> CACHE;

    static {
        //堆内持久化
        CACHE = new TransactionalIndexedCollection<>(CategoryT.class);
        //添加索引
        CACHE.addIndex(UniqueIndex.onAttribute(Index.ID));
        //联合索引
        CACHE.addIndex(CompoundIndex.onAttributes(Index.ONE_CATEGORY, Index.TWO_CATEGORY));
        //复合唯一索引
        CACHE.addIndex(UniqueIndex.onAttribute(Index.UNI_KEY));
    }

    private static final class Index {
        //创建属性的访问者对象
        private static final Attribute<CategoryT, Integer> ID = attribute("ID", CategoryT::getId);
        private static final Attribute<CategoryT, String> ONE_CATEGORY = attribute("ONE_CATEGORY", CategoryT::getOneCategory);
        private static final Attribute<CategoryT, String> TWO_CATEGORY = attribute("TWO_CATEGORY", CategoryT::getTwoCategory);
        private static final Attribute<CategoryT, String> UNI_KEY = attribute("UNI_KEY", CategoryT::getUniKey);
        private static final Attribute<CategoryT, String> CREATE_TIME = attribute("CREATE_TIME", CategoryT::getCreateTime);
        private static final Attribute<CategoryT, String> UPDATE_TIME = attribute("UPDATE_TIME", CategoryT::getUpdateTime);
    }

    public static void upset(List<CategoryT> categoryTList, boolean delete) {
        if (CollectionUtils.isEmpty(categoryTList)) {
            return;
        }
        List<CategoryT> delList, insList;
        List<Integer> idList = categoryTList.stream().map(CategoryT::getId).collect(Collectors.toList());
        try (ResultSet<CategoryT> result = CACHE.retrieve(in(Index.ID, idList))) {
            delList = result.stream().collect(Collectors.toList());
            insList = delete ? Collections.emptyList() : categoryTList;
        }
        CACHE.update(delList, insList);
    }

    public static List<CategoryT> queryAll() {
        try (ResultSet<CategoryT> result = CACHE.retrieve(all(CategoryT.class))) {
            return result.stream().collect(Collectors.toList());
        }
    }

    public static CategoryT queryByCategory(String oneCategory, String twoCategory) {
        Equal<CategoryT, String> equalOne = equal(Index.ONE_CATEGORY, oneCategory);
        Equal<CategoryT, String> equalTwo = equal(Index.TWO_CATEGORY, twoCategory);
        try (ResultSet<CategoryT> result = CACHE.retrieve(and(equalOne, equalTwo))) {
            return result.uniqueResult();
        }
    }

    public static CategoryT queryById(Integer id){
        try (ResultSet<CategoryT> result = CACHE.retrieve(equal(Index.ID, id))) {
            return result.uniqueResult();
        }
    }

    public static void clear(){
        LOG.info("清空分类缓存：{}", CACHE.size());
        CACHE.clear();
    }
}