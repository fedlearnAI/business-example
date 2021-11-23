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
import com.jdt.fedlearn.frontend.constant.ResponseHandler;
import com.jdt.fedlearn.frontend.entity.privacyInfer.RealtimePriInferReq;
import com.jdt.fedlearn.frontend.entity.project.PartnerInfoNew;
import com.jdt.fedlearn.frontend.entity.table.ProjectDO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请求转发Controller，主要是将前端的请求转发到训练系统，
 * 部分前端页面和后台不兼容的地方在此处做适配处理
 */
@Conditional(JdChainFalseCondition.class)
@RestController
@RequestMapping("test/api/privacyInfer")
public class PrivacyInferController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${baseUrl}")
    private String baseUrl;
    @Resource
    IProjectService projectService;
    @Resource
    IPartnerService partnerService;


    private static final String CLIENT_LIST = "clientList";
    private static final String USER_NAME = "username";
    private static final String MODEL_TOKEN = "modelToken";
    private static final String MODEL = "model";
    private static final String UID = "uid";

    /**
     * 预测
     *
     * @param priInferReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "realtime", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> predict(@Validated @RequestBody RealtimePriInferReq priInferReq) {

        String modelId = priInferReq.getModelId();
        String taskId = modelId.split("-")[0];
        List<PartnerInfoNew> partnerInfos = partnerService.queryPartnerDTOList(taskId).stream()
                .map(x -> new PartnerInfoNew(x.url(), x.getDataset()))
                .collect(Collectors.toList());


        Map<String, Object> request = new HashMap<>();
        request.put(CLIENT_LIST, partnerInfos);
        logger.info(JsonUtil.object2json(request) + " ====");
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.INFERENCE_BATCH, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }



    private static final String TASK_LIST = "taskList";
    /**
     * 已训练完成的模型查询
     *
     * @param request 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "query", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> fetchModel(@Validated @RequestBody Map<String, Object> request) {
        String username = request.get(USER_NAME).toString();
        List<ProjectDO> tasks = projectService.queryTaskListByOwner(username);
        List<Integer> taskIds = tasks.parallelStream().map(ProjectDO::getId).collect(Collectors.toList());
        request.remove(USER_NAME);
        request.put(TASK_LIST, taskIds);
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.TRAIN_LIST, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        Map data = (Map) res.get(projectService.DATA);
        List trainList = (List) data.get(IProjectService.TRAIN_LIST);
        List<String> models = new ArrayList<>();
        for (Object train : trainList) {
            Map trainMap = (Map) train;
            String runningStatus = trainMap.get("runningStatus").toString();
            if ("COMPLETE".equalsIgnoreCase(runningStatus)) {
                models.add((String) trainMap.get("modelToken"));
            }
        }
        Map<String, List<String>> resMap = new HashMap<>();
        resMap.put("models", models);
        ModelMap result = ResponseHandler.successResponse(resMap);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
