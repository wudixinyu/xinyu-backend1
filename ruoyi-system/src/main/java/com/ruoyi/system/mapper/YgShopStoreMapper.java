package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgShopStore;

/**
 * 店铺设置Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface YgShopStoreMapper
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
     * 查询启用状态的店铺（下拉选项，不含密钥）
     */
    public List<YgShopStore> selectEnabledStoreOptions();

    /**
     * 查询启用状态的店铺（含密钥，供同步使用）
     */
    public List<YgShopStore> selectEnabledStores();

    /**
     * 新增店铺
     */
    public int insertYgShopStore(YgShopStore ygShopStore);

    /**
     * 修改店铺
     */
    public int updateYgShopStore(YgShopStore ygShopStore);

    /**
     * 删除店铺
     */
    public int deleteYgShopStoreByStoreIds(Long[] storeIds);

    /**
     * 更新最近同步时间
     */
    public int updateLastSyncTime(@Param("storeId") Long storeId, @Param("lastSyncTime") Date lastSyncTime);
}
