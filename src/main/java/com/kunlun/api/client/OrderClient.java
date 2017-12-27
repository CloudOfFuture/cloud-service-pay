package com.kunlun.api.client;

import com.kunlun.api.hystrix.OrderClientHystrix;
import com.kunlun.entity.Order;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author by hws
 * @created on 2017/12/26.
 */
@FeignClient(value = "cloud-service-order",fallback = OrderClientHystrix.class)
public interface OrderClient {
    /**
     * 新增订单
     * @param order
     * @return
     */
    @PostMapping("/addOrder")
    DataRet<String> addOrder(Order order);
}
