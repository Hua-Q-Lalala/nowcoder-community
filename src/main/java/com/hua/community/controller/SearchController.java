package com.hua.community.controller;

import com.hua.community.entity.DiscussPost;
import com.hua.community.entity.Page;
import com.hua.community.service.ElasticsearchService;
import com.hua.community.service.LikeService;
import com.hua.community.service.UserService;
import com.hua.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @create 2022-05-04 14:00
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        Map<String, Object> resuleMap = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        //搜索结果
        List<DiscussPost> searchResule = (List<DiscussPost>) resuleMap.get("searchResule");
        //聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(searchResule != null){
            for(DiscussPost post : searchResule){
                Map<String, Object> map = new HashMap<>();
                //帖子
                map.put("post", post);
                //作者
                map.put("user", userService.findUserById(post.getUserId()));
                //点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        //分页信息
        page.setPath("/search?keyword=" + keyword);
        Long o = (Long) resuleMap.get("total");
        page.setRows(o.intValue());

        return "site/search";
    }
}
