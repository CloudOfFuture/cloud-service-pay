package com.kunlun.api.service;

import com.kunlun.api.client.ActivityClient;
import com.kunlun.api.client.GoodClient;
import com.kunlun.entity.Good;
import com.kunlun.result.DataRet;
import com.kunlun.utils.WxUtil;
import com.kunlun.wxentity.UnifiedRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author by hmy
 * @version <0.1>
 * @created on 2018-01-04.
 */
@Service
public class FreeServiceImpl implements FreeService{


    @Autowired
    private ActivityClient activityClient;


    /**
     * 免费试用预付款订单
     *
     * @param unifiedRequestData
     * @param ipAddress
     * @return
     */
    @Override
    public DataRet apply(UnifiedRequestData unifiedRequestData, String ipAddress) {
        String userId= WxUtil.getOpenId(unifiedRequestData.getWxCode());

        //活动校验
        DataRet<String> activityRet=activityClient.checkActivity(unifiedRequestData.getGoodId(),unifiedRequestData.getActivityId(),userId);
        if (!activityRet.isSuccess()){
            return new DataRet("ERROR",activityRet.getMessage());
        }

        //校验活动商品是否还有库存

        return null;
    }
}
