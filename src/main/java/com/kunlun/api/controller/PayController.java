package com.kunlun.api.controller;

import com.kunlun.result.DataRet;
import com.kunlun.api.service.PayService;
import com.kunlun.utils.IpUtil;
import com.kunlun.wxentity.UnifiedRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/21.
 */
@RestController
@RequestMapping("pay")
public class PayController {

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


}
