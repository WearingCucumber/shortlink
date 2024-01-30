package com.study.shortLink.project.mq.producer;

import com.study.shortLink.project.mq.domain.MessageWrapper;
import com.study.shortLink.project.mq.event.ShortLinkStatsEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.study.shortLink.project.common.constant.ShortLinkStatsMQConstant.*;

/**
 * 短链接跳转监控信息入库消息生产者
 */
@Slf4j
@Component
public class ShortLinkStatsDelaySaveSendProduce extends AbstractCommonSendProduceTemplate<ShortLinkStatsEvent>{
    public ShortLinkStatsDelaySaveSendProduce(RocketMQTemplate rocketMQTemplate) {
        super(rocketMQTemplate);
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(ShortLinkStatsEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("短链接监控信息入库")
                .topic(LINK_STATS_TOPIC_KEY)
                .tag(LINK_STATS_DELAY_SAVE_TAG_KEY)
                //1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
                .delayLevel(3)
                .sentTimeout(2000L)
                .build();

    }

    @Override
    protected Message<?> buildMessage(ShortLinkStatsEvent messageSendEvent, BaseSendExtendDTO requestParam) {
        String keys = UUID.randomUUID().toString() ;
        return MessageBuilder
                .withPayload(new MessageWrapper(keys, messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();

    }
}
