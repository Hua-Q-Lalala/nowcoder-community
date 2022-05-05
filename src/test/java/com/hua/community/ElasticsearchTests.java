package com.hua.community;

import com.hua.community.dao.DiscussPostMapper;
import com.hua.community.dao.elasticsearch.DiscussPostRepository;
import com.hua.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Update;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;

/**
 * @create 2022-05-03 10:35
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //加载配置类
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    //@Autowired
    //private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;



    /**
     * 向elasticsearch插入单条数据
     */
    @Test
    public void testInsert(){
        discussPostRepository.save(discussMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussMapper.selectDiscussPostById(243));
    }

    /**
     * 向elasticsearch插入列表
     */
    @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussMapper.selectDiscussPosts(0, 0, 1000));
    }

    /**
     * 更新
     */
    @Test
    public void testupdate(){
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("你好YA，我是新人哦");
        discussPostRepository.save(post);
    }

    /**
     * 删除
     */
    @Test
    public void testdelete(){
        //discussPostRepository.deleteById(231);    //根据id删除
        discussPostRepository.deleteAll();      //删除所有数据
    }

    /**
     * 视频内的， 基于Spring-data 3.1.9 elasticsearch6.4.3
     */
    @Test
    public void testsearchByRepository(){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("<.em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("<.em>")
                        ).build();

        //discussPostRepository.searchSimilar();
        // elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        // 底层获取得到了高亮显示的值, 但是没有返回.

        /*
        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
        */
    }

    /*

    @Test
    public void testSearchByTemplate() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        //获取高亮显示的值
        Page<DiscussPost> page = elasticTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHits hits = response.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }

                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits) {
                    DiscussPost post = new DiscussPost();

                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    // 处理高亮显示的结果
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        post.setTitle(titleField.getFragments()[0].toString());
                    }

                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    list.add(post);
                }

                return new AggregatedPageImpl(list, pageable,
                        hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
            }
        });

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }
    */

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    /**
     * 搜素
     */
    @Test
    public void testtestsearchByRepository1(){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                        ).build();
        SearchHits<DiscussPost> hits = elasticsearchOperations.search(searchQuery, DiscussPost.class);

        System.out.println(hits.getTotalHits());

        List<SearchHit<DiscussPost>> list = hits.getSearchHits();
        for(SearchHit hit : list){
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

            System.out.println(post);
        }



    }

















}
