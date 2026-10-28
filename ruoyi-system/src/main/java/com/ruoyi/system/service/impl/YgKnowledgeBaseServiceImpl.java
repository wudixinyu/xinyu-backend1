package com.ruoyi.system.service.impl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.YgKnowledgeBase;
import com.ruoyi.system.kb.config.KbProperties;
import com.ruoyi.system.kb.store.KbEmbeddingStoreManager;
import com.ruoyi.system.mapper.YgKbDocumentMapper;
import com.ruoyi.system.mapper.YgKnowledgeBaseMapper;
import com.ruoyi.system.service.IYgKnowledgeBaseService;

/**
 * 知识库Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgKnowledgeBaseServiceImpl implements IYgKnowledgeBaseService
{
    private static final Logger log = LoggerFactory.getLogger(YgKnowledgeBaseServiceImpl.class);

    @Autowired
    private YgKnowledgeBaseMapper ygKnowledgeBaseMapper;

    @Autowired
    private YgKbDocumentMapper ygKbDocumentMapper;

    @Autowired
    private KbProperties props;

    @Autowired
    private KbEmbeddingStoreManager storeManager;

    @Override
    public YgKnowledgeBase selectYgKnowledgeBaseByKbId(Long kbId)
    {
        return ygKnowledgeBaseMapper.selectYgKnowledgeBaseByKbId(kbId);
    }

    @Override
    public List<YgKnowledgeBase> selectYgKnowledgeBaseList(YgKnowledgeBase ygKnowledgeBase)
    {
        return ygKnowledgeBaseMapper.selectYgKnowledgeBaseList(ygKnowledgeBase);
    }

    @Override
    public int insertYgKnowledgeBase(YgKnowledgeBase ygKnowledgeBase)
    {
        // 建库时固化当前 embedding 模型与维度，后续用于一致性校验
        ygKnowledgeBase.setEmbeddingModel(props.currentEmbeddingModel());
        ygKnowledgeBase.setEmbeddingDim(props.currentEmbeddingDimension());
        if (ygKnowledgeBase.getDocCount() == null)
        {
            ygKnowledgeBase.setDocCount(0);
        }
        if (ygKnowledgeBase.getChunkCount() == null)
        {
            ygKnowledgeBase.setChunkCount(0);
        }
        return ygKnowledgeBaseMapper.insertYgKnowledgeBase(ygKnowledgeBase);
    }

    @Override
    public int updateYgKnowledgeBase(YgKnowledgeBase ygKnowledgeBase)
    {
        return ygKnowledgeBaseMapper.updateYgKnowledgeBase(ygKnowledgeBase);
    }

    @Override
    @Transactional
    public int deleteYgKnowledgeBaseByKbIds(Long[] kbIds)
    {
        for (Long kbId : kbIds)
        {
            storeManager.delete(kbId);
            deleteUploadDir(kbId);
        }
        ygKbDocumentMapper.deleteByKbIds(kbIds);
        return ygKnowledgeBaseMapper.deleteYgKnowledgeBaseByKbIds(kbIds);
    }

    private void deleteUploadDir(Long kbId)
    {
        Path dir = props.uploadPath().resolve(String.valueOf(kbId));
        if (!Files.exists(dir))
        {
            return;
        }
        try
        {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException
                {
                    Files.delete(d);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e)
        {
            log.error("删除知识库[{}]上传目录失败：{}", kbId, e.getMessage());
        }
    }
}
