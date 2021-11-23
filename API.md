# 京东数科自研联邦学习（FedLearn）API文档


目前前端的接口分为三大部分，分别是权限系统(authority)与任务(task)，对齐(psi)与训练(train)，推理(inference),  除此以外还有部分系统状态(system)接口。

详见下文叙述。

### 一、权限系统
#### 1.1  用户注册

|meta |  协议 | HTTP  |  接口 | api/auth/register |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||password	|String	|密码	|Y	|30|
|响应结果|	名称	|类型	|含义	|是否必传|备注|
||code|	int|	异常码	|Y	||
||data	|Map<String, String>|	返回结果	|N|	|
||status	|String|	状态信息|Y|	|


请求示例：

```json
{
    "username": "nlp",
    "password": "666666"
}
```


创建成功时，返回
```json
{
    "status": "success",
    "code": 0
}
```

用户已存在时，返回
```json
{
    "status": "user exist",
    "code": -2
}
```

其他失败时，返回
```json
{
    "status": "fail",
    "code": -1
}
```



#### 1.2 用户登录

|metal |  协议 | HTTP  |  接口 | api/auth/login |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||password	|String	|密码	|Y	|30||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||


请求示例：	
```json
{
    "username": "nlp",
    "password": "666666"
}
```
登录成功时，返回
```json
{
    "status": "success",
    "code": 0
}
```
用户不存在时
```json
{
    "status": "account not exist",
    "code": -1
}
```

其他错误返回:
```json
{
    "status": "fail",
    "code": -2
}
```
判断返回状态应该使用code 判断，0 是正常，负数是报错，正整数代表存在不影响结果的异常，主要用于部分特殊情况，下同


### 二、任务管理系统（包含任务的创建、加入、查询和修改等功能）
#### 2.1 任务创建

|metal |  协议 | HTTP  |  接口 | api/task/create |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskName	|String	|任务名称	|Y	|25||
||clientInfo | 复合类型 |客户端信息||||
||features | 复合类型 |特征信息     ||||
|响应结果|	名称	|类型	|含义	|是否必传||备注|
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|Y|  |返回生成的taskId|
||status	|String|	状态码|Y|	||

ClientInfo详情：
|名称|类型|含义|是否必传|长度|备注|
|-|-|-|-|-|-|
|ip|String|IP地址| Y | 20|
|port|String|端口|Y  |10|
|protocol|String|协议|Y|20|

Features详情：
|名称|类型|含义|是否必传|长度|备注|
|-|-|-|-|-|-|	
|name|String|特征|Y	|20||
|dtype	|String|	类型|	Y|	8|
|describe|string|描述	|N|	40|特征信息	|

请求示例：
```json
{
    "username": "att",
    "taskName": "训练",
    "dataset": "cl0_train.csv",
    "clientInfo": {
        "ip": "127.0.0.1",
        "port": 8081,
        "protocol": "http"
    },
    "features": [
        {
            "name": "年龄",
            "dtype": "int",
            "describe": ""
        },
        {
            "name": "性别",
            "dtype": "bool",
            "describe": ""
        }
    ]
}
```
成功返回示例：
```json
{
    "status":"success",
    "code":0,
    "data": {
        "taskId": 100
    }
}
```

#### 2.2 加入任务

|metal |  协议 | HTTP  |  接口 | /api/task/join |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
||username	| String | |Y|20| |
||taskId	|String	|任务名称	|Y	|10||
||clientInfo|      |||||
||features  |
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||


ClientInfo详情：
|名称|类型|含义|是否必传|长度|备注|
|-|-|-|-|-|-|
|ip|String|IP地址| Y | 20|
|port|String|端口|Y  |10|
|protocol|String|协议|Y|20|

Features详情：
|名称|类型|含义|是否必传|长度|备注|
|-|-|-|-|-|-|	
|name|String|特征|Y	|20||
|dtype	|String|	类型|	Y|	8|
|describe|String|描述	|N|	40|特征信息	|
|alignment|复合类型| 对齐信息|N|

Alignment 详情：
|名称|类型|含义|是否必传|长度|备注|
|-|-|-|-|-|-|	
|participant|参与方|特征|Y	|20||
|feature	|特征名|	类型|	Y|	20|


**请求示例**：
```json
{
    "username": "abc",
    "taskId": 2,
    "taskName": "训练2",
    "dataset": "cl0_train.csv",
    "clientInfo": {
        "ip": "127.0.0.1",
        "port": 3333,
        "protocol": "http"
    },
    "features": [
        {
            "name": "age",
            "dtype": "int",
            "describe": "",
            "alignment": {
                "participant": "A",
                "feature": "age1"
            }
        },
        {
            "name": "gender",
            "dtype": "bool",
            "describe": "",
            "alignment": {
                "participant": "A",
                "feature": "sex"
            }
        }
    ]
}
```
**返回示例**：
```json
{
    "status":"success",
    "code":0,
    "data": {}
}
```


#### 2.3任务列表查询
  分为三个部分, 已创建的任务，已加入的任务，和待加入（既不是自己创建，也未加入）的任务，通过category 参数区分

|metal |  协议 | HTTP  |  接口 | api/task/list |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String |用户名 |Y|20| |
||category	|String	|任务名称	|Y	|25|已创建任务：created <br>已加入任务：joined <br>待加入任务：option|
|响应结果|	名称	|类型	|含义	|是否必传||备注|
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
  "username": "att",
  "category": "created"
}
```
返回示例：
```json
{
    "status":"success",
    "code":0,
    "data":{
        "taskList":[
            {
                "owner":"nlp_test",
                "taskName":"abcd",
                "taskId":5,
                "participants":[]
            },
            {
                "owner":"nlp_test",
                "taskName":"test",
                "taskId":4,
                "participants":[
                    "nlp_test1"
                ]
            },
            {
                "owner":"nlp_test",
                "taskName":"test3",
                "taskId":3,
                "participants":[
                    "nlp_test1"
                ]
            }
        ]
    }
}
```

#### 2.4 任务详情查询

|metal |  协议 | HTTP  |  接口 | api/task/detail |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|Y	|10||
||clientInfo |      |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
    "username":"nlp",
    "taskId": "101"
}
```

返回示例：
```json
{
    "status": "success",
    "code": 0,
    "data": {
        "task": {
            "taskId": 1,
            "taskName": "aaa",
            "taskOwner": "nlp",
            "participants": [
                "jd",
                "jdd",
                "jddd"
            ],
            "clientList": [
                {
                "ip": "10.222.113.150",
                "port": 8096,
                "protocol": "http",
                "token": "dhfwioehow3243",
                "dataset": "cl0_train.csv",
                "hasLabel":false
                },
                {
                    "ip": "10.222.113.150",
                    "port": 8096,
                    "protocol": "http",
                    "token": "dhfwioehow3243",
                    "dataset": "cl0_train.csv",
                    "hasLabel":false
                }
            ],
            "featureList": [
                {
                    "task_id": 100,
                    "username": "nlp",
                    "feature": "x0",
                    "feature_type": "float",
                    "feature_describe": "",
                    "dep_user": null,
                    "dep_feature": null
                },
                {
                    "task_id": 100,
                    "username": "nlp",
                    "feature": "x1",
                    "feature_type": "float",
                    "feature_describe": "",
                    "dep_user": null,
                    "dep_feature": null
                }
            ]
        }
    }
}
```

### 三、ID对齐

#### 3.1 发起ID对齐

|meta|协议 | HTTP |  接口 | /api/match/start |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|Y	|10||
||matchAlgorithm |String|id匹配的算法|Y|40||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||


请求示例：
```json
{
    "username": "jdt",
    "taskId": 92,
    "matchAlgorithm": "MD5"
}
```

启动成功时返回：
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "matchId": "92-SecureBoost-20200604201446"
    }
}
```

#### 3.2 ID对齐状态查询
|meta |  协议 | HTTP |接口| /api/match/progress |请求类型 |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||matchToken |String  |id匹配的算法|Y|40||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求类型	POST	
```json
{
    "username": "admin",
    "matchId":"XXX-XXXXX"
}
```

进度查询成功时返回： （百分比范围为0-100整数）
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "percent":30,
        "describe":"正在对齐"
  }
}
```

#### 3.3 ID对齐列表查询
|meta |  协议 | HTTP |接口| /api/match/list |请求类型 |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||matchToken |String  |id匹配的算法|Y|40||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求类型	POST	
```json
{
    "username": "admin",
    "matchId":"XXX-XXXXX"
}
```

进度查询成功时返回： （百分比范围为0-100整数）
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "percent":30,
        "describe":"正在对齐"
  }
}
```

#### 3.4 删除对齐结果
|meta |  协议 | HTTP |接口| /api/match/delete |请求类型 |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||matchToken |String  |id匹配的算法|Y|40||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求类型	POST	
```json
{
    "username": "admin",
    "matchId":"XXX-XXXXX"
}
```

进度查询成功时返回： （百分比范围为0-100整数）
```json
{
    "code": 0,
    "status": "success"
}
```


### 四、训练过程  包括开始、停止、暂停、恢复训练和训练进度查询、训练详情查询等

#### 4.1 训练参数选项查询

|meta |协议|HTTP|接口|/api/train/option |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||algorithmType	|String	|算法类型	|Y	|20||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

```json
{
    "username": "jdt",
    "algorithmType": "LinearRegression"
}
```

返回结果示例：	
```json
{
    "code": 0,
    "data": {
        "algorithmParams":[
        {
            "field":"secretKey", 
            "name":"秘钥key", 
            "defaultValue":"RAS", 
            "describe" : ["RSA", "NRSA"],  
            "type":"STRING"
        },
        {
            "field":"encryptionAlgorithm", 
            "name":"算法",  
            "defaultValue":"Boots", 
            "describe" : ["1", "2"],  
            "type":"NUMS"
        },
        {
            "field":"eval_metric", 
            "name":"指标",  
            "defaultValue":"rmse", 
            "describe" : ["RMSE", "MAPE"],  
            "type":"MULTI"
        }
      ]
    },
    "status": "success"
}
```

#### 4.2  发起训练

|metal |  协议 | HTTP  |  接口 | /api/train/start |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|Y	|10||
||clientInfo |      |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
    "username": "nlp",
    "taskId": 92,
    "model": "SecureBoost",
    "algorithmParams":[
        {
            "field":"secretKey", 
            "value":"rtretwery454"
        },
        {
            "field":"encryptionAlgorithm", 
            "value":"RSA"
        },
        {
            "field":"trainStepLimit", 
            "value":"1000"
        },
        {
            "field":"eval_metric", 
            "value":["rmse","mape"]
        }
    ],
    "commonParams":[
        {
            "field":"crossValidation", 
            "value":"0.7"
        }
    ]
}
```

启动或者进度查询成功时返回： （百分比范围为0-100整数）
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "modelToken": "92-SecureBoost-20200604201446"
    }
}
```
启动失败时：
```json
{
    "status": "fail",
    "code": -1
}
```


#### 4.3  发起自动化训练

|metal |  协议 | HTTP  |  接口 | /api/train/auto |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|Y	|10||
||clientInfo |      |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
    "username": "nlp",
    "taskId": 92,
    "model": "SecureBoost"
}
```

启动或者进度查询成功时返回： （百分比范围为0-100整数）
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "modelId": "92-SecureBoost-20200604201446"
    }
}
```
启动失败时：
```json
{
    "status": "fail",
    "code": -1
}
```

#### 4.4  单个任务训练进度（包括训练完成和训练失败的任务也可以查询）

|meta |  协议 | HTTP |  接口 | /api/train/progress |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||modelToken |String	|模型id	|Y	|50||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
    "modelId": "92-SecureBoost-20200604201446"
}
```
响应结果示例：
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "percent": 11,
        "runningType": "running",
        "message": "ok"
    }
}
```
code	int	返回码	Y	

data	
返回结果	Y	
runningType 是枚举来类型，共有
running, suspend,
resume, waiting,
complete, stop,
fail 等类型。
message用来表示
详细的运行信息，比如fail时
用来表示详细的报错信息


#### 4.5 单个任务完整训练参数，用于任务恢复，重进入等

|metal |  协议 | HTTP  |  接口 | /api/train/parameter |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
|| modelToken|String	|模型key	|Y	|50||
||type |      ||1 运行中 2 已完成 3重启|||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
    "username":"nlp",
    "modelToken": "92-SecureBoost-20200604201446",
    "type":"3"
}
```
返回示例：
```json
{
    "status": "success",
    "code": 0,
    "data": {
    "taskName": null,
    "taskId": "126",
    "modelToken": "126-SecureBoost-20200703152105",
    "algorithmParams": [
        {
        "field": "num_boost_round",
        "name": "树的个数",
        "value": null,
        "defaultValue": "2",
        "describe": ["1", "100"],
        "type": "NUMS"
        }
    ],
    "trainStartTime": "2020-07-03 00:00:00",
    "trainInfo": [
        "开始", "参数初始化成功", "2020-07-03 15:22:20： 第0轮，mape：1.7976931348623157E308", "2020-07-03 15:28:45： 第1轮，mape：164.42978527009484", "2020-07-03 15:35:47： 第2轮，mape：148.6194573494872", "训练结束", "modelToken is:126-SecureBoost-20200703152105"
    ],
    "percent": 100,
    "model": "SecureBoost",
    "runningStatus": "COMPLETE"
    }
}
```
【TODO 所有的defaultValue 的值赋给value】


#### 4.6 训练任务列表

|metal |  协议 | HTTP  |  接口 | /api/train/list |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|Y	|10||
||clientInfo |      |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求参数	名称	类型	含义	是否必传	长度	备注	示例
username	String	用户名	Y	50	
请求示例：
```json
{
    "username":"nlp",
    "category":"running"
}
```
响应结果示例：
```json
{
    "code": 0,
    "data": {
        "taskList": [
            {
                "taskName": "test",
                "modelToken": "4-SecureBoost-201203161908",
                "taskId": 4
            },
            {
                "taskName": "test3",
    
                "modelToken": "3-SecureBoost-201125144444",
    
                "taskId": 3
            },
            {
                "taskName": "test3",
                "modelToken": "3-SecureBoost-201203105616",
                "taskId": 3
            }
        ]
    },
    "status": "success"
}
```

#### 4.7 训练状态变更，暂停恢复和停止

|meta |  协议 | HTTP  |  接口 | /api/train/change |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||modelToken	|String	|模型key	|Y	|50||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

```json
{
  "username": "jdt",
  "modelId": "92-SecureBoost-20200604201446",
  "type":"stop"
}
```

暂停或者进度查询成功时： （百分比范围为0-100整数）
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "percent":30,
        "describe":"暂停"
    }
}
```
暂停未成功时，返回
```json
{
    "code": 1,
    "status": "success",
    "data": {
        "percent":30,
        "describe":"暂停异常","请稍后再试"
    }
}
```
注：code为0表示暂停成功，前端按钮变为“继续训练”，code为1表示暂停异常，按钮无变化

暂停或者查询失败时：
```json
{
    "status": "fail",
    "code": -1
}
```


#### 4.8 删除训练

|metal |  协议 | HTTP  |  接口 | /api/train/delete |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||modelToken	|String	|模型key	|Y	|50||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
  "username": "nlp",
  "modelId": "92-SecureBoost-20200604201446"
}
```
返回示例：
```json
{
    "code": 0,
    "status": "success",
}
```

### 四、推理（包括模型查询，开始预测、进度查询等）
#### 4.1  查询训练已完成模型
协议	HTTP	接口	


|metal | 协议 | HTTP  |  接口 | /api/inference/query/model|请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|N	|10|不传taskId时返回该用户所有的模型|
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
  "username":"att",
  "taskId": 1
}
```

返回示例：
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "models": [
            "lr_model1",
            "LinearRegression_Model2934729347273947298"
    ]
  }
}
```


#### 4.2 调用接口推理（同步接口）

|metal |  协议 | HTTP  |  接口 | /api/inference/batch |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|Y	|10||
||clientInfo |      |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求类型	POST	

请求示例：
```json
{
    "username":"nlp",
    "uid": [11,12,13],
    "modelToken":"LinearRegression_Model2934729347273947298"
}
```

返回示例：
```json
{
    "code": 0,
    "status": "success",
    "data": {
        {"uid":11, "score":"10.263121046652351"},
        {"uid":12, "score":"10.263121046652351"},
        {"uid":13, "score":"10.263121046652351"}           
    }
    }
}
```

#### 4.3 远端推理

|metal |  协议 | HTTP  |  接口 | /api/inference/remote |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||modelToken	|String	|模型key	|Y	|10||
||path | String  |文件路径|Y|||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例
```json
{
  "username": “nlp”,
  "modelToken": “96-SecureBoost-20200605115258”,
  "path": “/aaa/bbb”
}
```

返回示例
```json
{
    "code": 0,
    "data": {
          "inferenceId": “LinearRegression_Model2934729347273947298”
    },
    "status": "success"
}
```

#### 4.4 远端推理进度查询

|metal |  协议 | HTTP  |  接口 | /api/inference/progress |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||inferenceId	|String	|任务名称	|Y	|10||
||clientInfo |      |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
    "username": "nlp",
    "inferenceId":"LinearRegression_Model2934729347273947298"
}
```

返回进度条
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "percent":0 
    }
}
```
当进度条到达100%时，返回前20条示例
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "percent":100, 
        "content":[
            {"uid":11, "score":"10.263121046652351"},
            {"uid":12, "score":"10.263121046652351"},
            {"uid":13, "score":"10.263121046652351"}
        ]
    }
}
```

#### 4.5 推理日志查询

|metal |  协议 | HTTP  |  接口 | /api/inference/query/log |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||modelToken	|String	|模型token	|Y	|50||
||startTime|String   |||||
||endTime | String  |||||
||pageIndex | int |||||
||pageSize |int |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

String		Y	50	
startTime	String	查询时间开始节点	Y	50	
endTime	String	查询时间结束节点	Y	50	
pageIndex
Integer	当前页	Y	10	从1开始
pageSize
Integer	每页数量	Y	10	


请求示例：
```json
{
    "modelToken": "97-SecureBoost-20200822170951",
    "username": "ak",
    "startTime":"2020-12-03 00:00:00",
    "endTime":"2020-12-03 23:00:00",
    "pageIndex": "1",
    "pageSize": "20"
}
```
返回示例：	
```json
{
    "code": 0,
    "data": {
        "inferenceList": [
         {
           "inferenceId": "3cac2d4f-d4b6-40b0-bfcb-f9d01a6e68ae",
           "caller": "JDD",
           "startTime": "2020-08-26 16:15:37",
           "endTime": "2020-08-26 16:15:38",
           "inferenceResult": "success",
           "requestNum": 94,
           "responseNum": 53
          },
         {
          "inferenceId": "3cac2d4f-d4b6-40b0-bfcb-f9d01a6e68ae",
           "caller": "MOB",
           "startTime": "2020-08-26 16:15:37",
           "endTime": "2020-08-26 16:15:38",
           "inferenceResult": "success",
           "requestNum": 94,
           "responseNum": 53
           }
        ],
        "inferenceCount": 2
    },
    "status": "success"
}
```


### 五、隐私推理（包括模型查询，开始预测、进度查询等）
#### 5.1  查询训练已完成模型

|meta | 协议 | HTTP  |  接口 | /api/inference/query/model|请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|N	|10|不传taskId时返回该用户所有的模型|
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
  "username":"att",
  "taskId": 1
}
```

返回示例：
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "models": [
            "lr_model1",
            "LinearRegression_Model2934729347273947298"
    ]
  }
}
```


#### 4.2 调用接口推理（同步接口）

|metal |  协议 | HTTP  |  接口 | /api/inference/batch |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|Y	|10||
||clientInfo |      |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求类型	POST	

请求示例：
```json
{
    "username":"nlp",
    "uid": [11,12,13],
    "modelToken":"LinearRegression_Model2934729347273947298"
}
```

返回示例：
```json
{
    "code": 0,
    "status": "success",
    "data": {
        {"uid":11, "score":"10.263121046652351"},
        {"uid":12, "score":"10.263121046652351"},
        {"uid":13, "score":"10.263121046652351"}           
    }
    }
}
```

#### 4.3 远端推理

|metal |  协议 | HTTP  |  接口 | /api/inference/remote |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||modelToken	|String	|模型key	|Y	|10||
||path | String  |文件路径|Y|||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例
```json
{
  "username": “nlp”,
  "modelToken": “96-SecureBoost-20200605115258”,
  "path": “/aaa/bbb”
}
```

返回示例
```json
{
    "code": 0,
    "data": {
          "inferenceId": “LinearRegression_Model2934729347273947298”
    },
    "status": "success"
}
```

#### 4.4 远端推理进度查询

|metal |  协议 | HTTP  |  接口 | /api/inference/progress |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||inferenceId	|String	|任务名称	|Y	|10||
||clientInfo |      |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
    "username": "nlp",
    "inferenceId":"LinearRegression_Model2934729347273947298"
}
```

返回进度条
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "percent":0 
    }
}
```
当进度条到达100%时，返回前20条示例
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "percent":100, 
        "content":[
            {"uid":11, "score":"10.263121046652351"},
            {"uid":12, "score":"10.263121046652351"},
            {"uid":13, "score":"10.263121046652351"}
        ]
    }
}
```

#### 4.5 推理日志查询

|metal |  协议 | HTTP  |  接口 | /api/inference/query/log |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||modelToken	|String	|模型token	|Y	|50||
||startTime|String   |||||
||endTime | String  |||||
||pageIndex | int |||||
||pageSize |int |||||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

String		Y	50	
startTime	String	查询时间开始节点	Y	50	
endTime	String	查询时间结束节点	Y	50	
pageIndex
Integer	当前页	Y	10	从1开始
pageSize
Integer	每页数量	Y	10	


请求示例：
```json
{
    "modelToken": "97-SecureBoost-20200822170951",
    "username": "ak",
    "startTime":"2020-12-03 00:00:00",
    "endTime":"2020-12-03 23:00:00",
    "pageIndex": "1",
    "pageSize": "20"
}
```
返回示例：	
```json
{
    "code": 0,
    "data": {
        "inferenceList": [
         {
           "inferenceId": "3cac2d4f-d4b6-40b0-bfcb-f9d01a6e68ae",
           "caller": "JDD",
           "startTime": "2020-08-26 16:15:37",
           "endTime": "2020-08-26 16:15:38",
           "inferenceResult": "success",
           "requestNum": 94,
           "responseNum": 53
          },
         {
          "inferenceId": "3cac2d4f-d4b6-40b0-bfcb-f9d01a6e68ae",
           "caller": "MOB",
           "startTime": "2020-08-26 16:15:37",
           "endTime": "2020-08-26 16:15:38",
           "inferenceResult": "success",
           "requestNum": 94,
           "responseNum": 53
           }
        ],
        "inferenceCount": 2
    },
    "status": "success"
}
```

### 六、功能接口（包括各类参数查询、系统统计、监控等）
#### 5.1删除已训练的模型

|metal |  协议 | HTTP  |  接口 | /api/system/model/delete |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||modelToken	|String	|模型token	|Y	|50|
|响应结果 |	名称	|类型	|含义	|是否必传|备注|
||code|	int|	异常码	|Y	||
||data	|Map<String, String>|	返回结果	|N|	|
||status	|String|	状态码|Y|	|

请求示例：
```json
{
    "modelToken": "97-SecureBoost-20200822170951",
    "username": "ak"
}
```
返回示例：
```json
{
    "code": 0,
    "status": "success"
}
```


#### 5.2  数据集和特征查询

|metal |  协议 | HTTP |  接口 | /api/system/query/dataset |请求类型  |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
|| clientUrl|String	|客户端地址	|Y	|80|
|响应结果|	名称	|类型	|含义	|是否必传|备注|
||code|	int|	异常码	|Y	||
||data	|Map<String, String>|	返回结果	|N|	|
||status	|String|	状态码|Y|	|

请求示例：
```json
{
    "username":"att",
    "clientUrl":"http://127.0.0.1:8091"
}
```
返回结果示例：
```json
{
    "code": 0,
    "data": {
        "list": [
            {
                "dataset": "reg0_train.csv",
                "features": [
                    {
                        "name": "uid",
                        "dtype": "float"
                    },
                    {
                        "name": "HouseAge",
                        "dtype": "float"
                    },
                    {
                        "name": "y",
                        "dtype": "float"
                    }
                ]
            },
            {
                "dataset": "class0_train.csv",
                "features": [
                    {
                        "name": "uid",
                        "dtype": "float"
                    },
                    {
                        "name": "job",
                        "dtype": "float"
                    },
                    {
                        "name": "poutcome",
                        "dtype": "float"
                    },
                    {
                        "name": "y",
                        "dtype": "float"
                    }
                ]
            }
        ]
    },
    "status": "success"
}
```

#### 3.4 训练通用参数查询
|metal|协议|HTTP|接口| /api/prepare/parameter/common |请求类型 |POST|
|-----|-----|-----|-----|-----|-----|-|
|请求参数|名称 |类型	|含义	|是否必传	|长度	|备注	|
|| username	| String | |Y|20| |
||taskId	|String	|任务名称	|Y	|10||
|响应结果|	名称	|类型	|含义	|是否必传|备注||
||code|	int|	异常码	|Y	|||
||data	|dict|	返回结果	|N|	||
||status	|String|	状态码|Y|	||

请求示例：
```json
{
    "username":"jdt"
}
```
返回示例：
```json
{
    "code": 0,
    "status": "success",
    "data": {
        "commonParams":[
            {
                "field":"crossValidation", 
                "name":"交叉验证参数", 
                "defaultValue":"0.7", 
                "describe" : ["0", "1"],  
                "type":"NUMS"
            }
        ]
  }
}
```


### 附录 错误码
#### 1
