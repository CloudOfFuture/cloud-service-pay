package com.kunlun.api.client;

import com.kunlun.api.hystrix.DeliveryClientHystrix;
import com.kunlun.entity.Delivery;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author by hws
 * @created on 2017/12/26.
 */
@FeignClient(value = "cloud-service-user-center")
public interface DeliveryClient {


    /**
     * 根据id查找收货详情
     * @param deliveryId
     * @return
     */
    @GetMapping("/delivery/findById")
    DataRet<Delivery> findById(@RequestParam(value = "deliveryId") Long deliveryId);
}
