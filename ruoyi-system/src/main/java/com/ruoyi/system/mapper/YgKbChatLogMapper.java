package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgKbChatLog;

/**
 * 知识库问答日志Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgKbChatLogMapper
{
    /**
     * 新增问答日志
     */
    public int insertYgKbChatLog(YgKbChatLog ygKbChatLog);

    /**
     * 查询问答日志列表
     */
    public List<YgKbChatLog> selectYgKbChatLogList(YgKbChatLog ygKbChatLog);
}
