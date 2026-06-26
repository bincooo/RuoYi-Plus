package org.dromara.generator.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 文件管理服务层接口
 */
public interface IFileService {

    /**
     * 批量保存文件
     *
     * @param fileList 文件列表，每个 Map 包含 filename 和 code
     * @param tableId  表编号
     * @throws IOException IO异常
     */
    void saveFiles(List<Map<String, String>> fileList, Long tableId) throws IOException;

    /**
     * 批量移动文件
     *
     * @param moveList 移动列表，每个 Map 包含 oldFilename 和 newFilename
     * @throws IOException IO异常
     */
    void moveFiles(List<Map<String, String>> moveList) throws IOException;

    /**
     * 批量删除文件
     *
     * @param fileNameList 文件名列表
     * @throws IOException IO异常
     */
    void deleteFiles(List<String> fileNameList) throws IOException;

    /**
     * 读取项目所有文件内容（含模板文件生成）
     *
     * @param tableId 表编号
     * @return 文件名与内容的映射集合
     * @throws IOException IO异常
     */
    Collection<Map<String, String>> readProjectFiles(Long tableId) throws IOException;
}
