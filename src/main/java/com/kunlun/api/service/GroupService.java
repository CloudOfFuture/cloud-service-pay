package com.kunlun.api.service;

import com.kunlun.result.DataRet;
import com.kunlun.wxentity.UnifiedOrderNotifyRequestData;
import com.kunlun.wxentity.UnifiedRequestData;

/**
 * @author by fk
 * @version <0.1>
 * @created on 2018-01-05.
 */
public interface GroupService {

    /**
     * 参见拼团
     *
     * @param unifiedRequestData
     * @param ipAddress
     * @return
     */
    DataRet joinGroup(UnifiedRequestData unifiedRequestData, String ipAddress);

    /**
     * 回调
     *
     * @param unifiedOrderNotifyRequestData
     * @return
     */
    DataRet callBack(UnifiedOrderNotifyRequestData unifiedOrderNotifyRequestData);
}
