package com.jdt.fedlearn.frontend.entity.inference;

import java.util.List;

public class RealtimeInferenceReq {
    private String modelId;
    private List<String> uidList;
    private boolean secureMode;

    public RealtimeInferenceReq() {
    }

    public RealtimeInferenceReq(String modelId, List<String> uidList, boolean secureMode) {
        this.modelId = modelId;
        this.uidList = uidList;
        this.secureMode = secureMode;
    }

    public String getModelId() {
        return modelId;
    }

    public List<String> getUidList() {
        return uidList;
    }

    public boolean isSecureMode() {
        return secureMode;
    }


}
