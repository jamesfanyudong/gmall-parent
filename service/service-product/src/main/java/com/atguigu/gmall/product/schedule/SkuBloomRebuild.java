package com.atguigu.gmall.product.schedule;

import com.atguigu.gmall.product.service.BloomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author fanyudong
 */
@Service
public class SkuBloomRebuild {

    @Autowired
    BloomService bloomService;
    @Scheduled(cron = "0 0 3 */7 * ?")
    public void rebuild(){
        bloomService.rebuildSkuBloom();
    }
}
