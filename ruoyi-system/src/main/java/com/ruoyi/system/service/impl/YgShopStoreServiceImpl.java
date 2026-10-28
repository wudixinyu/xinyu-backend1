package com.ruoyi.system.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgShopStore;
import com.ruoyi.system.mapper.YgShopStoreMapper;
import com.ruoyi.system.service.IYgShopStoreService;
import com.ruoyi.system.service.shop.YgShopWooClient;

/**
 * 店铺设置Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@Service
public class YgShopStoreServiceImpl implements IYgShopStoreService
{
    @Autowired
    private YgShopStoreMapper ygShopStoreMapper;

    @Autowired
    private YgShopWooClient ygShopWooClient;

    @Override
    public YgShopStore selectYgShopStoreByStoreId(Long storeId)
    {
        return ygShopStoreMapper.selectYgShopStoreByStoreId(storeId);
    }

    @Override
    public List<YgShopStore> selectYgShopStoreList(YgShopStore ygShopStore)
    {
        return ygShopStoreMapper.selectYgShopStoreList(ygShopStore);
    }

    @Override
    public List<YgShopStore> selectEnabledStoreOptions()
    {
        return ygShopStoreMapper.selectEnabledStoreOptions();
    }

    @Override
    public int insertYgShopStore(YgShopStore ygShopStore)
    {
        normalizeStore(ygShopStore);
        checkStoreUrlUnique(ygShopStore);
        ygShopStore.setCreateTime(DateUtils.getNowDate());
        return ygShopStoreMapper.insertYgShopStore(ygShopStore);
    }

    @Override
    public int updateYgShopStore(YgShopStore ygShopStore)
    {
        normalizeStore(ygShopStore);
        checkStoreUrlUnique(ygShopStore);
        ygShopStore.setUpdateTime(DateUtils.getNowDate());
        return ygShopStoreMapper.updateYgShopStore(ygShopStore);
    }

    @Override
    public int deleteYgShopStoreByStoreIds(Long[] storeIds)
    {
        return ygShopStoreMapper.deleteYgShopStoreByStoreIds(storeIds);
    }

    @Override
    public void testConnection(Long storeId)
    {
        YgShopStore store = ygShopStoreMapper.selectYgShopStoreByStoreId(storeId);
        if (store == null)
        {
            throw new ServiceException("店铺不存在");
        }
        ygShopWooClient.testConnection(store);
    }

    @Override
    public List<YgShopStore> selectSyncStores(Long storeId)
    {
        if (storeId != null)
        {
            YgShopStore store = ygShopStoreMapper.selectYgShopStoreByStoreId(storeId);
            if (store == null)
            {
                throw new ServiceException("店铺不存在");
            }
            if (!"0".equals(store.getStatus()))
            {
                throw new ServiceException("店铺【" + store.getStoreName() + "】已停用，无法同步");
            }
            return Collections.singletonList(store);
        }
        List<YgShopStore> stores = ygShopStoreMapper.selectEnabledStores();
        if (stores.isEmpty())
        {
            throw new ServiceException("暂无启用的店铺，请先在店铺设置中维护店铺");
        }
        return stores;
    }

    /**
     * 更新最近同步时间
     */
    @Override
    public void updateLastSyncTime(Long storeId)
    {
        ygShopStoreMapper.updateLastSyncTime(storeId, new Date());
    }

    /**
     * 名称/地址基础校验与规范化
     */
    private void normalizeStore(YgShopStore store)
    {
        if (StringUtils.isEmpty(store.getStoreName()))
        {
            throw new ServiceException("店铺名称不能为空");
        }
        if (StringUtils.isEmpty(store.getStoreUrl()))
        {
            throw new ServiceException("店铺地址不能为空");
        }
        String url = store.getStoreUrl().trim();
        while (url.endsWith("/"))
        {
            url = url.substring(0, url.length() - 1);
        }
        store.setStoreName(store.getStoreName().trim());
        store.setStoreUrl(url);
        store.setConsumerKey(StringUtils.trimToEmpty(store.getConsumerKey()));
        store.setConsumerSecret(StringUtils.trimToEmpty(store.getConsumerSecret()));
        if (store.getSort() == null)
        {
            store.setSort(0);
        }
        if (StringUtils.isEmpty(store.getStatus()))
        {
            store.setStatus("0");
        }
    }

    /**
     * 店铺地址唯一性校验
     */
    private void checkStoreUrlUnique(YgShopStore store)
    {
        YgShopStore query = new YgShopStore();
        query.setStoreUrl(store.getStoreUrl());
        List<YgShopStore> list = ygShopStoreMapper.selectYgShopStoreList(query);
        for (YgShopStore item : list)
        {
            if (store.getStoreUrl().equals(item.getStoreUrl())
                    && !item.getStoreId().equals(store.getStoreId()))
            {
                throw new ServiceException("店铺地址已存在：" + store.getStoreUrl());
            }
        }
    }
}
