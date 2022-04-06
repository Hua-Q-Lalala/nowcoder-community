package com.hua.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @create 2022-03-25 10:29
 */
public class CommunityUtil {

    /**
     *  生成随机字符串
     * @return
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD%加密
     * key = hello  --> abc134def453
     * hello（用户真正输入的密码） + salt(数据库字段)  -->  abc134def453pou343djf （通过md5加密后的密码）
     * @param key
     * @return
     */
    public static String md5(String key){
        //利用org.apache.commons.lang3.StringUtils验证key是否为空
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 生成json数据
     * @param code    状态码
     * @param msg   提示信息
     * @param map   业务数据
     * @return
     */
    public static String getJsonString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null){
            for (String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();
    }

    /**
     * 生成json数据
     * @param code   状态码
     * @param msg    提示信息
     * @return
     */
    public static String getJsonString(int code, String msg){
        return getJsonString(code, msg, null);
    }

    /**
     * 生成json数据
     * @param code  状态码
     * @return
     */
    public static String getJsonString(int code){
        return getJsonString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();

        map.put("name", "zhangsan");
        map.put("age", 48);
        System.out.println(getJsonString(0, "ok", map));
    }
}
