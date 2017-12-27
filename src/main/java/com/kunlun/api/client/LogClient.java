package com.kunlun.api.client;

import com.kunlun.api.hystrix.LogClientHystrix;
import com.kunlun.entity.OrderLog;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author by hws
 * @created on 2017/12/26.
 */
@FeignClient(value = "cloud-service-log",fallback = LogClientHystrix.class)
public interface LogClient {

    /**
     * 创建订单日志
     * @param orderLog
     * @return
     */
    @PostMapping("/log/add/orderLog")
    DataRet<String> addOrderLog(@RequestBody OrderLog orderLog);
}
