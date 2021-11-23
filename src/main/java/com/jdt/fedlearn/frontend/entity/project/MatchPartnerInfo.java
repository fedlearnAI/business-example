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

package com.jdt.fedlearn.frontend.entity.project;


import java.util.Objects;

/**
 * 新版开始id对齐的请求
 * 删除token，把ip port protocol合并成url
 * 后续所有partnerinfo都改成这种格式
 */
public class MatchPartnerInfo {
    private String url;
    private String index;
    private String dataset;

    public MatchPartnerInfo() {

    }

    public MatchPartnerInfo(String url, String dataset, String index) {
        this.url = url;
        this.dataset = dataset;
        this.index = index;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MatchPartnerInfo that = (MatchPartnerInfo) o;
        return Objects.equals(url, that.url) && Objects.equals(index, that.index) && Objects.equals(dataset, that.dataset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, index, dataset);
    }
}
