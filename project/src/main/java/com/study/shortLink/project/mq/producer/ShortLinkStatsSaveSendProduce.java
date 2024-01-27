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

import static com.study.shortLink.project.common.constant.ShortLinkStatsMQConstant.LINK_STATS_SAVE_TAG_KEY;
import static com.study.shortLink.project.common.constant.ShortLinkStatsMQConstant.LINK_STATS_TOPIC_KEY;

/**
 * 短链接跳转监控信息入库消息生产者
 */
@Slf4j
@Component
public class ShortLinkStatsSaveSendProduce  extends AbstractCommonSendProduceTemplate<ShortLinkStatsEvent>{
    public ShortLinkStatsSaveSendProduce(RocketMQTemplate rocketMQTemplate) {
        super(rocketMQTemplate);
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(ShortLinkStatsEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("短链接监控信息入库")
                .topic(LINK_STATS_TOPIC_KEY)
                .tag(LINK_STATS_SAVE_TAG_KEY)
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
