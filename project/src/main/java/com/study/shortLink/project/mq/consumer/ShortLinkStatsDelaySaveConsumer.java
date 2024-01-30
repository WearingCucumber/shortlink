package com.study.shortLink.project.mq.consumer;

import com.study.shortLink.project.common.convention.exception.ServiceException;
import com.study.shortLink.project.mq.Idempotent.MessageQueueIdempotentHandler;
import com.study.shortLink.project.mq.domain.MessageWrapper;
import com.study.shortLink.project.mq.event.ShortLinkStatsEvent;
import com.study.shortLink.project.mq.producer.ShortLinkStatsSaveSendProduce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShortLinkStatsDelaySaveConsumer implements RocketMQListener<MessageWrapper<ShortLinkStatsEvent>> {
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;
    private final ShortLinkStatsSaveSendProduce shortLinkStatsSaveSendProduce;
    @Override
    public void onMessage(MessageWrapper<ShortLinkStatsEvent> message){
        Executors.newSingleThreadExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("link_stats-delay-save_consumer");
                    thread.setDaemon(true);
                    return thread;
                })
                .execute(()->{
                    String messageId = message.getUuid();
                    //判断这条消息是否被消费过了 如果返回false 则说明 这条消息被消费过 但是不确定是否消费完成  所以需要后面的 判断;
                    if (!messageQueueIdempotentHandler.isMessageProcessed(messageId)){
                        //判断当前这个消息流程是否执行完成
                        if (messageQueueIdempotentHandler.isAccomplish(messageId))
                            return;
                        throw new ServiceException("消息消费异常，需要重试");
                    }
                    try {
                        shortLinkStatsSaveSendProduce.sendMessage(message.getMessage());
                    }catch (Exception e){
                        messageQueueIdempotentHandler.delMessageProcessed(messageId);
                        throw new ServiceException("消息消费异常，需要重试");
                    }
                    messageQueueIdempotentHandler.setAccomplish(messageId);
                });
    }



}
