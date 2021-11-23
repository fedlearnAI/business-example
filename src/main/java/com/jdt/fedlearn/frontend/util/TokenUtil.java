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

package com.jdt.fedlearn.frontend.util;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * token相关工具类
 *
 */
public class TokenUtil {

    public static final int RANDOM_NUMBER_ORIGIN = 100000;
    public static final int RANDOM_NUMBER_BOUND = 999999;
    public static final String TOKEN_STR = "tokenStr";
    public static final String SPLIT_ARRAY = "splitArray";
    public static final String MSG = "tokenStr格式不正确";
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static Random random = new Random();

    /**
     * 初始化构造方法
     */
    private TokenUtil() {

    }

    public static final String SPILT = "-";
    public static final String FORMAT = "yyMMddHHmmss";




    /**
     * 获取随机数
     *
     * @return 生成的随机数
     */
    private static int getRandom() {
        return ThreadLocalRandom.current().ints(RANDOM_NUMBER_ORIGIN, RANDOM_NUMBER_BOUND)
                .distinct().limit(ONE).findFirst().getAsInt();
    }


    public static Integer generateClientInfoToken() {
        return random.nextInt();
    }
}
