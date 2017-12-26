package com.kunlun.api.hystrix;

import com.kunlun.api.client.GoodClient;
import com.kunlun.result.DataRet;
import org.springframework.stereotype.Component;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@Component
public class GoodClientHystrix implements GoodClient {
    /**
     * 商品信息检查
     *
     * @param goodId
     * @return
     */
    @Override
    public DataRet<String> checkGoodById(Long goodId) {
        return new DataRet<>("ERROR", "商品校验接口异常");
    }
}
