package com.hua.community.service;

import com.hua.community.dao.elasticsearch.DiscussPostRepository;
import com.hua.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @create 2022-05-04 13:26
 */
@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchOperations operations;

    /**
     * 添加到elasticsearch
     * @param post
     */
    public void saveDiscussPost(DiscussPost post){
        discussRepository.save(post);
    }

    /**
     * 根据id从elasticsearch删除一条帖子
     * @param id
     */
    public void deleteDiscussPost(int id){
        discussRepository.deleteById(id);
    }

    /**
     * 返回第current页每页limit条根据keyword搜索的数据，
     * @param keyword
     * @param current
     * @param limit
     * @return
     */
    public Map<String, Object> searchDiscussPost(String keyword, int current, int limit) {
        HashMap<String, Object> map = new HashMap<>();

        //构建搜索条件
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        //搜索结果
        SearchHits<DiscussPost> hits = operations.search(searchQuery, DiscussPost.class);

        List<SearchHit<DiscussPost>> list = hits.getSearchHits();


        List<DiscussPost> resule = new ArrayList<>();
        for (SearchHit hit : list) {
            //将搜索的结果转为java对象
            DiscussPost post = (DiscussPost) hit.getContent();

            //设置高亮显示
            Map fieldMap = hit.getHighlightFields();
            List title = (List) fieldMap.get("title");
            if (title != null) {
                post.setTitle(title.get(0).toString());
            }
            List content = (List) fieldMap.get("content");
            if (content != null) {
                post.setContent(content.get(0).toString());
            }
            resule.add(post);
        }

        map.put("searchResule", resule);
        map.put("total", hits.getTotalHits());

        return map;
    }

}
