package org.dromara.generator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dromara.common.core.domain.R;
import org.dromara.generator.service.IFileService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 文件管理 Controller
 * 负责接收文件操作请求，调用 IFileService 处理业务逻辑
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/tool/file")
@Log4j2
public class FileController {

    private final IFileService fileService;

    @PostMapping("saveFile")
    public R<String> saveFile(@RequestBody List<Map<String, String>> list, @RequestParam Long tableId) throws IOException {
        if (list == null || list.isEmpty()) {
            return R.ok();
        }
        fileService.saveFiles(list, tableId);
        return R.ok();
    }

    @PostMapping("moveFile")
    public R<String> moveFile(@RequestBody List<Map<String, String>> list) throws IOException {
        if (list == null || list.isEmpty()) {
            return R.ok();
        }
        fileService.moveFiles(list);
        return R.ok();
    }

    @PostMapping("deleteFile")
    public R<String> deleteFile(@RequestBody List<String> list) throws IOException {
        if (list == null || list.isEmpty()) {
            return R.ok();
        }
        fileService.deleteFiles(list);
        return R.ok();
    }

    @GetMapping(value = "readFiles")
    public R<Collection<Map<String, String>>> readFiles(Long tableId) throws IOException {
        return R.ok(fileService.readProjectFiles(tableId));
    }
}
