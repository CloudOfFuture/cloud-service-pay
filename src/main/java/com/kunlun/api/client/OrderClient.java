package com.kunlun.api.client;

import com.kunlun.api.hystrix.OrderClientHystrix;
import com.kunlun.entity.Order;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @PostMapping("/wx/order/addOrder")
    DataRet<String> addOrder(Order order);

    /**
     * 修改预付款订单号
     * @param id
     * @param prepayId
     * @return
     */
    @PostMapping("/wx/order/updatePrepayId")
    DataRet<String> updateOrderPrepayId(Long id,String prepayId);

    /**
     * 根据id查找订单详情
     * @param id
     * @return
     */
    @GetMapping("/wx/order/findById")
    DataRet<Order> findOrderById(@RequestParam(value = "id") Long id);
}

