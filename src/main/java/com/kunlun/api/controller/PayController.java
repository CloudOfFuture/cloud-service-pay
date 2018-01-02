package com.kunlun.api.controller;

import ch.qos.logback.core.joran.spi.XMLUtil;
import com.kunlun.result.DataRet;
import com.kunlun.api.service.PayService;
import com.kunlun.utils.IpUtil;
import com.kunlun.utils.WxUtil;
import com.kunlun.utils.XmlUtil;
import com.kunlun.wxentity.UnifiedOrderNotifyRequestData;
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
    public DataRet<Object> unifiedOrder(@RequestBody UnifiedRequestData unifiedRequestData,
                                        HttpServletRequest request) {

        String ipAddress = IpUtil.getIPAddress(request);

        return payService.unifiedOrder(unifiedRequestData, ipAddress);
    }

    /**重新支付
     *
     * @param orderId
     * @return
     */
    @PostMapping("/order/repay")
    public DataRet<Object> repay(@RequestParam(value = "orderId") Long orderId){
        return payService.repay(orderId);
    }


    /**
     * 支付成功回调
     * @param request
     */
    @PostMapping("/order/payCallback")
    public void payCallBack(HttpServletRequest request){
        String inputLine;
        StringBuffer notifyXml = new StringBuffer();
        //读取流中的信息
        try{
            while((inputLine = request.getReader().readLine())!=null){
                notifyXml.append(inputLine);//添加内容
            }
            request.getReader().close();
        }catch (IOException e){
            e.printStackTrace();
        }
        //将xml字符串转换成统一下单请求对象
        UnifiedOrderNotifyRequestData unifiedOrderNotifyRequestData = XmlUtil.castXMLStringToUnifiedOrderNotifyRequestData(
                notifyXml.toString());
        if("FAIL".equalsIgnoreCase(unifiedOrderNotifyRequestData.getReturn_code())){
            LOGGER.error("@微信错误-----"+unifiedOrderNotifyRequestData.getReturn_msg());
            return;
        }
        //支付成功回调


    }


}
