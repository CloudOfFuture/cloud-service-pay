package com.kunlun.api.hystrix;

import com.kunlun.api.client.PointClient;
import com.kunlun.result.DataRet;
import org.springframework.stereotype.Component;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@Component
public class PointClientHystrix implements PointClient {
    @Override
    public DataRet<String> checkPoint(Integer pointValue, String openid) {
        return new DataRet<>("ERROR", "积分校验接口异常");
    }
}
