package com.kunlun.service;

import com.kunlun.result.DataRet;
import com.kunlun.utils.WxUtil;
import com.kunlun.wxentity.UnifiedRequestData;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/21.
 */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 统一下单
     *
     * @param unifiedRequestData
     * @param ipAddress
     * @return
     */
    @Override
    public DataRet<Object> unifiedOrder(UnifiedRequestData unifiedRequestData, String ipAddress) {
        String openid = WxUtil.getOpenId(unifiedRequestData.getWxCode());
        String checkPointUrl = "http://cloud-ribbon-server/api/point/checkPoint?pointValue=" + unifiedRequestData.getPoint() + "&openid=" + openid;
        //TODO 积分判断

        String pointMsg = restTemplate.getForObject(checkPointUrl, String.class);
        if (!StringUtils.isNullOrEmpty(pointMsg)) {
            return new DataRet<>("ERROR", pointMsg);
        }


        //TODO 优惠券校验
        //TODO 查询商品库存
        //TODO 查询收货地址
        //TODO 创建商品快照
        return null;
    }

}
