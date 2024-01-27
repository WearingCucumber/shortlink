package com.study.shortLink.project;

import com.study.shortLink.project.mq.event.ShortLinkStatsEvent;
import com.study.shortLink.project.mq.producer.ShortLinkStatsSaveSendProduce;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class MQTest {
    @Autowired
    private  ShortLinkStatsSaveSendProduce shortLinkStatsSaveSendProduce;

    @Test
    public void mqSendTest(){
        SendResult sendResult = shortLinkStatsSaveSendProduce.sendMessage(
                ShortLinkStatsEvent
                        .builder()
                        .fullShortUrl("test")
                        .gid("test")
                        .request(null)
                        .build());

        System.out.println(sendResult.toString());
    }



}
