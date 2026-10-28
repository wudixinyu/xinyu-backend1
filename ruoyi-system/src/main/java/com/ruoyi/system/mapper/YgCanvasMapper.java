package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgCanvas;

/**
 * 画布Mapper接口
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgCanvasMapper 
{
    /**
     * 查询画布
     * 
     * @param canvasId 画布主键
     * @return 画布
     */
    public YgCanvas selectYgCanvasByCanvasId(Long canvasId);

    /**
     * 查询画布列表（不含 content 大字段）
     * 
     * @param ygCanvas 画布
     * @return 画布集合
     */
    public List<YgCanvas> selectYgCanvasList(YgCanvas ygCanvas);

    /**
     * 新增画布
     * 
     * @param ygCanvas 画布
     * @return 结果
     */
    public int insertYgCanvas(YgCanvas ygCanvas);

    /**
     * 修改画布
     * 
     * @param ygCanvas 画布
     * @return 结果
     */
    public int updateYgCanvas(YgCanvas ygCanvas);

    /**
     * 删除画布
     * 
     * @param canvasId 画布主键
     * @return 结果
     */
    public int deleteYgCanvasByCanvasId(Long canvasId);

    /**
     * 批量删除画布
     * 
     * @param canvasIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteYgCanvasByCanvasIds(Long[] canvasIds);
}
