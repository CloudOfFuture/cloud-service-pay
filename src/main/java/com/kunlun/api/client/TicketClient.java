package com.kunlun.api.client;

import com.kunlun.api.hystrix.TicketClientHystrix;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@FeignClient(value = "cloud-service-common",fallback = TicketClientHystrix.class)
public interface TicketClient {

    /**
     * 检查优惠券
     * @param userTicket
     * @param ticketId
     * @return
     */
    @GetMapping("/checkTicket")
    DataRet<String> checkTicket(@RequestParam("useTicket") String userTicket, @RequestParam("ticketId") Long ticketId);

    /**
     * 修改用户优惠券状态
     * @param status
     * @param id
     * @return
     */
    @GetMapping("/modifyStatus")
    DataRet<String> modifyStatus(@RequestParam(value = "id") Long id,@RequestParam(value = "status") String status);
}
