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
public class OrderClientHystrix implements OrderClient{
    /**
     * 新增订单
     *
     * @param order
     * @return
     */
    @Override
    public DataRet<String> addOrder(Order order) {
        return new DataRet<>("ERROR","新增订单发生异常");
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
        return new DataRet<>("ERROR","修改预付款订单号发生异常");
    }
}
