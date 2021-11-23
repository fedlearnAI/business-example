package com.jdt.fedlearn.frontend.service.impl.match;

import com.jdt.fedlearn.frontend.constant.RequestConstant;
import com.jdt.fedlearn.frontend.entity.match.*;
import com.jdt.fedlearn.frontend.entity.project.MatchPartnerInfo;
import com.jdt.fedlearn.frontend.entity.project.PartnerDTO;
import com.jdt.fedlearn.frontend.entity.table.ProjectDO;
import com.jdt.fedlearn.frontend.jdchain.config.JdChainFalseCondition;
import com.jdt.fedlearn.frontend.service.IMatchService;
import com.jdt.fedlearn.frontend.service.IPartnerService;
import com.jdt.fedlearn.frontend.service.IProjectService;
import com.jdt.fedlearn.frontend.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Conditional(JdChainFalseCondition.class)
@Service
public class MatchServiceDbImpl implements IMatchService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${baseUrl}")
    private String baseUrl;
    @Resource
    private IPartnerService partnerService;
    @Resource
    private IProjectService projectService;

    public String matchStart(MatchStart matchStart) {
        String taskId = matchStart.getTaskId();
        List<PartnerDTO> partnerDTOS = partnerService.queryPartnerDTOList(taskId);
        List<MatchPartnerInfo> clientInfosNew = partnerDTOS.stream().map(x -> new MatchPartnerInfo(x.url(), x.getDataset(), "uid")).collect(Collectors.toList());
        Map<String, Object> request = new HashMap<>();
        request.put(TASK_ID, taskId);
        request.put(MATCH_ALGORITHM, matchStart.getMatchType());
        request.put(CLIENT_LIST, clientInfosNew);
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.MATCH_START, request);
        return modelMap;
    }

    @Override
    public String matchProgress(MatchProgressReq matchProgressReq) {
        Map<String, Object> request = new HashMap<>();
        request.put(MATCH_ID, matchProgressReq.getMatchId());
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.MATCH_PROGRESS, request);
        return modelMap;
    }


    @Override
    public String detail(MatchDetailReq detailReq) {
        Map<String, Object> request = new HashMap<>();
        request.put(MATCH_ID, detailReq.getMatchId());
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.MATCH_PROGRESS, request);
        return modelMap;
    }

    @Override
    public String matchList(MatchListReq matchListReq) {
        Map<String, Object> request = new HashMap<>();
        //TODO 如果taskList为空，则根据username查询该用户所属的全部任务
        //去除空字符串
        List<String> taskList = matchListReq.getTaskList().stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
        if (taskList.size() == 0) {
            List<ProjectDO> projectDOList = projectService.queryTaskListByOwner(matchListReq.getUsername());
            taskList = projectDOList.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        }
        request.put(TYPE, matchListReq.getRunningType());
        logger.info("taskList:" + taskList);
        request.put(TASK_LIST, taskList);
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.MATCH_LIST, request);
        return modelMap;
    }

    @Override
    public String matchDelete(MatchDeleteReq deleteReq) {
        Map<String, Object> request = new HashMap<>();
        request.put(MATCH_ID, deleteReq.getMatchId());
        String modelMap = HttpClientUtil.doHttpPost(baseUrl + RequestConstant.MATCH_DELETE, request);
        return modelMap;
    }
}
