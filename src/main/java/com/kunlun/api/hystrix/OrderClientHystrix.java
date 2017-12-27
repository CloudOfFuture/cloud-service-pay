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
}
