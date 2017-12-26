package com.kunlun.api.client;

import com.kunlun.api.hystrix.GoodClientHystrix;
import com.kunlun.entity.Good;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@FeignClient(value = "cloud-service-good",fallback = GoodClientHystrix.class)
public interface GoodClient {

    /**
     * 商品信息检查
     * @param goodId
     * @return
     */
    @GetMapping("/checkGoodById")
    DataRet<String> checkGoodById(@RequestParam("goodId")Long goodId);

}
