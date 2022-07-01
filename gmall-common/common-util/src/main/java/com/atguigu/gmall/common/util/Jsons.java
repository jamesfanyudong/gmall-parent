package com.atguigu.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;


@Slf4j
public class Jsons {
    static ObjectMapper mapper = new ObjectMapper();
    public static String toStr(Object o){

        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";

    }

    public static<T> T  toObj(String categorys, Class<T> reference) {
        if (StringUtils.isEmpty(categorys)){
            return null;
        }
        try {
            return mapper.readValue(categorys,reference);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param json
     * @param ref
     * @param <T>
     * @return
     */
    public static<T> T toObj(String json, TypeReference<T> ref) {

        if (StringUtils.isEmpty(json)){
            return null;
        }
        T t = null;
        try {
            t = mapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            log.error("json转换对象异常：{}",e);
        }
        return t;
    }
}
