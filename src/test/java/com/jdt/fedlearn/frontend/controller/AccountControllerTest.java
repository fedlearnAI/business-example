package com.jdt.fedlearn.frontend.controller;

import com.jdt.fedlearn.frontend.util.JsonUtil;
import com.jdt.fedlearn.frontend.FederatedApplication;
import com.jdt.fedlearn.frontend.JdchainFederatedApplication;
import com.jdt.fedlearn.frontend.constant.Constant;
import com.jdt.fedlearn.frontend.entity.table.AccountDO;
import com.jdt.fedlearn.frontend.service.impl.account.AccountServiceDbImpl;
import com.jdt.fedlearn.frontend.service.impl.account.AccountServiceJdchainImpl;
import mockit.Mock;
import mockit.MockUp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest(classes = {JdchainFederatedApplication.class, FederatedApplication.class})
public class AccountControllerTest extends AbstractTestNGSpringContextTests{
    private MockMvc mockMvc;
    private static final String p = "123456";

    @Autowired
    private WebApplicationContext context;

    @BeforeClass
    public void setup() {
        //单个类,项目拦截器无效
        //mockMvc = MockMvcBuilders.standaloneSetup(new AccountController()).build();
        // 项目拦截器有效
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockAccountServiceJdchain();
        mockAccountServiceDb();
    }

    @Test
    public void register() {

    }

    @Test
    public void login() throws Exception {
        String message = "{\"username\":\"nlp\",\"password\":\""+ p +"\"}";
        //调用接口，传入用户参数
        RequestBuilder request = MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(message);
        MvcResult mvcResult = mockMvc.perform(request).andReturn() ;
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println(result);
        ModelMap modelMap = JsonUtil.json2Object(result,ModelMap.class);
        Assert.assertEquals(modelMap.get("code"),0);
    }

    @Test
    public void queryUserList() {

    }

    @Test
    public void update() {
    }

    private void mockAccountServiceDb() {
        new MockUp<AccountServiceDbImpl>() {
            @Mock
            public AccountDO queryAccount(String userName) {
                AccountDO accountDO = new AccountDO();
                accountDO.setUsername(userName);
                accountDO.setPassword(p);
                accountDO.setStatus(Constant.STATUS_ENABLE);
                return accountDO;
            }
        };
    }

    private void mockAccountServiceJdchain() {
        new MockUp<AccountServiceJdchainImpl>() {
            @Mock
            public AccountDO queryAccount(String userName) {
                AccountDO accountDO = new AccountDO();
                accountDO.setUsername(userName);
                accountDO.setPassword(p);
                accountDO.setStatus(Constant.STATUS_ENABLE);
                return accountDO;
            }
        };
    }
}