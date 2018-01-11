package com.kunlun.api.client;

import com.kunlun.api.hystrix.OrderClientHystrix;
import com.kunlun.entity.Order;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author by hws
 * @created on 2017/12/26.
 */
@FeignClient(value = "cloud-service-order", fallback = OrderClientHystrix.class)
public interface OrderClient {
    /**
     * 新增订单
     *
     * @param order
     * @return
     */
    @PostMapping("/wx/order/addOrder")
    DataRet<String> addOrder(@RequestBody Order order);

    /**
     * 修改预付款订单号
     *
     * @param id
     * @param prepayId
     * @return
     */
    @PostMapping("/wx/order/updatePrepayId")
    DataRet<String> updateOrderPrepayId(@RequestParam("id") Long id, @RequestParam("prepayId") String prepayId);

    /**
     * 根据id查找订单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("/wx/order/findById")
    DataRet<Order> findById(@RequestParam(value = "orderId") Long orderId);

    /**
     * 修改订单状态
     *
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/backend/order/updateStatusById")
    DataRet<String> modifyOrderStatus(@RequestParam(value = "id") Long id,
                                      @RequestParam(value = "status") String status);

    /**
     * 根据订单号查找订单
     *
     * @param orderNo
     * @return
     */
    @GetMapping("/backend/order/findByOrderNo")
    DataRet<Order> findOrderByOrderNo(@RequestParam(value = "orderNo") String orderNo);

    /**
     * 修改订单状态和支付订单号
     *
     * @param id
     * @param status
     * @param wxOrderNo
     * @return
     */
    @PostMapping("/backend/order/modifyStatusAndPayOrderNo")
    DataRet<String> modifyStatusAndPayOrderNo(@RequestParam(value = "id") Long id,
                                              @RequestParam(value = "status") String status,
                                              @RequestParam(value = "wxOrderNo") String wxOrderNo);
}

