package com.kunlun.api.controller;

import com.kunlun.api.service.GroupService;
import com.kunlun.result.DataRet;
import com.kunlun.utils.IpUtil;
import com.kunlun.utils.WxPayConstant;
import com.kunlun.utils.WxUtil;
import com.kunlun.utils.XmlUtil;
import com.kunlun.wxentity.UnifiedOrderNotifyRequestData;
import com.kunlun.wxentity.UnifiedOrderNotifyResponseData;
import com.kunlun.wxentity.UnifiedRequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author by fk
 * @version <0.1>
 * @created on 2018-01-05.
 */
@RestController
@RequestMapping("/wx/group")
public class GroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupService groupService;


    /**
     * 参见拼图
     *
     * @param unifiedRequestData
     * @param request
     * @return
     */
    @PostMapping("/joinGroup")
    public DataRet joinGroup(@RequestBody UnifiedRequestData unifiedRequestData, HttpServletRequest request) {
        String ipAddress = IpUtil.getIPAddress(request);
        return groupService.joinGroup(unifiedRequestData, ipAddress);
    }

    /**
     * 回调
     *
     * @param request
     */
    @PostMapping("/callBack")
    public void callBack(HttpServletRequest request) {
        String inputLine;
        StringBuffer notifyXml = new StringBuffer();
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml.append(inputLine);
            }
            request.getReader().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UnifiedOrderNotifyRequestData unifiedOrderNotifyRequestData = XmlUtil.castXMLStringToUnifiedOrderNotifyRequestData(
                notifyXml.toString());

        //回调出现错误 返回
        if (unifiedOrderNotifyRequestData.getReturn_code().equalsIgnoreCase("FAIL")) {
            LOGGER.error("微信回调出错@------" + unifiedOrderNotifyRequestData.getReturn_msg());
            return;
        }

        DataRet dataRet = groupService.callBack(unifiedOrderNotifyRequestData);

        //回调成功   通知微信已经接收完成
        if (dataRet.isSuccess()) {
            UnifiedOrderNotifyResponseData unifiedOrderNotifyResponseData = new UnifiedOrderNotifyResponseData();
            unifiedOrderNotifyResponseData.setReturn_code("SUCCESS");
            unifiedOrderNotifyResponseData.setReturn_msg("OK");
            String responseXML = XmlUtil.castDataToXMLString(unifiedOrderNotifyResponseData);
            //通知微信回调接收成功
            WxUtil.httpsRequest(WxPayConstant.WECHAT_UNIFIED_ORDER_URL, "POST", responseXML);
        }
    }
}
