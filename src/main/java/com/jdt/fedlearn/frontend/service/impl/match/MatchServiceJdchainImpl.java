package com.jdt.fedlearn.frontend.service.impl.match;

import com.jd.blockchain.ledger.TypedKVEntry;
import com.jdt.fedlearn.frontend.constant.Constant;
import com.jdt.fedlearn.frontend.constant.JdChainConstant;
import com.jdt.fedlearn.frontend.constant.RequestConstant;
import com.jdt.fedlearn.frontend.entity.match.*;
import com.jdt.fedlearn.frontend.entity.project.MatchPartnerInfo;
import com.jdt.fedlearn.frontend.entity.project.PartnerDTO;
import com.jdt.fedlearn.frontend.exception.RandomServerException;
import com.jdt.fedlearn.frontend.jdchain.config.JdChainCondition;
import com.jdt.fedlearn.frontend.mapper.JdChainBaseMapper;
import com.jdt.fedlearn.frontend.service.IMatchService;
import com.jdt.fedlearn.frontend.service.IPartnerService;
import com.jdt.fedlearn.frontend.util.HttpClientUtil;
import com.jdt.fedlearn.frontend.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Conditional(JdChainCondition.class)
@Service
public class MatchServiceJdchainImpl implements IMatchService {
    private static Logger logger = LoggerFactory.getLogger(MatchServiceJdchainImpl.class);
    @Resource
    private IPartnerService partnerService;
    @Resource
    JdChainBaseMapper jdChainBaseMapper;
    @Override
    public String matchStart(MatchStart matchStart) {
        String taskId = matchStart.getTaskId();
        String userName = matchStart.getUsername();
        String matchAlgorithm = matchStart.getMatchType();
        String url = randomServer(userName, taskId, matchAlgorithm);
        List<PartnerDTO> partnerDTOS = partnerService.queryPartnerDTOList(taskId);
        List<MatchPartnerInfo> clientInfosNew = partnerDTOS.stream().map(x -> new MatchPartnerInfo(x.url(), x.getDataset(), "uid")).collect(Collectors.toList());
        Map<String, Object> request = new HashMap<>();
        request.put(TASK_ID, taskId);
        request.put(MATCH_ALGORITHM, matchStart.getMatchType());
        request.put(CLIENT_LIST, clientInfosNew);
        logger.info("random server is {}", url);
        String modelMap = HttpClientUtil.doHttpPost(url + RequestConstant.MATCH_START, request);
        return modelMap;
    }

    @Override
    public String matchProgress(MatchProgressReq matchProgressReq) {
        String matchId = matchProgressReq.getMatchId();
        String taskId = matchId.substring(0, matchId.indexOf("-"));
        Map<String, Object> request = new HashMap<>();
        request.put(MATCH_ID, matchProgressReq.getMatchId());
        String url = getRandomServer(taskId);
        String modelMap = HttpClientUtil.doHttpPost(url + RequestConstant.MATCH_PROGRESS, request);
        return modelMap;
    }

    @Override
    public String detail(MatchDetailReq matchProgressReq) {
        String matchId = matchProgressReq.getMatchId();
        String taskId = matchId.substring(0, matchId.indexOf("-"));
        Map<String, Object> request = new HashMap<>();
        request.put(MATCH_ID, matchProgressReq.getMatchId());
        String url = getRandomServer(taskId);
        String modelMap = HttpClientUtil.doHttpPost(url + RequestConstant.MATCH_PROGRESS, request);
        return modelMap;
    }

    @Override
    public String matchList(MatchListReq matchListReq) {

        return null;
    }

    @Override
    public String matchDelete(MatchDeleteReq deleteReq) {
        return null;
    }

    private String randomServer(String userName, String taskId, String matchAlgorithm) {
        String result = jdChainBaseMapper.invokeRandomtraining(userName, taskId, matchAlgorithm);
        return parseRandomServer(result);
    }

    private String getRandomServer(String taskId) {
        String queryKey = JdChainConstant.INVOKE_RANDOM_TRAINING + JdChainConstant.SEPARATOR + taskId + JdChainConstant.SEPARATOR + JdChainConstant.FRONT;
        TypedKVEntry typedKVEntry = jdChainBaseMapper.queryByChaincode(queryKey);
        if (typedKVEntry != null) {
            String result = (String) typedKVEntry.getValue();
            return parseRandomServer(result);
        } else {
            throw new RandomServerException("queryKey:" + queryKey + "server not found!");
        }
    }

    private static final String IDENTITY = "identity";
    private static final String API = "/api/";
    private String parseRandomServer(String result) {
        ModelMap modelMap = JsonUtil.json2Object(result, ModelMap.class);
        String server = (String) JsonUtil.object2map(modelMap.get(JdChainConstant.SERVER)).get(IDENTITY);
//        String server = "127.0.0.1:8092";
        return Constant.HTTP_PREFIX + server + API;
    }
}
