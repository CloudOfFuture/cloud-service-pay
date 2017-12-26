package com.kunlun.service;

import com.kunlun.entity.Delivery;
import com.kunlun.entity.Good;
import com.kunlun.entity.GoodSnapshot;
import com.kunlun.entity.Order;
import com.kunlun.result.DataRet;
import com.kunlun.utils.CommonUtil;
import com.kunlun.utils.WxUtil;
import com.kunlun.wxentity.UnifiedRequestData;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.OrderUtils;
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
    private RestTemplate restTemplate;

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
        String checkPointUrl = "http://cloud-ribbon-server/api/point/checkPoint?pointValue=" + unifiedRequestData.getPoint() + "&openid=" + openid;
        //TODO 积分判断

        String pointMsg = restTemplate.getForObject(checkPointUrl, String.class);
        if (!StringUtils.isNullOrEmpty(pointMsg)) {
            return new DataRet<>("ERROR", pointMsg);
        }


        //TODO 优惠券校验
        String checkTicketUrl = "http://cloud-ribbon-server/api/ticket/checkTicket?useTicket=" + unifiedRequestData.getUseTicket() +
                "&ticketId=" + unifiedRequestData.getTicket() ;
        String modifyTicketStatusUrl = "http://cloud-ribbon-server/api/ticket/modifyStatus?status=ALREADY_USED" +
                "&ticketId=" + unifiedRequestData.getTicket() ;
        String ticketMsg = restTemplate.getForObject(checkTicketUrl,String.class);
        //判断是否使用优惠券并且是否可用
        if("1".equals(unifiedRequestData.getUseTicket())&&StringUtils.isNullOrEmpty(ticketMsg)){
            //优惠券使用
            DataRet userTicketRes = restTemplate.getForObject(modifyTicketStatusUrl,DataRet.class);
            if(!userTicketRes.isSuccess()){
                return new DataRet<>("Error","使用优惠券失败");
            }
        }else if(!StringUtils.isNullOrEmpty(ticketMsg)){
            return new DataRet<>("Error",ticketMsg);
        }

        //TODO 查询商品库存与商品信息
        String getGoodUrl = "http://cloud-ribbon-server/api/good/findById?id="+unifiedRequestData.getGoodId();
        Good good = restTemplate.getForObject(getGoodUrl,Good.class);
        String goodMsg = checkGood(good,unifiedRequestData.getOrderFee(),unifiedRequestData.getCount());
        if(!StringUtils.isNullOrEmpty(goodMsg)){
            return new DataRet<>("Error",goodMsg);
        }

        //TODO 查询收货地址
        String getDelivey = "http://cloud-ribbon-server/api/delivey/findById?id="+unifiedRequestData.getDeliveryId();
        Delivery delivery = restTemplate.getForObject(getDelivey,Delivery.class);
        if(delivery==null){
            return new DataRet<>("Error","收货地址不存在");
        }

        //TODO 创建商品快照
        GoodSnapshot goodSnapshot = CommonUtil.snapshotConstructor(good,good.getId());
        String addGoodSnapShotUrl = "http://cloud-ribbon-server/api/wx/good/addGoodSnapShot";
        //创建商品快照操作
        DataRet addGoodResult = restTemplate.postForObject(addGoodSnapShotUrl,goodSnapshot,DataRet.class);
        if(!addGoodResult.isSuccess()){
            return new DataRet<>("Error","创建商品快照失败");
        }

        //TODO 构建订单
        Order postOreder = CommonUtil.constructOrder(good,goodSnapshot.getId(),unifiedRequestData,delivery,openid);
        return null;
    }

    /**
     * 校验商品信息
     * @param good
     * @param orderFee
     * @param count
     * @return
     */
    private String checkGood(Good good, Integer orderFee, Integer count){
        if(good==null||good.getStock() <= 0){
            return "该商品库存不足";
        }else if("UN_NORMA".equals(good.getStatus())){
            return "该商品已下架";
        }
        //商品信息判断
        int price = orderFee/count;
        if(price!=good.getPrice()){
            return "商品信息已过期,请重新下单";
        }
        return null;
    }


}
