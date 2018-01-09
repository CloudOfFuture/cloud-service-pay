package com.kunlun.api.client;

import com.kunlun.api.hystrix.GoodClientHystrix;
import com.kunlun.entity.Good;
import com.kunlun.entity.GoodSnapshot;
import com.kunlun.result.DataRet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@FeignClient(value = "cloud-service-good")
public interface GoodClient {

    /**
     * 商品信息检查
     * @param goodId
     * @return
     */
    @GetMapping("/backstage/good/checkGood")
    DataRet<Good> checkGoodById(@RequestParam("goodId")Long goodId,
                                  @RequestParam("count") Integer count,
                                  @RequestParam("orderFee") Integer orderFee);


    /**
     * 根据id查找详情
     * @param goodId
     * @return
     */
    @GetMapping("/backstage/good/findById")
    DataRet<Good> findById(@RequestParam(value = "goodId") Long goodId);

    /**
     * 新增商品快照
     * @param goodSnapshot
     * @return
     */
    @PostMapping("/wx/good/addGoodSnapShot")
    DataRet<String> addGoodSnapShot(@RequestBody GoodSnapshot goodSnapshot);


    /**
     * 修改商品库存
     * @param id
     * @param count
     * @return
     */
    @PostMapping("/seller/good/updateGoodStock")
    DataRet<String> updateGoodStock(@RequestParam(value = "id") Long id,
                                    @RequestParam(value = "count") Integer count);

    /**
     * 商品销量
     *
     * @param count
     * @param goodId
     * @return
     */
    @PostMapping("/backstage/good/updateSaleVolume")
    DataRet<String> updateSaleVolume(@RequestParam(value = "count") Integer count,
                                     @RequestParam(value = "goodId") Long goodId);
}
