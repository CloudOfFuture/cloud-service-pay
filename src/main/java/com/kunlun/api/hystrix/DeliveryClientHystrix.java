package com.kunlun.api.hystrix;

import com.kunlun.api.client.DeliveryClient;
import com.kunlun.entity.Delivery;
import com.kunlun.result.DataRet;
import org.springframework.stereotype.Component;

/**
 * @author by hws
 * @created on 2017/12/26.
 */
@Component
public class DeliveryClientHystrix implements DeliveryClient {


    /**
     * 根据id查找收货详情
     *
     * @param deliveryId
     * @return
     */
    @Override
    public DataRet<Delivery> findById(Long deliveryId) {
        return new DataRet("ERROR","获取收货地址异常");
    }
}
