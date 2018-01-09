package com.kunlun.api.controller;

import com.kunlun.api.service.FreeService;
import com.kunlun.result.DataRet;
import com.kunlun.utils.IpUtil;
import com.kunlun.utils.WxPayConstant;
import com.kunlun.utils.WxUtil;
import com.kunlun.utils.XmlUtil;
import com.kunlun.wxentity.UnifiedOrderNotifyRequestData;
import com.kunlun.wxentity.UnifiedOrderNotifyResponseData;
import com.kunlun.wxentity.UnifiedRequestData;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author by hmy
 * @version <0.1>
 * @created on 2018-01-03.
 */
@RestController
@RequestMapping("/wx/free")
public class FreeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(FreeController.class);

    @Autowired
    private FreeService freeService;


    /**
     * 试用预付款订单
     *
     * @param unifiedRequestData
     * @return
     */
    @PostMapping("/apply")
    public DataRet apply(@RequestBody UnifiedRequestData unifiedRequestData){
        return freeService.apply(unifiedRequestData);
    }


    /**
     * 回调
     *
     * @param request
     */
    @PostMapping("/callBack")
    public void callBack(javax.servlet.http.HttpServletRequest request){

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

        DataRet<String> dataRet = freeService.callBack(unifiedOrderNotifyRequestData);

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
