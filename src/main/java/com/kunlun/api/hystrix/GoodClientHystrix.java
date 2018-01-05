package com.kunlun.api.hystrix;

import com.kunlun.api.client.GoodClient;
import com.kunlun.entity.Good;
import com.kunlun.entity.GoodSnapshot;
import com.kunlun.result.DataRet;
import org.springframework.stereotype.Component;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/26.
 */
@Component
public class GoodClientHystrix implements GoodClient {


    /**
     * 商品信息检查
     *
     * @param goodId
     * @param count
     * @param orderFee @return
     */
    @Override
    public DataRet<Good> checkGoodById(Long goodId, Integer count, Integer orderFee) {
        return new DataRet<>("ERROR","商品校验接口异常");
    }

    /**
     * 根据id查找详情
     *
     * @param id
     * @return
     */
    @Override
    public DataRet findById(Long id) {
        return new DataRet("ERROR","查询商品详情失败");
    }

    /**
     * 新增商品快照
     *
     * @param goodSnapshot
     * @return
     */
    @Override
    public DataRet addGoodSnapShot(GoodSnapshot goodSnapshot) {
        return new DataRet("Error","商品快照新增失败");
    }

    /**
     * 修改商品库存
     *
     * @param id
     * @param count
     * @return
     */
    @Override
    public DataRet<String> updateGoodStock(Long id, Integer count) {
        return new DataRet<>("ERROR","修改商品库存发生异常");
    }


    /**
     * 修改销量
     *
     * @param count
     * @param goodId
     * @return
     */
    @Override
    public DataRet<String> updateSaleVolume(Integer count, Long goodId) {
        return new DataRet<>("ERROR","修改销量失败");
    }
}
