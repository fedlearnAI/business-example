package com.jdt.fedlearn.frontend.entity.privacyInfer;

import java.util.List;

public class RealtimePriInferReq {
    private String modelId;
    private List<String> uidList;

    public RealtimePriInferReq() {
    }

    public RealtimePriInferReq(String modelId, List<String> uidList) {
        this.modelId = modelId;
        this.uidList = uidList;
    }

    public String getModelId() {
        return modelId;
    }

    public List<String> getUidList() {
        return uidList;
    }


}
