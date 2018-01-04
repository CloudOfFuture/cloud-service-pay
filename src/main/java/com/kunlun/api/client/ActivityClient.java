package com.kunlun.api.client;

import com.kunlun.api.hystrix.ActivityClientHystrix;
import com.kunlun.entity.ActivityGood;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author by hmy
 * @version <0.1>
 * @created on 2018-01-04.
 */
@FeignClient(value = "cloud-service-common",fallback = ActivityClientHystrix.class)
public interface ActivityClient {

    /**
     * 活动校验
     *
     * @return
     */
    @GetMapping("/activity/checkActivity")
     DataRet<String> checkActivity(@RequestParam(value = "goodId") Long goodId,
                                         @RequestParam(value = "activityId") Long activityId,
                                         @RequestParam(value = "userId") String userId);

    /**
     * 校验活动商品
     *
     * @param goodId
     * @return
     */
    @GetMapping("/activity/checkActivityGood")
    DataRet<String>checkActivityGood(@RequestParam(value = "goodId") Long goodId);


    /**
     * 获取库存量
     *
     * @param goodId
     * @return
     */
    @GetMapping("/activity/getStock")
    DataRet<ActivityGood> findActivityGoodStock(@RequestParam(value = "goodId") Long goodId);


    /**
     * 库存扣减
     *
     * @param id
     * @param count
     * @return
     */
    @PostMapping("/activity/updateStock")
    DataRet<String> updateStock(@RequestParam("id") Long id,
                                @RequestParam("count") int count);
}
