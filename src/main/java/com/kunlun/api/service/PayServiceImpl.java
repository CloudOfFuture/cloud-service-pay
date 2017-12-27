package com.kunlun.api.service;

import com.alibaba.fastjson.JSON;
import com.kunlun.api.client.*;
import com.kunlun.constant.Constant;
import com.kunlun.entity.*;
import com.kunlun.enums.CommonEnum;
import com.kunlun.exception.PayException;
import com.kunlun.result.DataRet;
import com.kunlun.utils.*;
import com.kunlun.wxentity.UnifiedOrderResponseData;
import com.kunlun.wxentity.UnifiedRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Logger;

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
        DataRet<Good> goodRet = goodClient.checkGoodById(unifiedRequestData.getGoodId(),
                                                           unifiedRequestData.getCount(),
                                                           unifiedRequestData.getOrderFee());
        if(!goodRet.isSuccess()){
            return new DataRet<>("ERROR",goodRet.getMessage());
        }

        //查询收货地址
        DataRet<Delivery> deliveryRet = deliveryClient.checkDelivery(unifiedRequestData.getDeliveryId());
        if(!deliveryRet.isSuccess()){
            return new DataRet<>("ERROR",deliveryRet.getMessage());
        }

        //创建商品快照
        GoodSnapshot goodSnapshot = CommonUtil.snapshotConstructor(goodRet.getBody(),unifiedRequestData.getGoodId());
        DataRet<String> goodSnapShotRet = goodClient.addGoodSnapShot(goodSnapshot);//成功后,数据库新生成的id将赋值到goodSnapShot的id
        if(!goodSnapShotRet.isSuccess()){
            return new DataRet<>("ERROR",goodSnapShotRet.getMessage());
        }

        //构建订单
        Order postOreder = CommonUtil.constructOrder(goodRet.getBody(),
                                                     goodSnapshot.getId(),
                                                     unifiedRequestData,
                                                     deliveryRet.getBody(),
                                                      openid);
        DataRet<String> orderRet =  orderClient.addOrder(postOreder);
        if(!orderRet.isSuccess()){
            return new DataRet<>("ERROR",orderRet.getMessage());
        }

        //生成订单日志
        OrderLog orderLog = constructOrderLog(postOreder.getOrderNo(),"生成预付款订单",ipAddress,postOreder.getId());
        DataRet<String> logRet = logClient.addOrderLog(orderLog);
        if(!logRet.isSuccess()){
            return new DataRet<>("ERROR",logRet.getMessage());
        }

        //调用微信下单接口
        String nonce_str = WxUtil.createRandom(false,10);
        String unifiedOrderXML = WxSignUtil.unifiedOrder(goodRet.getBody().getGoodName(),openid,
                                                         postOreder.getOrderNo(),
                                                         postOreder.getPaymentFee(),
                                                         nonce_str,"UNIFIED");
        //调用微信统一下单接口,生成预付款订单
        String wxOrderResponse = WxUtil.httpsRequest(WxPayConstant.WECHAT_UNIFIED_ORDER_URL,"POST",unifiedOrderXML);
        //将xml返回信息转换成bean
        UnifiedOrderResponseData unifiedOrderResponseData = XmlUtil.castXMLStringToUnifiedOrderResponseData(wxOrderResponse);

        if("FAIL".equalsIgnoreCase(unifiedOrderResponseData.getReturn_code())){
            //扔出微信下单错误
            throw  new PayException(unifiedOrderResponseData.getReturn_msg());
        }

        //修改订单预付款订单号
        DataRet<String> prepayIdRet = orderClient.updateOrderPrepayId(postOreder.getId(),unifiedOrderResponseData.getPrepay_id());
        if(!prepayIdRet.isSuccess()){
            return new DataRet<>("ERROR","修改预付款订单号失败");
        }

        //时间戳
        Long timeStamp = System.currentTimeMillis()/1000;

        //随机字符串
        String nonceStr =  WxSignUtil.createRandom(false,10);

        //生成支付签名
        Map<String,Object> map = WxSignUtil.payParam(timeStamp,nonceStr,unifiedOrderResponseData.getPrepay_id());
        String paySign = WxSignUtil.paySign(map);
        map.put("paySign",paySign);
        return new DataRet<>(JSON.toJSON(map));
    }

    /**
     * 重新支付
     *
     * @param id
     * @return
     */
    @Override
    public DataRet<Object> repay(Long id) {
        // 校验订单信息
        DataRet<Order> orderDataRet = checkOrder(id);
        if(!orderDataRet.isSuccess()){
            return new DataRet("ERROR",orderDataRet.getMessage());
        }
        //初始化订单实例
        Order order = orderDataRet.getBody();

        //校验商品信息
        DataRet<Good> goodDataRet = goodClient.checkGoodById(order.getGoodId(),0,0);
        if(!goodDataRet.isSuccess()){
            return new DataRet<>("ERROR",goodDataRet.getMessage());
        }

        //生成签名
        Long timeStamp = System.currentTimeMillis()/1000;
        String nonceStr = WxUtil.createRandom(false,10);
        Map<String,Object> map = WxSignUtil.payParam(timeStamp,nonceStr,order.getPrepayId());
        String paySign = WxSignUtil.paySign(map);
        map.put("paySign",paySign);

        return new DataRet<>(JSON.toJSON(map));
    }




    /**
     * 组装订单日志
     * @param orderNo
     * @param action
     * @param ipAddress
     * @param orderId
     * @return
     */
    private OrderLog constructOrderLog(String orderNo,String action,String ipAddress,Long orderId){
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderNo(orderNo);
            orderLog.setAction(action);
            orderLog.setIpAddress(ipAddress);
            orderLog.setOrderId(orderId);
            return orderLog;
    }

    /**
     * 校验订单信息
     * @param orderId
     * @return
     */
    private DataRet<Order> checkOrder(Long orderId){
        DataRet<Order> orderDataRet = orderClient.findOrderById(orderId);
        if(!orderDataRet.isSuccess()){
            return new DataRet<>("ERROR",orderDataRet.getMessage());
        }

        Order order = orderDataRet.getBody();

        if(CommonEnum.UN_PAY.getCode().equalsIgnoreCase(order.getOrderStatus())){
            return new DataRet<>("ERROR","订单状态异常");
        }

        //订单超时处理
        Long createTime = order.getCreateDate().getTime();
        Long currentTime = System.currentTimeMillis();
        if(currentTime-createTime>= Constant.TIME_TWO_HOUR){
            //关闭订单
            DataRet<String> closeOrderRet = orderClient.modifyOrderStatus(orderId,CommonEnum.CLOSING.getCode());
            if(!closeOrderRet.isSuccess()){
                return new DataRet<>("ERROR","订单支付超时,订单关闭失败");
            }
            return new DataRet<>("ERROR","订单支付超时，请重新下单");
        }
        return new DataRet<>(order);
    }
}
