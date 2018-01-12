package com.kunlun.api.service;

import com.kunlun.result.DataRet;
import com.kunlun.wxentity.UnifiedOrderNotifyRequestData;
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
     * @return
     */
    DataRet<Object> unifiedOrder(UnifiedRequestData unifiedRequestData);

    /**
     * 重新支付
     *
     * @param id
     * @return
     */
    DataRet<Object> repay(Long id);

    /**
     * 支付成功回调
     *
     * @param unifiedOrderNotifyRequestData
     * @return
     */
    DataRet<String> payCallBack(UnifiedOrderNotifyRequestData unifiedOrderNotifyRequestData);

}
