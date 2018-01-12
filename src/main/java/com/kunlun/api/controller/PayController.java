package com.kunlun.api.controller;

import com.kunlun.result.DataRet;
import com.kunlun.api.service.PayService;
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
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/21.
 */
@RestController
@RequestMapping("pay")
public class PayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private PayService payService;

    /**
     * 统一下单
     *
     * @param unifiedRequestData
     * @return
     */
    @PostMapping("/order/unifiedOrder")
    public DataRet<Object> unifiedOrder(@RequestBody UnifiedRequestData unifiedRequestData) {

        return payService.unifiedOrder(unifiedRequestData);
    }

    /**
     * 重新支付
     *
     * @param orderId
     * @return
     */
    @PostMapping("/order/repay")
    public DataRet<Object> repay(@RequestParam(value = "orderId") Long orderId) {
        return payService.repay(orderId);
    }


    /**
     * 支付成功回调
     *
     * @param request
     */
    @PostMapping("/order/payCallback")
    public void payCallBack(HttpServletRequest request) {
        String inputLine;
        StringBuilder notifyXml = new StringBuilder();
        //读取流中的信息
        try {
            while ((inputLine = request.getReader().readLine()) != null) {
                notifyXml.append(inputLine);//添加内容
            }
            request.getReader().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将xml字符串转换成统一下单请求对象
        UnifiedOrderNotifyRequestData unifiedOrderNotifyRequestData = XmlUtil.castXMLStringToUnifiedOrderNotifyRequestData(
                notifyXml.toString());
        if ("FAIL".equalsIgnoreCase(unifiedOrderNotifyRequestData.getReturn_code())) {
            LOGGER.error("@微信错误-----" + unifiedOrderNotifyRequestData.getReturn_msg());
            return;
        }
        //支付成功回调函数
        DataRet<String> callbackRet = payService.payCallBack(unifiedOrderNotifyRequestData);
        if (callbackRet.isSuccess()) {
            requestWx();
        }
    }

    private void requestWx() {
        //通知微信端成功处理支付
        UnifiedOrderNotifyResponseData unifiedOrderNotifyResponseData = new UnifiedOrderNotifyResponseData();
        unifiedOrderNotifyResponseData.setReturn_msg("OK");
        unifiedOrderNotifyResponseData.setReturn_code("SUCCESS");
        String responseXML = XmlUtil.castDataToXMLString(unifiedOrderNotifyResponseData);
        //通知微信回调成功
        WxUtil.httpsRequest(WxPayConstant.WECHAT_UNIFIED_ORDER_URL, "POST", responseXML);
    }

    /**
     * 接收微信回调
     *
     * @param unifiedOrderNotifyRequestData UnifiedOrderNotifyRequestData
     * @return UnifiedOrderNotifyRequestData
     */
    @PostMapping(value = "/callback")
    public DataRet callback(UnifiedOrderNotifyRequestData unifiedOrderNotifyRequestData) {
        return payService.payCallBack(unifiedOrderNotifyRequestData);
    }
}
