package com.jdt.fedlearn.frontend.service;

import com.jdt.fedlearn.frontend.entity.match.*;

/**
 * @className: IMatchService
 * @description: id对齐相关service
 * @author: geyan29
 * @createTime: 2021/9/27 4:42 下午
 */
public interface IMatchService {

    String TASK_ID = "taskId";
    String MATCH_ALGORITHM = "matchAlgorithm";
    String CLIENT_LIST = "clientList";
    String TYPE = "type";
    String MATCH_ID = "matchId";
    String TASK_LIST = "taskList";

    String matchStart(MatchStart matchStart);

    String matchProgress(MatchProgressReq matchProgressReq);

    String detail(MatchDetailReq detailReq);

    String matchList(MatchListReq matchListReq);

    String matchDelete(MatchDeleteReq deleteReq);
}
