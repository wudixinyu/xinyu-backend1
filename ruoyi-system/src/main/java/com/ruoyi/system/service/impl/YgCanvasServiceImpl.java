package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.YgCanvas;
import com.ruoyi.system.mapper.YgCanvasMapper;
import com.ruoyi.system.service.IYgCanvasService;

/**
 * 画布Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgCanvasServiceImpl implements IYgCanvasService 
{
    @Autowired
    private YgCanvasMapper ygCanvasMapper;

    /**
     * 查询画布
     * 
     * @param canvasId 画布主键
     * @return 画布
     */
    @Override
    public YgCanvas selectYgCanvasByCanvasId(Long canvasId)
    {
        return ygCanvasMapper.selectYgCanvasByCanvasId(canvasId);
    }

    /**
     * 查询画布列表
     * 
     * @param ygCanvas 画布
     * @return 画布
     */
    @Override
    public List<YgCanvas> selectYgCanvasList(YgCanvas ygCanvas)
    {
        return ygCanvasMapper.selectYgCanvasList(ygCanvas);
    }

    /**
     * 新增画布
     * 
     * @param ygCanvas 画布
     * @return 结果
     */
    @Override
    public int insertYgCanvas(YgCanvas ygCanvas)
    {
        ygCanvas.setCreateTime(DateUtils.getNowDate());
        return ygCanvasMapper.insertYgCanvas(ygCanvas);
    }

    /**
     * 修改画布
     * 
     * @param ygCanvas 画布
     * @return 结果
     */
    @Override
    public int updateYgCanvas(YgCanvas ygCanvas)
    {
        ygCanvas.setUpdateTime(DateUtils.getNowDate());
        return ygCanvasMapper.updateYgCanvas(ygCanvas);
    }

    /**
     * 批量删除画布
     * 
     * @param canvasIds 需要删除的画布主键
     * @return 结果
     */
    @Override
    public int deleteYgCanvasByCanvasIds(Long[] canvasIds)
    {
        return ygCanvasMapper.deleteYgCanvasByCanvasIds(canvasIds);
    }

    /**
     * 删除画布信息
     * 
     * @param canvasId 画布主键
     * @return 结果
     */
    @Override
    public int deleteYgCanvasByCanvasId(Long canvasId)
    {
        return ygCanvasMapper.deleteYgCanvasByCanvasId(canvasId);
    }
}
