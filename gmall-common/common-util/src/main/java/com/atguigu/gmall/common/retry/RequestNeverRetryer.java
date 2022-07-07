package com.atguigu.gmall.common.retry;

import feign.RetryableException;
import feign.Retryer;

/**
 * @author fanyudong
 * @date 2022/7/7 10:55
 */
public class RequestNeverRetryer implements Retryer {


    @Override
    public void continueOrPropagate(RetryableException e) {
        //只要重试器抛出了异常就会打断重试逻辑
        throw e;
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
