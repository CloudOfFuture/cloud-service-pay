package com.kunlun.api.service;

import com.kunlun.api.client.*;
import com.kunlun.constant.Constant;
import com.kunlun.entity.*;
import com.kunlun.enums.CommonEnum;
import com.kunlun.exception.PayException;
import com.kunlun.result.DataRet;
import com.kunlun.utils.*;
import com.kunlun.wxentity.UnifiedOrderNotifyRequestData;
import com.kunlun.wxentity.UnifiedOrderResponseData;
import com.kunlun.wxentity.UnifiedRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    @Autowired
    private ActivityClient activityClient;

    /**
     * 统一下单
     *
     * @param unifiedRequestData
     * @return
     */
    @Override
    public DataRet<Object> unifiedOrder(UnifiedRequestData unifiedRequestData) {
//        String openId = WxUtil.getOpenId(unifiedRequestData.getWxCode());
        //积分校验
        Integer pointValue = unifiedRequestData.getPoint();
        DataRet<String> pointRet = pointClient.checkPoint(pointValue, unifiedRequestData.getWxCode());
        if (!pointRet.isSuccess()) {
            return new DataRet<>("ERROR", pointRet.getMessage());
        }

        //优惠券校验
        String useTicket = unifiedRequestData.getUseTicket();
        Long ticketId = unifiedRequestData.getTicketId();
        DataRet<String> ticketRet = ticketClient.checkTicket(useTicket, ticketId);
        if (!ticketRet.isSuccess()) {
            return new DataRet<>("ERROR", ticketRet.getMessage());
        }
        //使用优惠券
        DataRet<String> useTicketRet = ticketClient.modifyStatus(ticketId, CommonEnum.ALREADY_USED.getCode());
        if (!useTicketRet.isSuccess()) {
            return new DataRet<>("ERROR", useTicketRet.getMessage());
        }


        //商品信息校验
        DataRet<Good> goodRet = goodClient.checkGood(unifiedRequestData.getGoodId(),
                unifiedRequestData.getCount(),
                unifiedRequestData.getOrderFee());
        if (!goodRet.isSuccess()) {
            return new DataRet<>("ERROR", goodRet.getMessage());
        }

        //查询收货地址
        DataRet<Delivery> deliveryRet = deliveryClient.findById(unifiedRequestData.getDeliveryId());
        if (!deliveryRet.isSuccess()) {
            return new DataRet<>("ERROR", deliveryRet.getMessage());
        }

        //创建商品快照
        GoodSnapshot goodSnapshot = CommonUtil.snapshotConstructor(goodRet.getBody(), unifiedRequestData.getGoodId());
        DataRet<String> goodSnapShotRet = goodClient.addGoodSnapShot(goodSnapshot);//成功后,数据库新生成的id将赋值到goodSnapShot的id
        if (!goodSnapShotRet.isSuccess()) {
            return new DataRet<>("ERROR", goodSnapShotRet.getMessage());
        }

        //构建订单
        Order postOreder = CommonUtil.constructOrder(goodRet.getBody(),
                goodSnapshot.getId(),
                unifiedRequestData,
                deliveryRet.getBody(),
                unifiedRequestData.getWxCode());
        DataRet<String> orderRet = orderClient.addOrder(postOreder);
        if (!orderRet.isSuccess()) {
            return new DataRet<>("ERROR", orderRet.getMessage());
        }

        //生成订单日志
        OrderLog orderLog = CommonUtil.constructOrderLog(postOreder.getOrderNo(), "生成预付款订单", unifiedRequestData.getIpAddress(), postOreder.getId());
        DataRet<String> logRet = logClient.addOrderLog(orderLog);
        if (!logRet.isSuccess()) {
            return new DataRet<>("ERROR", logRet.getMessage());
        }

        //调用微信下单接口
        String nonce_str = WxUtil.createRandom(false, 10);
        String unifiedOrderXML = WxSignUtil.unifiedOrder(goodRet.getBody().getGoodName(), unifiedRequestData.getWxCode(),
                postOreder.getOrderNo(),
                postOreder.getPaymentFee(),
                nonce_str, "UNIFIED");
        //调用微信统一下单接口,生成预付款订单
        String wxOrderResponse = WxUtil.httpsRequest(WxPayConstant.WECHAT_UNIFIED_ORDER_URL, "POST", unifiedOrderXML);
        //将xml返回信息转换成bean
        UnifiedOrderResponseData unifiedOrderResponseData = XmlUtil.castXMLStringToUnifiedOrderResponseData(wxOrderResponse);

        if ("FAIL".equalsIgnoreCase(unifiedOrderResponseData.getReturn_code())) {
            //扔出微信下单错误
            throw new PayException(unifiedOrderResponseData.getReturn_msg());
        }

        //修改订单预付款订单号
        DataRet<String> prepayIdRet = orderClient.updateOrderPrepayId(postOreder.getId(), unifiedOrderResponseData.getPrepay_id());
        if (!prepayIdRet.isSuccess()) {
            return new DataRet<>("ERROR", "修改预付款订单号失败");
        }

        //时间戳
        Long timeStamp = System.currentTimeMillis() / 1000;

        //随机字符串
        String nonceStr = WxSignUtil.createRandom(false, 10);

        //生成支付签名
        Map<String, Object> map = WxSignUtil.payParam(timeStamp, nonceStr, unifiedOrderResponseData.getPrepay_id());
        String paySign = WxSignUtil.paySign(map);
        map.put("paySign", paySign);
//        return new DataRet<>(JSON.toJSON(map));
        return new DataRet<>();
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
        if (!orderDataRet.isSuccess()) {
            return new DataRet("ERROR", orderDataRet.getMessage());
        }
        //初始化订单实例
        Order order = orderDataRet.getBody();

        if (CommonEnum.FREE_ORDER.getCode().equals(order.getOrderType())) {
            //校验活动商品是否还有库存
            DataRet<String> activityGoodRet = activityClient.checkActivityGood(order.getGoodId());
            if (!activityGoodRet.isSuccess()) {
                return new DataRet("ERROR", activityGoodRet.getMessage());
            }
        }
        if (CommonEnum.SPELL_GROUP.getCode().equals(order.getOrderType())) {
            //校验拼团活动商品是否还有库存
            DataRet activityGoodRet = activityClient.checkActivityGood(order.getGoodId());
            if (!activityGoodRet.isSuccess()) {
                return new DataRet<>("ERROR", activityGoodRet.getMessage());
            }
        }
        //校验商品信息
        DataRet<Good> goodDataRet = goodClient.checkGood(order.getGoodId(), 0, 0);
        if (!goodDataRet.isSuccess()) {
            return new DataRet<>("ERROR", goodDataRet.getMessage());
        }

        //生成签名
        Long timeStamp = System.currentTimeMillis() / 1000;
        String nonceStr = WxUtil.createRandom(false, 10);
        Map<String, Object> map = WxSignUtil.payParam(timeStamp, nonceStr, order.getPrepayId());
        String paySign = WxSignUtil.paySign(map);
        map.put("paySign", paySign);

//        return new DataRet<>(JSON.toJSON(map));
        return new DataRet<>(JacksonUtil.toJSON(map));
    }

    /**
     * 支付成功回调
     *
     * @param unifiedOrderNotifyRequestData
     * @return
     */
    @Override
    public DataRet<String> payCallBack(UnifiedOrderNotifyRequestData unifiedOrderNotifyRequestData) {
        //校验订单信息
        DataRet<Order> orderRet = orderClient.findOrderByOrderNo(unifiedOrderNotifyRequestData.getOut_trade_no());
        if (!orderRet.isSuccess()) {
            return new DataRet<>("ERROR", orderRet.getMessage());
        }

        Order order = orderRet.getBody();
        if (order.getPayDate() == null && CommonEnum.UN_PAY.getCode().equals(order.getOrderStatus())) {
            //修改订单状态与微信支付订单号
            DataRet<String> orderModifyRet = orderClient.modifyStatusAndPayOrderNo(order.getId(),
                    CommonEnum.UN_DELIVERY.getCode(), unifiedOrderNotifyRequestData.getTransaction_id());
            if (!orderModifyRet.isSuccess()) {
                return new DataRet<>("ERROR", orderModifyRet.getMessage());
            }
            //修改订单日志
            OrderLog orderLog = CommonUtil.constructOrderLog(order.getOrderNo(), "付款", "", order.getId());
            DataRet<String> orderLogRet = logClient.addOrderLog(orderLog);
            if (!orderLogRet.isSuccess()) {
                return new DataRet<>("ERROR", orderLogRet.getMessage());
            }
            //库存扣减
            Integer reduceCount = -1 * order.getCount();
            DataRet<String> goodStockRet = goodClient.updateGoodStock(order.getGoodId(), reduceCount);
            if (!goodStockRet.isSuccess()) {
                return new DataRet<>("ERROR", goodStockRet.getMessage());
            }
            //创建商品库存扣减日志
            GoodLog goodLog = CommonUtil.constructGoodLog(order.getGoodId(), order.getGoodName(), "库存扣减" + order.getCount());
            DataRet<String> goodLogRet = logClient.addGoodLog(goodLog);

            //积分扣减
            DataRet<String> pointRet = pointClient.updatePoint(order.getOperatePoint(), order.getUserId());
            if (!pointRet.isSuccess()) {
                return new DataRet<>("ERROR", pointRet.getMessage());
            }
            //获取当前积分
            DataRet<Point> currentPointRet = pointClient.findPointByUserId(order.getUserId());
            if (!currentPointRet.isSuccess()) {
                return new DataRet<>("ERROR", currentPointRet.getMessage());
            }
            //积分扣减日志构建
            PointLog pointLog = CommonUtil.constructPointLog(order.getUserId(), order.getOperatePoint(), currentPointRet.getBody().getPoint());
            DataRet<String> pointLogRet = logClient.addPointLog(pointLog);
        }
//        TODO:
        //校验
        return null;
    }

    /**
     * 校验订单信息
     *
     * @param orderId
     * @return
     */
    private DataRet<Order> checkOrder(Long orderId) {
        DataRet<Order> orderDataRet = orderClient.findById(orderId);
        if (!orderDataRet.isSuccess()) {
            return new DataRet<>("ERROR", orderDataRet.getMessage());
        }

        Order order = orderDataRet.getBody();

        if (!CommonEnum.UN_PAY.getCode().equalsIgnoreCase(order.getOrderStatus())) {
            return new DataRet<>("ERROR", "订单状态异常");
        }

        //订单超时处理
        Long createTime = order.getCreateDate().getTime();
        Long currentTime = System.currentTimeMillis();
        if (currentTime - createTime >= Constant.TIME_TWO_HOUR) {
            //关闭订单
            DataRet<String> closeOrderRet = orderClient.modifyOrderStatus(orderId, CommonEnum.CLOSING.getCode());
            if (!closeOrderRet.isSuccess()) {
                return new DataRet<>("ERROR", "订单支付超时,订单关闭失败");
            }
            return new DataRet<>("ERROR", "订单支付超时，请重新下单");
        }
        return new DataRet<>(order);
    }
}
