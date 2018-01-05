package com.kunlun.api.hystrix;

import com.kunlun.api.client.ActivityClient;
import com.kunlun.entity.ActivityGood;
import com.kunlun.result.DataRet;
import org.springframework.stereotype.Component;

/**
 * @author by hmy
 * @version <0.1>
 * @created on 2018-01-04.
 */
@Component
public class ActivityClientHystrix implements ActivityClient {


    /**
     * 校验活动
     *
     * @param goodId
     * @param activityId
     * @param userId
     * @return
     */
    @Override
    public DataRet<String> checkActivity(Long goodId, Long activityId, String userId) {
        return new DataRet<>("ERROR", "不可重复参加活动");
    }


    /**
     * 校验活动商品
     *
     * @param goodId
     * @return
     */
    @Override
    public DataRet<String> checkActivityGood(Long goodId) {
        return new DataRet<>("ERROR", "商品已过期");
    }


    /**
     * 获取活动商品库存库存
     *
     * @param goodId
     * @return
     */
    @Override
    public DataRet<ActivityGood> findActivityGoodStock(Long goodId) {
        return new DataRet<>("ERROR", "查无结果");
    }


    /**
     * 活动商品库存扣减
     *
     * @param id
     * @param count
     * @return
     */
    @Override
    public DataRet<String> updateStock(Long id, int count) {
        return new DataRet<>("ERROR", "库存扣减失败");
    }
}
