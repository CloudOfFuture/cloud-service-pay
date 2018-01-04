package com.kunlun.api.service;

import com.kunlun.result.DataRet;
import com.kunlun.utils.WxUtil;
import com.kunlun.wxentity.UnifiedRequestData;
import org.springframework.stereotype.Service;

/**
 * @author by hmy
 * @version <0.1>
 * @created on 2018-01-04.
 */
@Service
public class FreeServiceImpl implements FreeService{


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

        return null;
    }
}
