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
import com.jdt.fedlearn.frontend.entity.match.*;
import com.jdt.fedlearn.frontend.service.IMatchService;
import com.jdt.fedlearn.frontend.util.JsonUtil;
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

/**
 * id对齐，包含
 */
@RestController
@RequestMapping("api/match")
public class MatchController {
    @Resource
    IMatchService matchService;

    /**
     * 发起id对齐
     *
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "start", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> matchStart(@Validated @RequestBody MatchStart matchStart) {
        String modelMap = matchService.matchStart(matchStart);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * id对齐进度查询
     *
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "progress", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> matchProgress(@Validated @RequestBody MatchProgressReq matchProgressReq) {
        String modelMap = matchService.matchProgress(matchProgressReq);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * id对齐详情查询
     *
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "detail", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> matchDetail(@Validated @RequestBody MatchDetailReq detailReq) {
        String modelMap = matchService.detail(detailReq);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);

        ModelMap fake = new ModelMap();
        fake.put("code", 0);
        fake.put("status", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("matchType", "MD5");
        data.put("taskId", 1000);
        data.put("taskName", "AAAFake");
        fake.put("data", data);
        return ResponseEntity.status(HttpStatus.OK).body(fake);
    }


    /**
     * id对齐列表查询接口
     *
     * @return ResponseEntity<Map>
     */
    @RequestMapping(value = "list", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> matchList(@Validated @RequestBody MatchListReq matchListReq) {
        String modelMap = matchService.matchList(matchListReq);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        //TODO 临时修改，后续训练系统修改后此处删除
        Map data = (Map) res.get("data");
        List matchList = (List) data.get("matchList");
        List newMatchList = new ArrayList();
        for (Object matchEle : matchList) {
            Map oldEle = (Map) matchEle;
            Map newEle = new HashMap();
            newEle.put("taskId", oldEle.get("taskId"));
            newEle.put("runningType", oldEle.get("runningStatus"));
            newEle.put("describe", "to be continue");
            newEle.put("matchId", oldEle.get("matchId"));
            newMatchList.add(newEle);
        }
        data.put("matchList", newMatchList);
        res.put("data", data);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    /**
     * id对齐列表查询接口
     *
     * @return ResponseEntity<Map>
     * TODO 后续会优化前端，
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST, produces = RequestConstant.HEADER)
    @ResponseBody
    public ResponseEntity<ModelMap> matchDelete(@Validated @RequestBody MatchDeleteReq deleteReq) {
        String modelMap = matchService.matchDelete(deleteReq);
        ModelMap res = JsonUtil.json2Object(modelMap, ModelMap.class);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
