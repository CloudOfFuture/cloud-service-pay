package com.kunlun.api.client;

import com.kunlun.api.hystrix.PointClientHystrix;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@FeignClient(value = "cloud-service-user-center", fallback = PointClientHystrix.class)
public interface PointClient {


    @GetMapping("/point/checkPoint")
    DataRet<String> checkPoint(@RequestParam("pointValue") Integer pointValue,
                               @RequestParam("openid") String openid);
}
