package com.kunlun.api.client;

import com.kunlun.api.hystrix.PointClientHystrix;
import com.kunlun.entity.Point;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@FeignClient(value = "cloud-service-user-center", fallback = PointClientHystrix.class)
public interface PointClient {


    /**
     * 校验积分
     * @param pointValue
     * @param openid
     * @return
     */
    @GetMapping("/point/checkPoint")
    DataRet<String> checkPoint(@RequestParam("pointValue") Integer pointValue,
                               @RequestParam("openid") String openid);

    /**
     * 积分操作
     * @param point
     * @param userId
     * @return
     */
    @PostMapping("/point/updatePoint")
    DataRet<String> updatePoint(@RequestParam("point") Integer point,
                                @RequestParam("userId") String userId);

    /**
     * 根据用户ID查询积分
     * @param userId
     * @return
     */
    @GetMapping("/point/findPointByUserId")
    DataRet<Point> findPointByUserId(@RequestParam("userId") String userId);

}
