package com.kunlun.api.service;

import com.kunlun.result.DataRet;
import com.kunlun.wxentity.UnifiedRequestData;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/21.
 */
public interface PayService {

    /**
     * 统一下单
     *
     * @param unifiedRequestData
     * @param ipAddress
     * @return
     */
    DataRet<Object> unifiedOrder(UnifiedRequestData unifiedRequestData, String ipAddress);

}
