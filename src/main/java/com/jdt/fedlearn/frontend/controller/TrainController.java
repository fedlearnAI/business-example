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

package com.jdt.fedlearn.frontend.controller;

import com.jdt.fedlearn.frontend.constant.RequestConstant;
import com.jdt.fedlearn.frontend.entity.ParameterType;
import com.jdt.fedlearn.frontend.entity.project.FeatureDTO;
import com.jdt.fedlearn.frontend.entity.project.PartnerInfoNew;
import com.jdt.fedlearn.frontend.entity.table.PartnerDO;
import com.jdt.fedlearn.frontend.entity.table.ProjectDO;
import com.jdt.fedlearn.frontend.entity.train.*;
import com.jdt.fedlearn.frontend.jdchain.config.JdChainFalseCondition;
import com.jdt.fedlearn.frontend.service.IFeatureService;
import com.jdt.fedlearn.frontend.service.IPartnerService;
import com.jdt.fedlearn.frontend.service.IProjectService;
import com.jdt.fedlearn.frontend.util.HttpClientUtil;
import com.jdt.fedlearn.frontend.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 训练
 */
@Conditional(JdChainFalseCondition.class)
@RestController
@RequestMapping("api/train")
public class TrainController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${baseUrl}")
    private String baseUrl;
    @Resource
    IFeatureService featureService;
    @Resource
    IProjectService projectService;
    @Resource
    IPartnerService partnerService;

    private static final String CLIENT_LIST = "clientList";
    private static final String USER_NAME = "username";
    private static final String TASK_LIST = "taskList";

    /**
     * 获取算法参数
     *
     * @param optionReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "option", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> trainOption(@Validated @RequestBody TrainOptionReq optionReq) {
        Map<String, Object> request = new HashMap<>();
        request.put("algorithmType", optionReq.getAlgorithmType());
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.TRAIN_OPTION, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        List<String> featureList = featureService.queryFeatureAnswer(String.valueOf(optionReq.getTaskId()));
        Map data = (Map) res.get("data");
        List algoParams = (List) data.get("algorithmParams");
        Map<String, Object> single = new HashMap<>();
        single.put("field", "label");
        single.put("value", "y");
        single.put("describe", featureList);
        single.put("defaultValue", "y");
        single.put("name", "标签");
        single.put("type", ParameterType.STRING);
        algoParams.add(single);
        data.put("parameters", algoParams);
        res.put("data", data);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    /**
     * 自动训练，后台调用自动调参算法
     *
     * @param trainReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "auto", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> autoTrain(@Validated @RequestBody AutoTrainReq trainReq) {
        String taskId = String.valueOf(trainReq.getTaskId());
        List<PartnerInfoNew> partnerInfos = partnerService.queryPartnersByTaskId(taskId)
                .stream()
                .map(x -> {
                    FeatureDTO featureDTO = featureService.queryFeatureDTO(taskId, x);
                    String url = PartnerDO.convert2PartnerDTO(x).url();
                    return new PartnerInfoNew(url, x.getDataset(), featureDTO);
                })
                .collect(Collectors.toList());

        Map<String, Object> request = new HashMap<>();
        request.put(CLIENT_LIST, partnerInfos);
        request.put("matchId", trainReq.getMatchId());
        request.put("algorithm", trainReq.getAlgorithm());
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.AUTO_TRAIN, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    /**
     * 发起训练
     *
     * @param startReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "start", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> trainStart(@Validated @RequestBody TrainStartReq startReq) {
        String taskId = String.valueOf(startReq.getTaskId());
        List<PartnerInfoNew> partnerInfos = partnerService.queryPartnersByTaskId(taskId)
                .stream()
                .map(x -> {
                    FeatureDTO featureDTO = featureService.queryFeatureDTO(taskId, x);
                    String url = PartnerDO.convert2PartnerDTO(x).url();
                    return new PartnerInfoNew(url, x.getDataset(), featureDTO);
                })
                .collect(Collectors.toList());

        Map<String, Object> request = new HashMap<>();
        request.put(CLIENT_LIST, partnerInfos);
        request.put("taskId", startReq.getTaskId());
        request.put("model", startReq.getAlgorithm());
        request.put("matchId", startReq.getMatchId());
        request.put("algorithmParams", startReq.getParameters());
        logger.info("=-=-=-=-=-");
        logger.info(JsonUtil.object2json(request));
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.TRAIN_START, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    /**
     * 训练参数查询，支持查询进行中和完成等各种状态的训练任务
     * 支持用户点击查询单个训练详情和重新训练等场景，
     *
     * @param trainStatusReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "parameter", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> trainParameter(@Validated @RequestBody TrainCommonReq trainStatusReq) {
        Map<String, Object> request = new HashMap<>();
        request.put("modelToken", trainStatusReq.getTrainId());
        request.put("type", 2);
        String parameterRet = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.TRAIN_PARAMETER, request);
        //包含所有参数，开始时间等信息
        ModelMap parameterMap = JsonUtil.json2Object(parameterRet, ModelMap.class);
        Map data = (Map) parameterMap.get("data");

        data.put("startTime", data.get("trainStartTime"));
        data.put("parameters", data.get("algorithmParams"));
        data.put("algorithmType", data.get("model"));

        parameterMap.put("data", data);
        return ResponseEntity.status(HttpStatus.OK).body(parameterMap);
    }


    /**
     * 训练进度和状态详情查询，支持查询进行中和完成等各种状态的训练任务
     * 支持用户点击查询单个训练详情和重新训练等场景，
     *
     * @param trainStatusReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "progress", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> trainProgress(@Validated @RequestBody TrainCommonReq trainStatusReq) {
        Map<String, Object> request = new HashMap<>();
        request.put("modelToken", trainStatusReq.getTrainId());
        String metricRetStr = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.TRAIN_STATUS, request);
        //包含训练指标，进度，训练状态等
        ModelMap metricRet = JsonUtil.json2Object(metricRetStr, ModelMap.class);
        Map data = (Map) metricRet.get("data");

        ModelMap newData = new ModelMap();
        int percent = (int) data.get("percent");
        newData.put("percent", percent);
        newData.put("runningType", data.get("runningType"));
        List trainMetrics = (List) data.get("trainMetrics");
        List<String> desc = addDesc(percent, trainMetrics, trainStatusReq.getTrainId());
        newData.put("describes", desc);
        //TODO message 主要用于报错时
        newData.put("message", "ok");
        metricRet.put("data", newData);
        return ResponseEntity.status(HttpStatus.OK).body(metricRet);
    }


    public static List<String> addDesc(double percent, List metrics, String modelToken) {
        List<String> describes = new ArrayList<>();
        describes.add("modelToken is: " + modelToken);
        if (percent == 5) {
            describes.add("开始");
            return describes;
        }
        describes.add("开始");
        describes.add("参数初始化成功");
        if (percent == 7) {
            return describes;
        }
        if (metrics != null && metrics.size() > 0) {
            Map firstMetric = (Map) metrics.get(0);
            List firstMetricValue = (List) firstMetric.get("metric");
            int size = firstMetricValue.size();
            for (int j = 0; j < size; j++) {
                StringBuilder stringBuffer = new StringBuilder();
                for (int i = 0; i < metrics.size(); i++) {
                    Map metric = (Map) metrics.get(i);
                    List metricValue = (List) metric.get("metric");
                    Map singleMetricValue = (Map) metricValue.get(j);
                    if (i == 0) {
                        stringBuffer.append(String.format("第%s轮，",singleMetricValue.get("x")));
                    }
                    stringBuffer.append(metric.get("name")).append(": ").append(singleMetricValue.get("y"));
                    if (i != metrics.size() - 1) {
                        stringBuffer.append("; ");
                    }
                }
                if (!describes.contains(stringBuffer.toString())) {
                    describes.add(" " + stringBuffer);
                }
            }
        }
        if (percent == 100) {
            describes.add("训练结束");
        }
        return describes;
    }


    /**
     * 训练状态变更
     *
     * @param trainChangeReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "change", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> changeTask(@Validated @RequestBody TrainChangeReq trainChangeReq) {
        Map<String, Object> request = new HashMap<>();
        request.put("modelToken", trainChangeReq.getTrainId());
        request.put("type", trainChangeReq.getType());

        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.TRAIN_CHANGE, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * 查询id对齐列表
     *
     * @param trainListReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "list", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> trainList(@Validated @RequestBody TrainListReq trainListReq) {
        String username = trainListReq.getUsername();
        List<ProjectDO> tasks = (List<ProjectDO>) projectService.queryTaskListByUserName(username);
        List<String> collect = tasks.stream().map(t -> t.getId().toString()).collect(Collectors.toList());

        Map<String, Object> request = new HashMap<>();
        request.put(TASK_LIST, collect);
        request.put("type", trainListReq.getType());
        logger.info("======");
        logger.info(JsonUtil.object2json(request));
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.TRAIN_LIST, request);
        ModelMap res = projectService.addTaskName(modelMap);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    /**
     * 根据id删除训练，需满足训练状态为 COMPLETE 或者 FAIL 或者 STOP
     *
     * @param commonReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> deleteTrain(@Validated @RequestBody TrainCommonReq commonReq) {
        String username = commonReq.getUsername(); //TODO check username

        Map<String, Object> request = new HashMap<>();
        request.put("modelToken", commonReq.getTrainId());
        logger.info("======");
        logger.info(JsonUtil.object2json(request));
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.TRAIN_DELETE, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
