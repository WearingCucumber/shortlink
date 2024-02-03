package com.study.shortLink.project;

import com.alibaba.nacos.api.exception.NacosException;
import com.study.shortLink.project.common.convention.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;

@SpringBootTest
public class NacosTest {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Test
    public void nacos() throws NacosException {
        throw new ServiceException(discoveryClient.getServices().toString());

    }
}
