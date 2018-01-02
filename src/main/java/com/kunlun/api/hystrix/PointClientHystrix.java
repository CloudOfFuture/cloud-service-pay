package com.kunlun.api.hystrix;

import com.kunlun.api.client.PointClient;
import com.kunlun.entity.Point;
import com.kunlun.result.DataRet;
import org.springframework.stereotype.Component;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@Component
public class PointClientHystrix implements PointClient {
    /**
     * 积分校验
     * @param pointValue
     * @param openid
     * @return
     */
    @Override
    public DataRet<String> checkPoint(Integer pointValue, String openid) {
        return new DataRet<>("ERROR", "积分校验接口异常");
    }

    /**
     * 积分操作
     * @param point
     * @param userId
     * @return
     */
    @Override
    public DataRet<String> updatePoint(Integer point, String userId) {
        return new DataRet<>("ERROR","积分操作发生异常");
    }

    /**
     * 根据用户ID查询积分
     *
     * @param userId
     * @return
     */
    @Override
    public DataRet<Point> findPointByUserId(String userId) {
        return new DataRet<>("ERROR","查询用户积分发生异常");
    }
}
