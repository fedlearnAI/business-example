package com.jdt.fedlearn.frontend.constant;

public interface RequestConstant {
    String HEADER = "application/json; charset=utf-8";

    //system api start
    String COMMON_PARAMETER = "prepare/parameter/common";
    String QUERY_DATASET = "system/dataset";
    // system api end

    // id match start
    String MATCH_START = "prepare/match/start";
    String MATCH_PROGRESS = "prepare/match/progress";
    String MATCH_LIST = "prepare/match/list";
    String MATCH_DELETE = "prepare/match/delete";
    // id match end

    // train start
    String TRAIN_OPTION = "prepare/parameter/algorithm";
    String AUTO_TRAIN = "train/auto";
    String TRAIN_START = "train/start";
    String TRAIN_STATUS = "train/status";
    String TRAIN_PARAMETER = "train/parameter";

    String TRAIN_LIST = "train/list";
    String TRAIN_CHANGE = "train/change";
    String TRAIN_DELETE = "train/delete";
    //train end

    //normal inference start
    String INFERENCE_BATCH = "inference/batch";
    String INFERENCE_REMOTE = "inference/remote";
   String INFERENCE_PROGRESS = "inference/progress";
    String CHECK_FEATURE = "system/check/feature";
    //privacy inference start

}
