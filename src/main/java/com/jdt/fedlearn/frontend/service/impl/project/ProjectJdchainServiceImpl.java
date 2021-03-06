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

package com.jdt.fedlearn.frontend.service.impl.project;

import com.jdt.fedlearn.frontend.entity.jdchain.ClientInfoFeatures;
import com.jdt.fedlearn.frontend.entity.jdchain.JdchainClientInfo;
import com.jdt.fedlearn.frontend.entity.jdchain.JdchainFeature;
import com.jdt.fedlearn.frontend.entity.jdchain.JdchainTask;
import com.jdt.fedlearn.frontend.util.JsonUtil;
import com.jdt.fedlearn.frontend.constant.Constant;
import com.jdt.fedlearn.frontend.entity.table.AccountDO;
import com.jdt.fedlearn.frontend.entity.table.FeatureDO;
import com.jdt.fedlearn.frontend.entity.table.MerchantDO;
import com.jdt.fedlearn.frontend.jdchain.config.JdChainCondition;
import com.jdt.fedlearn.frontend.mapper.project.ProjectJdchainMapper;
import com.jdt.fedlearn.frontend.entity.vo.ProjectListVO;
import com.jdt.fedlearn.frontend.service.IAccountService;
import com.jdt.fedlearn.frontend.service.IMerchantService;
import com.jdt.fedlearn.frontend.service.IProjectService;
import com.jdt.fedlearn.frontend.util.IdGenerateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Conditional(JdChainCondition.class)
@Service
public class ProjectJdchainServiceImpl implements IProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectJdchainServiceImpl.class);
    @Autowired
    ProjectJdchainMapper projectJdchainMapper;
    @Resource
    IAccountService accountService;
    @Resource
    IMerchantService merchantService;

    private Random random = new Random();

    /**
     * @param request
     * @description: ??????task??????????????? ????????????client?????????feature??????
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author: geyan29
     * @date: 2021/1/26 5:29 ??????
     */
    @Override
    public Map<String, Object> createTask(Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>(8);
        String taskId = IdGenerateUtil.randomDigitNumber();
        logger.info("taskId = {}", taskId);
        String username = (String) request.get(IProjectService.USER_NAME);
        AccountDO accountDO = accountService.queryAccount(username);
        String merCode = "";
        if (accountDO != null) {
            merCode = accountDO.getMerCode();
        }
        String taskName = (String) request.get(IProjectService.TASK_NAME);
        String pwd = (String) request.get(IProjectService.P_KEY);
        String hasPwd = (String) request.get(IProjectService.HAS_PKEY);
        String visible = (String) request.get(IProjectService.VISIBLE);
        String visibleMerCode = (String) request.get(IProjectService.VISIBLE_MER_CODE);
        String inferenceFlag = (String) request.get(IProjectService.INFERENCE_FLAG);
        rebuildMap(request, taskId, username);
        JdchainTask jdchainTask = new JdchainTask(taskId, username, taskName, hasPwd, pwd, visible, visibleMerCode, inferenceFlag, merCode);
        String jsonStr = JsonUtil.object2json(request);
        ClientInfoFeatures clientInfoFeatures = JsonUtil.json2Object(jsonStr, ClientInfoFeatures.class);
        List<ClientInfoFeatures> list = new ArrayList<>();
        list.add(clientInfoFeatures);
        jdchainTask.setClientInfoFeatures(list);
        String taskStr = JsonUtil.object2json(jdchainTask);
        projectJdchainMapper.createTask(taskId, taskStr);
        result.put(IProjectService.TASK_ID, taskId);
        return result;
    }

    /**
     * @description: ????????????????????????????????????????????????
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author: geyan29
     * @date: 2021/1/27 2:19 ??????
     */
    @Override
    public Map<String, Object> queryTaskList(ModelMap request) {
        String username = (String) request.get(IProjectService.USER_NAME);
        AccountDO accountDO = accountService.queryAccount(username);
        String merCode = accountDO != null ? accountDO.getMerCode() : "";
        String category = (String) request.get(IProjectService.CATEGORY);
        Map<String, Object> result = new HashMap<>(16);
        List<JdchainTask> jdchainTasks;
        /*?????????????????????task???username?????????????????????*/
        List<JdchainTask> allJdchainTasks = projectJdchainMapper.queryAllTask();
        if (IProjectService.OPTION.equals(category)) { //???????????????????????? ???????????????????????????????????????????????????
            jdchainTasks = allJdchainTasks.parallelStream()
                    /*????????????????????????????????????????????????????????????????????????????????????
                     * ???????????????????????????????????????????????????????????????*/
                    .filter(task -> ((Constant.TASK_VISIBLE_PUBLIC.equals(task.getVisible())
                            || (Constant.TASK_VISIBLE_PART.equals(task.getVisible()) && (task.getVisibleMerCode() != null && task.getVisibleMerCode().contains(merCode)))
                            || (Constant.TASK_INVISIBLE_PART.equals(task.getVisible()) && (task.getVisibleMerCode() != null && !task.getVisibleMerCode().equals(merCode))))
                            && !task.getUsername().equalsIgnoreCase(username)
                            && (task.getPartners() == null || !task.getPartners().contains(username))))
                    .sorted(Comparator.comparing(JdchainTask::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        } else if (IProjectService.JOINED.equals(category)) {//???????????????????????? ????????????????????? ???????????????????????????
            jdchainTasks = allJdchainTasks.parallelStream()
                    .filter(task -> !task.getUsername().equalsIgnoreCase(username) && task.getPartners() != null && task.getPartners().contains(username))
                    .sorted(Comparator.comparing(JdchainTask::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        } else if (IProjectService.INFERENCE.equals(category)) { //TODO ??????master????????????????????? ?????????????????? ???????????????????????????
            return result;
        } else {
            jdchainTasks = allJdchainTasks.parallelStream()
                    .filter(task -> task.getUsername().equalsIgnoreCase(username))
                    .sorted(Comparator.comparing(JdchainTask::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        List<ProjectListVO> resultList = packageTaskVO(jdchainTasks);
        result.put(IProjectService.TASK_LIST, resultList);
        return result;
    }

    /**
     * @param request
     * @description: ??????task??????
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author: geyan29
     * @date: 2021/1/27 3:18 ??????
     */
    @Override
    public Map<String, Object> queryTaskDetail(ModelMap request) {
        Map<String, Object> result = new HashMap<>(16);
        String taskId = (String) request.get(IProjectService.TASK_ID);
        JdchainTask jdchainTask = projectJdchainMapper.queryById(taskId);
        List<ClientInfoFeatures> clientInfoFeatures = jdchainTask.getClientInfoFeatures();
        List<FeatureDO> featureList = new ArrayList<>();
        List<JdchainClientInfo> clientList = new ArrayList<>();
        clientInfoFeatures.stream().forEach(entity -> {
            JdchainClientInfo jdchainClientInfo = entity.getClientInfo();
            if (jdchainClientInfo.getUsername().equals(jdchainTask.getUsername())) {
                clientList.add(jdchainClientInfo);
            }
            List<JdchainFeature> features = entity.getFeatures().getFeatureList();
            features.stream().forEach(feature -> {
                FeatureDO featureVo = new FeatureDO(Integer.parseInt(feature.getTaskId()), feature.getUsername(), feature.getName(), feature.getdType());
                featureList.add(featureVo);
            });
        });
        Map<String, Object> childResult = new HashMap<>(16);
        childResult.put(IProjectService.FEATURE_LIST, featureList);
        childResult.put(IProjectService.CLIENT_LIST, clientList);
        childResult.put(IProjectService.PARTICIPANTS, jdchainTask.getPartners());
        childResult.put(IProjectService.TASK_ID, jdchainTask.getTaskId());
        childResult.put(IProjectService.TASK_OWNER, jdchainTask.getUsername());
        childResult.put(IProjectService.TASK_NAME, jdchainTask.getTaskName());
        result.put(IProjectService.TASK, childResult);
        return result;
    }

    /**
     * @param request
     * @description: ????????????
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author: geyan29
     * @date: 2021/1/27 2:26 ??????
     */
    @Override
    public Map<String, Object> joinTask(Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>(8);
        String taskId = String.valueOf(request.get(IProjectService.TASK_ID));
        JdchainTask jdchainTask = projectJdchainMapper.queryById(taskId);
        String username = (String) request.get(IProjectService.USER_NAME);
        rebuildMap(request, taskId, username);
        request.remove(IProjectService.USER_NAME);
        /*????????????????????????????????????feature??????*/
        String jsonStr = JsonUtil.object2json(request);
        ClientInfoFeatures clientInfoFeatures = JsonUtil.json2Object(jsonStr, ClientInfoFeatures.class);
        List<ClientInfoFeatures> list = jdchainTask.getClientInfoFeatures();
        list.add(clientInfoFeatures);
        jdchainTask.setClientInfoFeatures(list);
        /* ???????????????*/
        List<String> partners = jdchainTask.getPartners();
        if (partners == null) {
            List newPartners = new ArrayList();
            newPartners.add(username);
            jdchainTask.setPartners(newPartners);
        } else {
            partners.add(username);
        }
        jdchainTask.setUpdateTime(new Date());
        String taskStr = JsonUtil.object2json(jdchainTask);
        projectJdchainMapper.createTask(taskId, taskStr);
        result.put(IProjectService.TASK_ID, taskId);
        return result;
    }

    @Override
    public JdchainTask queryTaskById(String taskId) {
        JdchainTask jdchainTask = projectJdchainMapper.queryById(taskId);
        return jdchainTask;
    }

    @Override
    public List<JdchainTask> queryTaskListByUserName(String userName) {
        /*?????????????????????task???username?????????????????????*/
        List<JdchainTask> allJdchainTasks = projectJdchainMapper.queryAllTask();
        List<JdchainTask> collect = allJdchainTasks.parallelStream()
                .filter(task -> userName.equalsIgnoreCase(task.getUsername()) || (task.getPartners() != null && task.getPartners().contains(userName)))
                .sorted(Comparator.comparing(JdchainTask::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * @param request
     * @param taskId
     * @param username
     * @description: ??????request???????????????
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author: geyan29
     * @date: 2021/1/27 3:01 ??????
     */
    private Map<String, Object> rebuildMap(Map<String, Object> request, String taskId, String username) {
        /* ??????clientInfo???*/
        Map clientInfo = (Map) request.get(IProjectService.CLIENT_INFO);
        clientInfo.put(IProjectService.DATASET, request.get(IProjectService.DATASET));
        clientInfo.put(IProjectService.TOKEN, random.nextInt());
        clientInfo.put(IProjectService.USER_NAME, username);
        /*??????Fearture?????? */
        Map features = (Map) request.get(IProjectService.FEATURES);
        List<Map> featureList = (List) features.get(IProjectService.FEATURE_LIST);
        featureList.stream().forEach(feature -> {
            feature.put(IProjectService.TASK_ID, taskId);
            feature.put(IProjectService.USER_NAME, username);
        });
        request.remove(IProjectService.USER_NAME);
        request.remove(IProjectService.HAS_PKEY);
        request.remove(IProjectService.VISIBLE);
        request.remove(IProjectService.INFERENCE_FLAG);
        request.remove(IProjectService.TASK_NAME);
        request.remove(IProjectService.TASK_ID);
        return request;
    }

    /**
     * @param jdchainTasks
     * @description: ??????????????????????????????????????????????????????VO
     * @return: java.util.List<com.jdt.fedlearn.frontend.entity.vo.JdchainTaskVo>
     * @author: geyan29
     * @date: 2021/1/27 3:17 ??????
     */
    private List<ProjectListVO> packageTaskVO(List<JdchainTask> jdchainTasks) {
        List<ProjectListVO> resultList = new ArrayList<>();
        /* ???????????????task???????????????????????????*/
        jdchainTasks.stream().forEach(task -> {
            ProjectListVO jdchainTaskVo = new ProjectListVO();
            jdchainTaskVo.setOwner(task.getUsername());
            jdchainTaskVo.setTaskId(Integer.parseInt(task.getTaskId()));
            jdchainTaskVo.setTaskName(task.getTaskName());
            jdchainTaskVo.setHasPwd(task.getHasPwd());
            jdchainTaskVo.setVisible(task.getVisible());
            jdchainTaskVo.setVisibleMerName(codeConvertName(task.getVisibleMerCode()));
            jdchainTaskVo.setInferenceFlag(task.getInferenceFlag());
            if (task.getPartners() != null && task.getPartners().size() > 0) {
                jdchainTaskVo.setParticipants(task.getPartners().toArray(new String[task.getPartners().size()]));
            } else {
                jdchainTaskVo.setParticipants(new String[]{});
            }
            resultList.add(jdchainTaskVo);
        });
        return resultList;
    }

    private static final String COMMA = ",";

    /***
     * @description: ????????????????????????????????????
     * @param codes
     * @return: java.lang.String
     * @author: geyan29
     * @date: 2021/3/17 3:17 ??????
     */
    private String codeConvertName(String codes) {
        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(codes)) {
            String[] codeArr = codes.split(COMMA);
            for (int j = 0; j < codeArr.length; j++) {
                MerchantDO merchant = merchantService.queryMerchantByCode(codeArr[j]);
                String merchantName = merchant.getName();
                if (j == codeArr.length - 1) {
                    stringBuffer.append(merchantName);
                } else {
                    stringBuffer.append(merchantName).append(COMMA);
                }
            }
        }
        return stringBuffer.toString();
    }
}
