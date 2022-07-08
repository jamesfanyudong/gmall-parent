package com.atguigu.gmall.common.config.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author fanyudong
 */

@Data
@Component
@ConfigurationProperties(prefix = "app.threadpool")
public class ThreadPoolProperties {

    private Integer corePoolSize = 4;
    private Integer maximumPoolSize = 8;
    private long keepAliveTime = 60;
    private Integer workQueueSize = 200;

}
