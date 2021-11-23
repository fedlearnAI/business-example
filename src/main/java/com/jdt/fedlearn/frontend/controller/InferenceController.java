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
import com.jdt.fedlearn.frontend.entity.inference.RealtimeInferenceReq;
import com.jdt.fedlearn.frontend.entity.inference.RemoteInferenceReq;
import com.jdt.fedlearn.frontend.entity.inference.RemoteProgressReq;
import com.jdt.fedlearn.frontend.entity.project.PartnerDTO;
import com.jdt.fedlearn.frontend.entity.project.PartnerInfoNew;
import com.jdt.fedlearn.frontend.entity.table.ProjectDO;
import com.jdt.fedlearn.frontend.exception.NotAcceptableException;
import com.jdt.fedlearn.frontend.jdchain.config.JdChainFalseCondition;
import com.jdt.fedlearn.frontend.service.IFeatureService;
import com.jdt.fedlearn.frontend.service.IPartnerService;
import com.jdt.fedlearn.frontend.service.IProjectService;
import com.jdt.fedlearn.frontend.util.FileUtil;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 请求转发Controller，主要是将前端的请求转发到训练系统，
 * 部分前端页面和后台不兼容的地方在此处做适配处理
 */
@Conditional(JdChainFalseCondition.class)
@RestController
@RequestMapping("test/api/inference")
public class InferenceController {
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
    private static final String MODEL_TOKEN = "modelToken";
    private static final String MODEL = "model";
    private static final String UID = "uid";
    private static final String USER_ADDRESS = "userAddress";

    /**
     * 预测
     *
     * @param inferenceReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "realtime", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> predict(@Validated @RequestBody RealtimeInferenceReq inferenceReq) {
        String modelId = inferenceReq.getModelId();
        String taskId = modelId.split("-")[0];
        List<PartnerInfoNew> partnerInfos = partnerService.queryPartnerDTOList(taskId)
                .stream()
                .map(x -> new PartnerInfoNew(x.url(), x.getDataset()))
                .collect(Collectors.toList());

        Map<String, Object> request = new HashMap<>();
        request.put(CLIENT_LIST, partnerInfos);
        request.put("uid", inferenceReq.getUidList());
        request.put("secureMode", inferenceReq.isSecureMode());
        logger.info(JsonUtil.object2json(request) + " ====");
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.INFERENCE_BATCH, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    /**
     * 批量远端推测
     *
     * @param inferenceReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "remote", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> predictRemote(@Validated @RequestBody RemoteInferenceReq inferenceReq) {
        String modelId = inferenceReq.getModelId();
        String taskId = modelId.split("-")[0];
        String username = inferenceReq.getUsername();
        List<PartnerInfoNew> partnerInfos = partnerService.queryPartnerDTOList(taskId).stream()
                .map(x -> new PartnerInfoNew(x.url(), x.getDataset()))
                .collect(Collectors.toList());
        PartnerDTO partnerDTO = partnerService.queryPartnerDTO(taskId, username);
        PartnerInfoNew partnerInfoNew = new PartnerInfoNew(partnerDTO.url(), partnerDTO.getDataset());

        Map<String, Object> request = new HashMap<>();
        request.put(CLIENT_LIST, partnerInfos);
        request.put(USER_ADDRESS, partnerInfoNew.getUrl());
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.INFERENCE_REMOTE, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    /**
     * 预测进度查询
     *
     * @param progressReq 请求
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "progress", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    public ResponseEntity<ModelMap> predictQuery(@Validated @RequestBody RemoteProgressReq progressReq) {
        String inferenceId = progressReq.getInferenceId();

        Map<String, Object> request = new HashMap<>();
        request.put("inferenceId", inferenceId);
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.INFERENCE_PROGRESS, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    /**
     * 预测文件上传和结果下载
     *
     * @param file  文件
     * @param model 模型
     */
    @RequestMapping(value = "inference/download", method = RequestMethod.POST)
    public void predictUpload(@RequestParam("file") MultipartFile file, String model, String username, HttpServletResponse response)
            throws IllegalStateException, IOException {
        // 读取文件
        List<String> content = FileUtil.getBodyData(file.getInputStream());
        List<String> uidList = FileUtil.readFirstColumn(content);
        String taskId = model.split("-")[0];
        List<PartnerInfoNew> partnerInfos = partnerService.queryPartnerDTOList(taskId).stream()
                .map(x -> new PartnerInfoNew(x.url(), x.getDataset()))
                .collect(Collectors.toList());
        // 调用预测接口
        Map<String, Object> request = new HashMap<>();
        request.put(UID, uidList);
        request.put(MODEL_TOKEN, model);
        request.put(CLIENT_LIST, partnerInfos);
        request.remove(USER_NAME);
        request.remove(MODEL);
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + "inference/batch", request);
        ModelMap predictMap = JsonUtil.json2Object(modelMap, ModelMap.class);
        final Integer code = (Integer) predictMap.get("code");
        if (code != 0) {
            throw new NotAcceptableException("调用接口失败");
        }
        final Map<String, List<Map<String, Object>>> data = (Map<String, List<Map<String, Object>>>) predictMap.get("data");
        final List<Map<String, Object>> resultData = data.get("predict");
        String lines = FileUtil.list2Lines(resultData);

        // 文件下载
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=predict.txt");
        OutputStream outputStream = response.getOutputStream();
        final byte[] bytes = lines.getBytes(StandardCharsets.UTF_8);
        outputStream.write(bytes, 0, bytes.length);
        outputStream.flush();
        outputStream.close();
    }

    //日志查询
    private static final String INFERENCE_LOG = "inference/query/log";

    @RequestMapping(value = INFERENCE_LOG, method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<ModelMap> queryInferenceLog(@Validated @RequestBody Map<String, Object> request) {
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + INFERENCE_LOG, request);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    /**
     * 已训练完成的模型查询
     *
     * @param request 请求
     * @return ResponseEntity<Map>
     */
    private static final String QUERY_MODEL = "inference/query/model";
    private static final String TASK_LIST = "taskList";

    @RequestMapping(value = QUERY_MODEL, method = RequestMethod.POST, produces = "application/json; charset=utf-8")
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
