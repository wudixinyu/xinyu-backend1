package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgShopStore;

/**
 * 店铺设置Service接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface IYgShopStoreService
{
    /**
     * 查询店铺
     */
    public YgShopStore selectYgShopStoreByStoreId(Long storeId);

    /**
     * 查询店铺列表
     */
    public List<YgShopStore> selectYgShopStoreList(YgShopStore ygShopStore);

    /**
     * 查询启用店铺下拉选项
     */
    public List<YgShopStore> selectEnabledStoreOptions();

    /**
     * 新增店铺
     */
    public int insertYgShopStore(YgShopStore ygShopStore);

    /**
     * 修改店铺
     */
    public int updateYgShopStore(YgShopStore ygShopStore);

    /**
     * 批量删除店铺
     */
    public int deleteYgShopStoreByStoreIds(Long[] storeIds);

    /**
     * 测试店铺连接
     */
    public void testConnection(Long storeId);

    /**
     * 获取需要同步的店铺：指定店铺或全部启用店铺
     */
    public List<YgShopStore> selectSyncStores(Long storeId);

    /**
     * 更新最近同步时间
     */
    public void updateLastSyncTime(Long storeId);
}
