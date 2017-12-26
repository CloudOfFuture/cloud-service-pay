package com.kunlun.api.service;

import com.kunlun.api.client.GoodClient;
import com.kunlun.api.client.PointClient;
import com.kunlun.api.client.TicketClient;
import com.kunlun.entity.Delivery;
import com.kunlun.entity.Good;
import com.kunlun.result.DataRet;
import com.kunlun.utils.WxUtil;
import com.kunlun.wxentity.UnifiedRequestData;
import com.mysql.jdbc.StringUtils;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/21.
 */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private PointClient pointClient;

    @Autowired
    private TicketClient ticketClient;

    @Autowired
    private GoodClient goodClient;

    /**
     * 统一下单
     *
     * @param unifiedRequestData
     * @param ipAddress
     * @return
     */
    @Override
    public DataRet<Object> unifiedOrder(UnifiedRequestData unifiedRequestData, String ipAddress) {
        String openid = WxUtil.getOpenId(unifiedRequestData.getWxCode());
        //积分校验
        Integer pointValue = unifiedRequestData.getPoint();
        DataRet<String> pointRet = pointClient.checkPoint(pointValue, openid);
        if (!pointRet.isSuccess()) {
            return new DataRet<>("ERROR",pointRet.getMessage());
        }
        //优惠券校验
        String useTicket = unifiedRequestData.getUseTicket();
        Long ticketId=unifiedRequestData.getTicketId();
        DataRet<String> ticketRet = ticketClient.checkTicket(useTicket,ticketId);
        if(!ticketRet.isSuccess()){
            return new DataRet<>("ERROR",ticketRet.getMessage());
        }
        //商品信息校验
        DataRet<String> goodRet = goodClient.checkGoodById(unifiedRequestData.getGoodId());
        if(!goodRet.isSuccess()){
            return new DataRet<>("ERROR",goodRet.getMessage());
        }

        //TODO 查询收货地址
        String getDelivey = "http://cloud-ribbon-server/api/delivey/findById?id=" + unifiedRequestData.getDeliveryId();
        Delivery delivery = restTemplate.getForObject(getDelivey, Delivery.class);

        //TODO 创建商品快照
        return null;
    }



}
