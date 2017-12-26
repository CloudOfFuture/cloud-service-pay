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
@FeignClient(value = "cloud-service-user-center",fallback = DeliveryClientHystrix.class)
public interface DeliveryClient {

    /**
     * 校验收获地址
     * @param id
     * @return
     */
    @GetMapping("/checkDelivery")
    DataRet checkDelivery(@RequestParam Long id);

    /**
     * 根据id查找收货详情
     * @param id
     * @return
     */
    @GetMapping("/findDetailById")
    DataRet<Delivery> findById(@RequestParam(value = "id") Long id);
}
