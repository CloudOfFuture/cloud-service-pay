package com.kunlun.api.hystrix;

import com.kunlun.api.client.OrderClient;
import com.kunlun.entity.Order;
import com.kunlun.result.DataRet;
import org.springframework.stereotype.Component;

/**
 * @author by hws
 * @created on 2017/12/26.
 */
@Component
public class OrderClientHystrix implements OrderClient {
    /**
     * 新增订单
     *
     * @param order
     * @return
     */
    @Override
    public DataRet<String> addOrder(Order order) {
        return new DataRet<>("ERROR", "新增订单发生异常");
    }

    /**
     * 修改预付款订单号
     *
     * @param id
     * @param prepayId
     * @return
     */
    @Override
    public DataRet<String> updateOrderPrepayId(Long id, String prepayId) {
        return new DataRet<>("ERROR", "修改预付款订单号发生异常");
    }

    /**
     * 根据id查找订单详情
     *
     * @param orderId
     * @return
     */
    @Override
    public DataRet<Order> findById(Long orderId) {
        return new DataRet<>("ERROR", "查找订单详情异常");
    }


    /**
     * 修改订单状态
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public DataRet<String> modifyOrderStatus(Long id, String status) {
        return new DataRet<>("ERROR", "修改订单状态发生异常");
    }

    /**
     * 根据订单号查找订单
     *
     * @param orderNo
     * @return
     */
    @Override
    public DataRet<Order> findOrderByOrderNo(String orderNo) {
        return new DataRet<>("ERROR", "查找订单发生异常");
    }

    /**
     * 修改订单状态和支付订单号
     *
     * @param id
     * @param status
     * @param wxOrderNo
     * @return
     */
    @Override
    public DataRet<String> modifyStatusAndPayOrderNo(Long id, String status, String wxOrderNo) {
        return new DataRet<>("ERROR", "修改订单状态失败");
    }
}
