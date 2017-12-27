package com.kunlun.api.service;

import com.kunlun.api.client.*;
import com.kunlun.entity.*;
import com.kunlun.enums.CommonEnum;
import com.kunlun.result.DataRet;
import com.kunlun.utils.CommonUtil;
import com.kunlun.utils.WxSignUtil;
import com.kunlun.utils.WxUtil;
import com.kunlun.wxentity.UnifiedRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private DeliveryClient deliveryClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private LogClient logClient;

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
        //使用优惠券
        DataRet<String> useTicketRet = ticketClient.modifyStatus(ticketId, CommonEnum.ALREADY_USED.getCode());
        if(!useTicketRet.isSuccess()){
            return new DataRet<>("ERROR",useTicketRet.getMessage());
        }


        //商品信息校验
        DataRet<String> goodRet = goodClient.checkGoodById(unifiedRequestData.getGoodId(),
                                                           unifiedRequestData.getCount(),
                                                           unifiedRequestData.getOrderFee());
        if(!goodRet.isSuccess()){
            return new DataRet<>("ERROR",goodRet.getMessage());
        }

        //查询收货地址
        DataRet<String> deliveryRet = deliveryClient.checkDelivery(unifiedRequestData.getDeliveryId());
        if(!deliveryRet.isSuccess()){
            return new DataRet<>("ERROR",deliveryRet.getMessage());
        }

        //创建商品快照
        Good good = goodClient.findById(unifiedRequestData.getGoodId()).getBody();
        GoodSnapshot goodSnapshot = CommonUtil.snapshotConstructor(good,unifiedRequestData.getGoodId());
        DataRet<String> goodSnapShotRet = goodClient.addGoodSnapShot(goodSnapshot);//成功后,数据库新生成的id将赋值到goodSnapShot的id
        if(!goodSnapShotRet.isSuccess()){
            return new DataRet<>("ERROR",goodSnapShotRet.getMessage());
        }

        //TODO 构建订单
        Delivery delivery = deliveryClient.findById(unifiedRequestData.getDeliveryId()).getBody();
        Order postOreder = CommonUtil.constructOrder(good, goodSnapshot.getId(),unifiedRequestData,delivery,openid);
        DataRet<String> orderRet =  orderClient.addOrder(postOreder);
        if(!orderRet.isSuccess()){
            return new DataRet<>("ERROR",orderRet.getMessage());
        }
        //TODO 生成订单日志
        OrderLog orderLog = constructOrderLog(postOreder.getOrderNo(),"生成预付款订单",ipAddress,postOreder.getId());
        DataRet<String> logRet = logClient.addOrderLog(orderLog);
        if(!logRet.isSuccess()){
            return new DataRet<>("ERROR",logRet.getMessage());
        }

        //TODO 调用支付接口
        String nonce_str = WxUtil.createRandom(false,10);
        String unifiedOrderXML = WxSignUtil.unifiedOrder(good.getGoodName(),openid,
                                                         postOreder.getOrderNo(),
                                                         postOreder.getPaymentFee(),
                                                         nonce_str,"UNIFIED");



        return null;
    }

    private OrderLog constructOrderLog(String orderNo,String action,String ipAddress,Long orderId){
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderNo(orderNo);
            orderLog.setAction(action);
            orderLog.setIpAddress(ipAddress);
            orderLog.setOrderId(orderId);
            return orderLog;
    }

}
