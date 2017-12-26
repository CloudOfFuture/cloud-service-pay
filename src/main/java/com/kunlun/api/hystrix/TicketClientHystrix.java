package com.kunlun.api.hystrix;

import com.kunlun.api.client.TicketClient;
import com.kunlun.result.DataRet;
import org.springframework.stereotype.Component;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@Component
public class TicketClientHystrix implements TicketClient {
    /**
     * 检查优惠券
     *
     * @param userTicket
     * @param ticketId
     * @return
     */
    @Override
    public DataRet<String> checkTicket(String userTicket, Long ticketId) {
        return new DataRet<>("ERROR","优惠券校验接口异常");
    }
}
