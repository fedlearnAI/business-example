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

package com.jdt.fedlearn.frontend.mapper;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.base.DefaultCryptoEncoding;
import com.jd.blockchain.crypto.base.HashDigestBytes;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.EventContext;
import com.jd.blockchain.sdk.UserEventListener;
import com.jd.blockchain.sdk.UserEventPoint;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.transaction.ContractEventSendOperationBuilder;
import com.jdt.fedlearn.frontend.constant.JdChainConstant;
import com.jdt.fedlearn.frontend.exception.RandomServerException;
import com.jdt.fedlearn.frontend.jdchain.config.JdChainCondition;
import com.jdt.fedlearn.frontend.util.IdGenerateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import utils.codec.Base58Utils;
import utils.io.BytesUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/*
 * JDChain??????????????????????????????
 * */
@Conditional(JdChainCondition.class)
@Component
public class JdChainBaseMapper implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JdChainBaseMapper.class);
    private BlockchainService blockchainService;
    private BlockchainKeypair adminKey;
    private HashDigest ledgerHash;

    @Value("${server.port}")
    private int port;
    @Value("${jdchain.user_pubkey}")
    private String userPubkey;
    @Value("${jdchain.user_privkey}")
    private String userPrivkey;
    @Value("${jdchain.user_privpwd}")
    private String userPrivpwd;
    @Value("${jdchain.gateway_ip}")
    private String gatewayIp;
    @Value("${jdchain.gateway_port}")
    private String gatewayPort;
    @Value("${jdchain.gateway_secure}")
    private String gatewaySecure;
    private String contractAddress;

    @Value("${jdchain.contract_address}")
    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    private String dataAccountAddress;

    @Value("${jdchain.data_account_address}")
    public void setDataAccountAddress(String dataAccountAddress) {
        this.dataAccountAddress = dataAccountAddress;
    }

    private String eventAccountAddress;

    @Value("${jdchain.event_account_address}")
    public void setEventAccountAddress(String eventAccountAddress) {
        this.eventAccountAddress = eventAccountAddress;
    }

    @Value("${jdchain.ledger_address}")
    private String ledgerAddress;

    @Value("${jdchain.task_table_address}")
    private String taskTableAddress;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            initGateway(); //?????????????????????]
        } catch (Exception e) {
            logger.error("??????????????????????????????{}",e.getMessage());
        }
    }

    //?????????????????????
    public void initGateway() {
        PubKey pubKey = KeyGenUtils.decodePubKey(userPubkey);
        PrivKey privKey = KeyGenUtils.decodePrivKey(userPrivkey, userPrivpwd);
        adminKey = new BlockchainKeypair(pubKey, privKey);
        GatewayServiceFactory connect = GatewayServiceFactory.connect(gatewayIp,
                Integer.parseInt(gatewayPort), Boolean.parseBoolean(gatewaySecure), adminKey);
        blockchainService = connect.getBlockchainService();
        connect.close();
        if (StringUtils.isBlank(ledgerAddress)) {
            ledgerHash = blockchainService.getLedgerHashs()[0];
        } else {
            ledgerHash = new HashDigestBytes(DefaultCryptoEncoding.decodeAlgorithm(Base58Utils.decode(ledgerAddress)), Base58Utils.decode(ledgerAddress));
        }
        logger.info("ledger hash:{}",ledgerHash);
    }

    /*
     * ???????????????????????????
     * */
    public String invokeRandomtraining(String userName, String taskId, String model) {
        Map<String, Object> metaMap = new HashMap<>();
        metaMap.put("userName", userName);
        metaMap.put("taskId", taskId);
        metaMap.put("model", model);
        metaMap.put("modelArgs", IdGenerateUtil.getUUID());
        String fname = JdChainConstant.INVOKE_RANDOM_TRAINING;
        String putKey = fname + JdChainConstant.SEPARATOR + taskId + JdChainConstant.SEPARATOR + JdChainConstant.FRONT;
        String eventName = fname;
        String result = "";
        try {
            TransactionResponse response = putByChaincode(eventName, fname, putKey, metaMap);
            logger.info("random server success ? {}", response.isSuccess());
            if (response.isSuccess()) {
                if (response.getOperationResults().length == 1) {
                    BytesValue content = response.getOperationResults()[0].getResult();
                    switch (content.getType()) {
                        case TEXT:
                            result = content.getBytes().toUTF8String();
                            break;
                        case INT64:
                            result = String.valueOf(BytesUtils.toLong(content.getBytes().toBytes()));
                            break;
                        case BOOLEAN:
                            result = String.valueOf(BytesUtils.toBoolean(content.getBytes().toBytes()[0]));
                            break;
                        case JSON:
                            result = content.getBytes().toUTF8String();
                            break;
                        default: // byte[], Bytes
                            result = content.getBytes().toBase58();
                            break;
                    }
                    return result;
                }
            } else {
                throw new RandomServerException("random server error, please check it");
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            logger.error("?????????????????????{}",e.getMessage());
        }
        return null;
    }

    /**
     * ?????????????????????????????????
     */
    public TransactionResponse invokeStarttraining(String tokenId, String phase, String phaseArgs) {
        Map<String, Object> metaMap = new HashMap<>();
        metaMap.put("taskId", tokenId);
        metaMap.put("phase", phase);
        metaMap.put("phaseArgs", phaseArgs);
        String fname = JdChainConstant.INVOKE_START_TRAINING;
        String putKey = fname + JdChainConstant.SEPARATOR + JdChainConstant.SERVER + JdChainConstant.SEPARATOR + tokenId + JdChainConstant.SEPARATOR + phase;
        String eventName = fname;
        try {
            TransactionResponse response = putByChaincode(eventName, fname, putKey, metaMap);
            return response;
        } catch (UnsupportedEncodingException e) {
            logger.error("?????????????????????{}",e.getMessage());
        }
        return null;
    }

    /*
     * ?????????????????????
     * */
    public TransactionResponse invokeSummarytraining(String taskId, String phase, String result) {
        ArrayList<String> putValue = new ArrayList<>();
        putValue.add(taskId);
        putValue.add(phase);
        putValue.add(result);
        String fname = JdChainConstant.INVOKE_SUMMARY_TRAINING;
        String putKey = fname + JdChainConstant.SEPARATOR + taskId + JdChainConstant.SEPARATOR + phase + JdChainConstant.SERVER;
        String eventName = fname;
        TransactionResponse response = putByChaincode(eventName, fname, putKey, putValue);
        return response;
    }

    /*
     * ????????????????????????(????????????)
     * @Param : annotationName: ????????????  | putKey:??????key??? | putVal:??????value???
     * */
    public TransactionResponse putByChaincodeNoEvents(String annotationName, String putKey, ArrayList<String> putVal) throws InvocationTargetException, IllegalAccessException {
        // ????????????
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        ContractEventSendOperationBuilder builder = txTemp.contract();
        // ?????????????????????????????????????????????????????????????????????
        // ????????????????????????????????????????????????????????????
        // ??????????????? registerUser ????????????????????????????????????????????????????????????????????????
        builder.send(contractAddress, annotationName,
                new BytesDataList(new TypedValue[]{
                        TypedValue.fromText(dataAccountAddress),
                        TypedValue.fromText(ledgerHash.toBase58()),
                        TypedValue.fromText(putKey),
                        TypedValue.fromText(putVal.toString())
                })
        );
        TransactionResponse txResp = commit(txTemp);
        try {
            txTemp.close();
        } catch (IOException e) {
            logger.error("TransactionResponse close error:{}",e.getMessage());
        }
        logger.info("txResp.isSuccess=" + txResp.isSuccess() + ",blockHeight=" + txResp.getBlockHeight());
        return txResp;
    }

    /*
     * ????????????????????????(?????????)
     * params : eventName:???????????? | annotationName:?????????????????????????????????(???????????????????????????????????????) |  putKey: ??????key??? | putVal: ??????values???
     * jdchain??????
     * 1,????????????????????????????????????key:value?????????????????????value?????????java 8?????????????????????
     * */
    public TransactionResponse putByChaincode(String eventName, String annotationName, String putKey, ArrayList<String> putVal) {
        // ????????????
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        ContractEventSendOperationBuilder builder = txTemp.contract();
        // ?????????????????????????????????????????????????????????????????????
        // ????????????????????????????????????????????????????????????
        // ??????????????? registerUser ????????????????????????????????????????????????????????????????????????
        builder.send(contractAddress, annotationName,
                new BytesDataList(new TypedValue[]{
                        TypedValue.fromText(dataAccountAddress),
                        TypedValue.fromText(ledgerHash.toBase58()),
                        TypedValue.fromText(eventAccountAddress),
                        TypedValue.fromText(eventName),
                        TypedValue.fromText(putKey),
                        TypedValue.fromText(putVal.toString())
                })
        );
        TransactionResponse txResp = commit(txTemp);
        try {
            txTemp.close();
        } catch (IOException e) {
            logger.error("TransactionResponse close error:{}",e.getMessage());
        }
        logger.info("txResp.isSuccess=" + txResp.isSuccess() + ",blockHeight=" + txResp.getBlockHeight());
        return txResp;
    }

    public TransactionResponse putByChaincode(String eventName, String annotationName, String putKey, Map<String, Object> putVal) throws UnsupportedEncodingException {
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        ContractEventSendOperationBuilder builder = txTemp.contract();
        byte[] putValue = getMapToString(putVal).getBytes("UTF-8"); //map to string
        builder.send(contractAddress, annotationName,
                new BytesDataList(new TypedValue[]{
                        TypedValue.fromText(dataAccountAddress),
                        TypedValue.fromText(ledgerHash.toBase58()),
                        TypedValue.fromText(eventAccountAddress),
                        TypedValue.fromText(eventName),
                        TypedValue.fromText(putKey),
                        TypedValue.fromBytes(putValue)
                })
        );
        TransactionResponse txResp = commit(txTemp);
        try {
            txTemp.close();
        } catch (IOException e) {
            logger.error("TransactionResponse close error:{}",e.getMessage());
        }
        logger.info("txResp.isSuccess=" + txResp.isSuccess() + ",blockHeight=" + txResp.getBlockHeight());
        return txResp;
    }

    /*
     * jdchain????????????(???????????????????????????)
     * */
    public void eventListening(String eventName) {
        //????????????
        blockchainService.monitorUserEvent(ledgerHash, eventAccountAddress, eventName, 0, new UserEventListener<UserEventPoint>() {
            @Override
            public void onEvent(Event eventMessage, EventContext<UserEventPoint> eventContext) {
                System.out.println("?????????name:" + eventMessage.getName() + ", sequence:" + eventMessage.getSequence() +
                        ",content:" + eventMessage.getContent().getBytes().toUTF8String() + ",blockheigh:" + eventMessage.getBlockHeight() +
                        ",eventAccount:" + eventMessage.getEventAccount());
            }
        });
    }

    /*
     * ??????????????????
     * @Params : queryKey :?????????key???
     * */
    public TypedKVEntry queryByChaincode(String queryKey) {
        String actionName = queryKey.substring(0, queryKey.indexOf("-"));
        String dataAccount = getOwnerDataAccount(dataAccountAddress, actionName);
        if (dataAccount.isEmpty()) {
            throw new IllegalStateException(String.format("actionName:%s ?????????????????????!", actionName));
        }
        TypedKVEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHash, dataAccount, queryKey);
        if (kvDataEntries == null || kvDataEntries.length == 0) {
            throw new IllegalStateException(String.format("Ledger %s Service inner Error !!!", ledgerHash.toBase58()));
        }
        TypedKVEntry kvDataEntry = kvDataEntries[0];
        return kvDataEntry;
    }

    /*
     * ????????????????????????????????????????????????(???????????????????????????)
     * ??????invoke_register <-> LdeNrYZWydsWexHeiK9U2QWrRK9Fv3TXGTEgr (?????????????????? <-> ??????????????????)
     * */
    private String getOwnerDataAccount(String dataAccountAddr, String actionName) {
        TypedKVEntry[] dataaccount = blockchainService.getDataEntries(ledgerHash, dataAccountAddr, actionName);
        if (dataaccount == null || dataaccount.length == 0) {
            throw new IllegalStateException("getOwnerDataAccount dataaccount unregister");
        }
        return dataaccount[0].getValue().toString();
    }

    private TransactionResponse commit(TransactionTemplate txTpl) {
        PreparedTransaction ptx = txTpl.prepare();
        ptx.sign(adminKey);
        TransactionResponse commit = ptx.commit();
        try {
            ptx.close();
        } catch (IOException e) {
            logger.info("PreparedTransaction close error:{}",e.getMessage());
        }
        return commit;
    }

    public String getMapToString(Map<String, Object> map) {
        Set<String> keySet = map.keySet();
        //???set?????????????????????
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        //???????????????(??????)
        Arrays.sort(keyArray);
        //??????String???????????????????????????????????????StringBuilder
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyArray.length; i++) {
            sb.append(keyArray[i]).append(":").append(String.valueOf(map.get(keyArray[i])).trim());
            if (i != keyArray.length - 1) {
                sb.append("#9#");
            }
        }
        String result = sb.toString().replace("#9##9#", "#9#");
        return result;
    }

    /*
     * ?????????????????????????????????
     * @Params:
     *  dataAccountAddr :??????????????????
     *  key : ??????????????????key???
     *  value : ??????????????????value???
     * */
    public TransactionResponse saveKV(String dataAccountAddr, String key, String value) {
        String[] checkParams = new String[]{dataAccountAddr, key, value};
        if (!verifyParamsIsNull(checkParams)) {
            return null;
        }
        long currentVersion = -1;
        TypedKVEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHash, dataAccountAddr, key);
        if (kvDataEntries == null || kvDataEntries.length == 0) {
            return null;
        }
        TypedKVEntry kvDataEntry = kvDataEntries[0];
        if (kvDataEntry.getVersion() != -1) {
            currentVersion = kvDataEntry.getVersion();
        }
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        txTpl.dataAccount(dataAccountAddr).setText(key, value, currentVersion);
        TransactionResponse txResp = commit(txTpl);
        try {
            txTpl.close();
        } catch (IOException e) {
            logger.error("TransactionResponse close error:{}",e.getMessage());
        }
        logger.info("txResp.isSuccess=" + txResp.isSuccess() + ",blockHeight=" + txResp.getBlockHeight());
        return txResp;
    }

    /*
     * ??????key?????????????????????
     * @Params:
     *  dataAccountAddr :??????????????????
     *  key : ??????????????????key???
     * */
    public String queryLatestValueByKey(String dataAccountAddr, String key) {
        String[] checkParams = new String[]{dataAccountAddr, key};
        if (!verifyParamsIsNull(checkParams)) {
            return "";
        }
        TypedKVEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHash, dataAccountAddr, key);
        if (kvDataEntries == null || kvDataEntries.length == 0) {
            return "";
        }
        TypedKVEntry kvDataEntry = kvDataEntries[0];
        if (kvDataEntry.getVersion() == -1) {
            return "";
        }
        logger.info("query result:" + kvDataEntry.getValue().toString());
        return kvDataEntry.getValue().toString();
    }

    /*
     * ??????key???????????????value???
     * @Params:
     *  dataAccountAddr :??????????????????
     *  key : ??????????????????key???
     * */
    public TypedKVEntry[] queryHistoryValueByKey(String dataAccountAddr, String key) {
        String[] checkParams = new String[]{dataAccountAddr, key};
        if (!verifyParamsIsNull(checkParams)) {
            return null;
        }
        TypedKVEntry[] kvDataEntrie = blockchainService.getDataEntries(ledgerHash, dataAccountAddr, key);
        if (kvDataEntrie == null || kvDataEntrie.length == 0) {
            return null;
        }
        long latestVersion = kvDataEntrie[0].getVersion();
        long[] versionArr = new long[(int) latestVersion + 1];
        for (long i = 0; i < latestVersion + 1; i++) {
            versionArr[(int) i] = i;
        }
        KVDataVO kvDataVO = new KVDataVO();
        kvDataVO.setKey(key);
        kvDataVO.setVersion(versionArr);

        KVInfoVO kvInfoVO = new KVInfoVO();
        kvInfoVO.setData(new KVDataVO[]{kvDataVO});

        TypedKVEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHash, dataAccountAddr, kvInfoVO);
        if (kvDataEntries == null || kvDataEntries.length == 0) {
            return null;
        }
        return kvDataEntries;
    }

    /*
     * ???????????????????????????????????????key:value
     * @Params:
     *   dataAccountAddr :??????????????????
     * */
    public TypedKVEntry[] queryAllKVByDataAccountAddr(String dataAccountAddr) {
        if (StringUtils.isBlank(dataAccountAddr)) {
            return null;
        }
        TypedKVEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHash, dataAccountAddr, 0, -1);
        logger.info("query dataaccount kv total:" + kvDataEntries.length);
        return kvDataEntries;
    }

    //????????????????????????
    public boolean verifyParamsIsNull(String... params) {
        if (params.length == 0) {
            return false;
        }
        for (String param : params) {
            if (param == null || param.length() == 0) {
                return false;
            }
        }
        return true;
    }

}
