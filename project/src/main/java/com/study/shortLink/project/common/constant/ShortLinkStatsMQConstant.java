package com.study.shortLink.project.common.constant;

/**
 * RocketMQ 短链接监控业务常量类
 */
public final class ShortLinkStatsMQConstant {

    /**
     * 监控数据入库业务 Topic Key
     */
    public static final String LINK_STATS_TOPIC_KEY = "link_stats_topic";

    /**
     * 监控数据入库业务 Tag Key
     */
    public static final String LINK_STATS_SAVE_TAG_KEY = "link_stats-save_tag";


    /**
     * 监控数据入库业务 Tag Key
     */
    public static final String LINK_STATS_DELAY_SAVE_TAG_KEY = "link_stats-delay-save_tag";




    /**
     * 监控数据入库消息消费者组 Key
     */
    public static final String LINK_STATS_SAVE_CG_KEY = "link_stats-save_cg";


}
