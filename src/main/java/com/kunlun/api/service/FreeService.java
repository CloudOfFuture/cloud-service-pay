package com.kunlun.api.service;

import com.kunlun.result.DataRet;
import com.kunlun.wxentity.UnifiedRequestData; /**
 * @author by hmy
 * @version <0.1>
 * @created on 2018-01-04.
 */
public interface FreeService {


    /**
     * 试用支付预付款订单
     *
     * @param unifiedRequestData
     * @param ipAddress
     * @return
     */
    DataRet apply(UnifiedRequestData unifiedRequestData, String ipAddress);
}
