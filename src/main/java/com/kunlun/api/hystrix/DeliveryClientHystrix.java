package com.kunlun.api.hystrix;

import com.kunlun.api.client.DeliveryClient;
import com.kunlun.result.DataRet;
import org.springframework.stereotype.Component;

/**
 * @author by hws
 * @created on 2017/12/26.
 */
@Component
public class DeliveryClientHystrix implements DeliveryClient {
    /**
     * 校验收获地址
     *
     * @param id
     * @return
     */
    @Override
    public DataRet checkDelivery(Long id) {
        return new DataRet("Error","收获地址校验异常");
    }

    /**
     * 根据id查找收货详情
     *
     * @param id
     * @return
     */
    @Override
    public DataRet findById(Long id) {
        return new DataRet("ERROR","获取收货地址异常");
    }
}
