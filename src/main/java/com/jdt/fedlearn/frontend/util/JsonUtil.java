/* Copyright 2020 The FedLearn Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.jdt.fedlearn.frontend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jdt.fedlearn.frontend.controller.TrainController;
import com.jdt.fedlearn.frontend.entity.train.metric.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static <T> T json2Object(String jsonStr,Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            logger.error("JsonUtil.json2Object JsonProcessingException:",e);
        }
        return null;
    }

    public static <T> T json2Object(String jsonStr, TypeReference<T> valueTypeRef){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonStr, valueTypeRef);
        } catch (JsonProcessingException e) {
            logger.error("JsonUtil.json2Object JsonProcessingException:",e);
        }
        return null;
    }




    public static String object2json(Object object) {
        if(object instanceof String){
            return (String) object;
        }
        if (object == null) {
            return null;
        }
        String jsonStr = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonStr = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("JsonUtil.object2json JsonProcessingException:" ,e);
        }
        return jsonStr;
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz){
        ObjectMapper objectMapper = new ObjectMapper();
        List<T> list = null;
        try {
            list = objectMapper.readValue(text, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.error("JsonUtil.parseArray JsonProcessingException:",e);
        }
        return list;
    }

    /***
     * @description: 对象转换为json字符串
     * @param object
     * @return: java.lang.String
     * @author: geyan29
     * @date: 2021/4/15 10:40 上午
     */
    public static Map<String, Object> object2map(Object object) {
        String jsonStr = object2json(object);
        Map<String,Object> map = json2Object(jsonStr,Map.class);
        return map;
    }

    public static void main(String[] args) throws JsonProcessingException {
        String a = "[{\"name\":\"mape\",\"metric\":[{\"x\":0,\"y\":-1.7976931348623157E308}]}]";
        ObjectMapper objectMapper = new ObjectMapper();

        List metrics = objectMapper.readValue(a, List.class);

        List<String> desc = TrainController.addDesc(10,metrics, "12sf-qwe" );
        System.out.println(desc);
    }

}
