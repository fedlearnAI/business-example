package com.jdt.fedlearn.frontend.entity.system;

import com.jdt.fedlearn.frontend.entity.project.SingleFeatureDTO;

import java.util.List;

public class CheckFeature {
    String newFeature;
    List<SingleFeatureDTO> featureList;

    public String getNewFeature() {
        return newFeature;
    }

    public void setNewFeature(String newFeature) {
        this.newFeature = newFeature;
    }

    public List<SingleFeatureDTO> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<SingleFeatureDTO> featureList) {
        this.featureList = featureList;
    }
}
