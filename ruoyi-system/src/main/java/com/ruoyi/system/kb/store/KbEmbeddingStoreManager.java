package com.ruoyi.system.kb.store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.kb.config.KbProperties;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * 知识库向量存储管理器
 *
 * <p>每个知识库对应一个 InMemoryEmbeddingStore 实例，持久化为 {store-dir}/{kbId}.json。
 * 写入采用 temp 文件 + 原子移动，防止半写损坏；按 kbId 串行 persist。</p>
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Component
public class KbEmbeddingStoreManager
{
    private static final Logger log = LoggerFactory.getLogger(KbEmbeddingStoreManager.class);

    @Autowired
    private KbProperties props;

    private final ConcurrentHashMap<Long, InMemoryEmbeddingStore<TextSegment>> cache = new ConcurrentHashMap<>();

    /**
     * 获取知识库的向量存储（懒加载，优先从持久化文件恢复）
     */
    public InMemoryEmbeddingStore<TextSegment> getStore(Long kbId)
    {
        return cache.computeIfAbsent(kbId, id -> {
            Path file = storeFile(id);
            if (Files.exists(file))
            {
                try
                {
                    return InMemoryEmbeddingStore.fromFile(file);
                }
                catch (Exception e)
                {
                    log.error("知识库[{}]向量文件加载失败，将重建空存储：{}", id, e.getMessage());
                }
            }
            return new InMemoryEmbeddingStore<>();
        });
    }

    /**
     * 持久化指定知识库的向量存储（原子写）
     */
    public void persist(Long kbId)
    {
        InMemoryEmbeddingStore<TextSegment> store = cache.get(kbId);
        if (store == null)
        {
            return;
        }
        synchronized (this)
        {
            try
            {
                Path dir = props.storePath();
                Files.createDirectories(dir);
                Path temp = dir.resolve(kbId + ".tmp");
                Path target = storeFile(kbId);
                store.serializeToFile(temp);
                try
                {
                    Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                }
                catch (IOException e)
                {
                    Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch (Exception e)
            {
                throw new ServiceException("持久化向量索引失败：" + e.getMessage());
            }
        }
    }

    /**
     * 删除向量存储（缓存 + 文件）
     */
    public void delete(Long kbId)
    {
        cache.remove(kbId);
        try
        {
            Files.deleteIfExists(storeFile(kbId));
        }
        catch (IOException e)
        {
            log.error("删除知识库[{}]向量文件失败：{}", kbId, e.getMessage());
        }
    }

    /**
     * 判断向量文件是否存在
     */
    public boolean storeExists(Long kbId)
    {
        return Files.exists(storeFile(kbId));
    }

    private Path storeFile(Long kbId)
    {
        return props.storePath().resolve(kbId + ".json");
    }
}
