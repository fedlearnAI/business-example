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

package com.jdt.fedlearn.frontend.entity.train.metric;

/**
 * 单一数值型指标，x代表训练轮数，y代表当前轮数的指标
 * 如指标RMSE x=1,y=4.5,即RMSE这一指标在第一轮的精度为4.5
 */
public class SingleMetric implements MetricPair {
    private int x;
    private double y;

    public SingleMetric() {
    }

    public SingleMetric(int x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String roundString() {
        return String.valueOf(x);
    }

    public String metricString() {
        return String.valueOf(y);
    }
}

