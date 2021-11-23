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

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.jdt.fedlearn.frontend.constant.RequestConstant;
import com.jdt.fedlearn.frontend.constant.ResponseConstant;
import com.jdt.fedlearn.frontend.constant.ResponseHandler;
import com.jdt.fedlearn.frontend.entity.project.SingleFeatureDTO;
import com.jdt.fedlearn.frontend.entity.system.CheckFeature;
import com.jdt.fedlearn.frontend.entity.system.DatasetQueryReq;
import com.jdt.fedlearn.frontend.entity.system.ParameterQueryReq;
import com.jdt.fedlearn.frontend.entity.table.ProjectDO;
import com.jdt.fedlearn.frontend.jdchain.config.JdChainFalseCondition;
import com.jdt.fedlearn.frontend.service.IProjectService;
import com.jdt.fedlearn.frontend.util.HttpClientUtil;
import com.jdt.fedlearn.frontend.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 请求转发Controller，主要是将前端的请求转发到训练系统，
 * 部分前端页面和后台不兼容的地方在此处做适配处理
 */
@Conditional(JdChainFalseCondition.class)
@RestController
@RequestMapping("api/system")
public class SystemController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${baseUrl}")
    private String baseUrl;
    @Value("${logging.file.path}")
    String logPath;
    @Value("${logging.file.name}")
    String logFile;

    @Resource
    IProjectService projectService;
    @Resource
    private DefaultKaptcha captchaProducer;


    /**
     * 获取通用参数
     *
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "parameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<ModelMap> commonParameter(@Validated @RequestBody ParameterQueryReq queryReq) {
        String username = queryReq.getUsername();//TODO check username
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.COMMON_PARAMETER, null);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    private static final String URL = "url";

    /**
     * @param queryReq request body
     * @return TODO 请求中移除任务密码并移动到加入任务接口中
     */
    @RequestMapping(value = "dataset", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<ModelMap> queryDataset(@Validated @RequestBody DatasetQueryReq queryReq) {
        ModelMap res = new ModelMap();
        int taskId = queryReq.getTaskId();
        if (taskId != 0) {
            String taskIdStr = String.valueOf(taskId);
            String taskPwd = queryReq.getTaskPwd();
            ProjectDO task = (ProjectDO) projectService.queryTaskById(taskIdStr);
            if (StringUtils.isNotBlank(taskPwd) && !taskPwd.equals(task.getTaskPwd())) {
                res.put(ResponseConstant.DATA, "任务密码错误,无法加入任务！");
                res.put(ResponseConstant.STATUS, ResponseConstant.FAIL);
                res.put(ResponseConstant.CODE, ResponseConstant.FAIL_CODE);
                return ResponseEntity.status(HttpStatus.OK).body(res);
            }
        }

        Map<String, Object> request = new HashMap<>();
        request.put(URL, queryReq.getClientUrl());
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.QUERY_DATASET, request);
        res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * @description: 读取日志文件内容
     * @param request
     * @return: org.springframework.http.ResponseEntity<org.springframework.ui.ModelMap>
     * @author: geyan29
     * @date: 2021/11/8 3:47 下午
     */
    private static String CONTENT = "content";

    @RequestMapping(value = "log", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<ModelMap> queryLog(@RequestBody Map<String, Object> request) throws IOException {
        String username = (String) request.get("username");
        String path = logPath + logFile;
        ModelMap res = new ModelMap();
        StringBuffer stringBuffer = new StringBuffer();
        List<String> content = new ArrayList<>();
        InputStream inputStream = new FileInputStream(path);
        try (Reader in = new InputStreamReader(inputStream, StandardCharsets.UTF_8); LineNumberReader reader = new LineNumberReader(in)) {
            String lineStr;
            while ((lineStr = reader.readLine()) != null) {
                int i = reader.getLineNumber();
                stringBuffer.append(lineStr).append("</br>");
                content.add(lineStr);
            }
        }
        Map<String, Object> data = new HashMap<>(8);
        data.put(CONTENT, content);
        res.put(ResponseConstant.DATA, data);
        res.put(ResponseConstant.STATUS, ResponseConstant.SUCCESS);
        res.put(ResponseConstant.CODE, ResponseConstant.SUCCESS_CODE);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /** 获取图片验证码**/
    @RequestMapping("/defaultKaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception{
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String createText = captchaProducer.createText();
//            logger.info("图片验证码内容为：{}",createText);
            httpServletRequest.getSession().setAttribute("verifyCode", createText);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = captchaProducer.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND); return;
        }
        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    @RequestMapping(value = "check/feature", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<ModelMap> checkFeature(@RequestBody CheckFeature checkFeature) throws IOException {
        ModelMap resultMap;
        String newFeature = checkFeature.getNewFeature();
        List<SingleFeatureDTO> featureList = checkFeature.getFeatureList();
        List<String> nameList = featureList.parallelStream().map(SingleFeatureDTO::getName).collect(Collectors.toList());
        Map<String,Object> param = new HashMap<>();
        param.put("expr",newFeature);
        param.put("featureList",nameList);
        String result = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.CHECK_FEATURE, param);
        Map map = JsonUtil.object2map(result);
        int code = (int) map.get(ResponseConstant.CODE);
        if(ResponseConstant.SUCCESS_CODE == code){
            resultMap = ResponseHandler.successResponse(map.get(ResponseConstant.DATA));
        }else {
            resultMap = ResponseHandler.failResponse(map.get(ResponseConstant.DATA));
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }
}
