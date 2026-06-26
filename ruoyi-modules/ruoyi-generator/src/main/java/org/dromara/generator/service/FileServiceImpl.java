package org.dromara.generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.dromara.generator.domain.GenTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;

/**
 * 文件管理服务层实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements IFileService {

    // ==================== 常量 ====================
    private static final List<String> IMAGE_EXTENSIONS = List.of(".ico", ".jpg", ".png", ".gif", ".jpeg");
    private static final List<String> SKIP_DIRS = List.of("node_modules", ".git", ".vscode");

    private static final String INDEX_TSX_FILENAME = "/src/index.tsx";
    private static final String ROUTES_TSX_FILENAME = "/src/routes.tsx";

    // ==================== 注入 ====================
    @Value("${nodejs.baseDir}")
    private String baseDir;

    @Value("${nodejs.filter}")
    private List<String> fileFilter;

    private final ResourcePatternResolver resourcePatternResolver;
    private final IGenTableService genTableService;

    @Override
    public void saveFiles(List<Map<String, String>> fileList, Long tableId) throws IOException {
        GenTable genTable = genTableService.selectGenTableById(tableId);
        List<String> effectiveFilters = buildEffectiveFilters(genTable);

        for (Map<String, String> fileMap : fileList) {
            String filename = fileMap.get("filename");
            String code = fileMap.get("code");

            Path path = Path.of(baseDir, filename);
            File file = path.toFile();

            if (isImageFile(file.getName())) {
                continue;
            }

            if (isExcludedByFilter(effectiveFilters, file.getAbsolutePath())) {
                continue;
            }

            Files.createDirectories(path.getParent());
            Files.writeString(path, code,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    @Override
    public void moveFiles(List<Map<String, String>> moveList) throws IOException {
        for (Map<String, String> moveMap : moveList) {
            String oldFilename = moveMap.get("oldFilename");
            String newFilename = moveMap.get("newFilename");

            Path oldPath = Path.of(baseDir, oldFilename);
            if (!Files.exists(oldPath)) {
                continue;
            }

            Path newPath = Path.of(baseDir, newFilename);
            Files.createDirectories(newPath.getParent());
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void deleteFiles(List<String> fileNameList) throws IOException {
        for (String filename : fileNameList) {
            Files.deleteIfExists(Path.of(baseDir, filename));
        }
    }

    @Override
    public Collection<Map<String, String>> readProjectFiles(Long tableId) throws IOException {
        Map<String, Map<String, String>> mapFiles = new HashMap<>();

        // 1. 加载 classpath 模板文件
        loadTemplateFiles(mapFiles);

        // 2. 添加 index.tsx 模板入口
        mapFiles.put(INDEX_TSX_FILENAME,
            Map.of("filename", INDEX_TSX_FILENAME, "code", buildIndexTsx()));

        // 3. 递归读取本地文件
        GenTable genTable = genTableService.selectGenTableById(tableId);
        List<String> effectiveFilters = buildEffectiveFilters(genTable);
        readFilesRecursively(mapFiles, new File(baseDir),
            path -> isExcludedByFilter(effectiveFilters, path));

        // 4. 生成 routes.tsx
        Map<String, Object> options = genTable.getOptions();
        if (options != null && options.containsKey("pages")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pages = (Map<String, Object>) options.get("pages");
            String routesCode = buildRoutesTsx(pages);
            mapFiles.put(ROUTES_TSX_FILENAME,
                Map.of("filename", ROUTES_TSX_FILENAME, "code", routesCode));
        }

        return mapFiles.values();
    }

    // ==================== 私有辅助方法 ====================

    private List<String> buildEffectiveFilters(GenTable genTable) {
        List<String> effectiveFilters = new ArrayList<>(fileFilter);
        if (genTable != null) {
            effectiveFilters.add(String.format("/src/pages/%s/%s/", genTable.getModuleName(), genTable.getBusinessName()));
        }
        return effectiveFilters;
    }

    private boolean isImageFile(String fileName) {
        String lowerName = fileName.toLowerCase();
        return IMAGE_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
    }

    private boolean isExcludedByFilter(List<String> filters, String absolutePath) {
        if (filters == null) {
            return true;
        }

        for (String filter : filters) {
            if (absolutePath.contains(filter)) {
                return false;
            }
        }

        // 黑名单路径
        if (absolutePath.contains("/cloudflare-worker/")) {
            return true;
        }

        return !absolutePath.equals(baseDir + "/tango.config.json")
            && !absolutePath.equals(baseDir + "/package.json")
            && !absolutePath.equals(baseDir + "/tsconfig.json");
    }

    private void loadTemplateFiles(Map<String, Map<String, String>> mapFiles) throws IOException {
        Resource[] resources = resourcePatternResolver.getResources("classpath:nodejs/**");
        for (Resource resource : resources) {
            if (!resource.isReadable() || resource.getFilename() == null) {
                continue;
            }
            String urlPath = resource.getURL().toString();
            String filename = urlPath.split("/nodejs")[1];
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            mapFiles.put(filename, Map.of("filename", filename, "code", content));
        }
    }

    private void readFilesRecursively(Map<String, Map<String, String>> mapFiles,
                                       File directory,
                                       Function<String, Boolean> filter) throws IOException {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        if (SKIP_DIRS.contains(directory.getName())) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                readFilesRecursively(mapFiles, file, filter);
                continue;
            }

            if (!file.isFile()) {
                continue;
            }

            String absolutePath = file.getAbsolutePath();
            if (Boolean.TRUE.equals(filter.apply(absolutePath))) {
                continue;
            }

            log.debug("read file: {}", absolutePath);
            String filename = absolutePath.substring(baseDir.length());
            String fileName = file.getName();

            if (isImageFile(fileName)) {
                mapFiles.put(filename, Map.of("filename", filename, "code", ""));
                continue;
            }

            mapFiles.put(filename, Map.of("filename", filename, "code", Files.readString(file.toPath())));
        }
    }

    // ==================== 模板构建方法 ====================

    private String buildIndexTsx() {
        return """
            import React, { useEffect } from "react";
            import ReactDOM from "react-dom/client";
            import { BrowserRouter, useRoutes } from "react-router-dom";
            import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

            /* eslint-disable import/no-mutable-exports */
            import type { MessageInstance } from "antd/es/message/interface";
            import type { ModalStaticFunctions } from "antd/es/modal/confirm";
            import type { NotificationInstance } from "antd/es/notification/interface";

            import { ProConfigProvider } from "@ant-design/pro-components";
            import globalValueTypeMap from "#src/components/pro-fields";

            import {
              message as antdMessage,
              Modal as antdModal,
              notification as antdNotification,
            } from "antd";
            import "antd/dist/reset.css";

            import routes from "./routes";
            import "./reject";
            import "./box.css";

            const rootElement = document.getElementById("root")!;
            const root = ReactDOM.createRoot(rootElement);
            const Routes = () => {
              // 监听设计模式
              useEffect(() => {
                const interval = setInterval(() => {
                  const preview = window.__TANGO_CONFIG__?.preview || false;
                  if (preview) {
                    document.body.classList.remove('designer');
                  } else {
                    document.body.classList.add('designer');
                  }
                }, 500);
              }, []);
              return useRoutes(routes);
            };

            let message: MessageInstance = antdMessage;
            let notification: NotificationInstance = antdNotification;

            const { ...resetFns } = antdModal;
            let modal: Omit<ModalStaticFunctions, "warn"> = resetFns;

            window.$message = message;
            window.$modal = modal;
            window.$notification = notification;

            root.render(
              <React.StrictMode>
                <QueryClientProvider client={new QueryClient()}>
                  <BrowserRouter>
                    <ProConfigProvider valueTypeMap={globalValueTypeMap}>
                        <Routes />
                    </ProConfigProvider>
                  </BrowserRouter>
                </QueryClientProvider>
              </React.StrictMode>
            );
            """;
    }

    private String buildRoutesTsx(Map<String, Object> pages) {
        transformPageNames(pages);

        List<String> imports = pages.values().stream()
            .map(obj -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                return String.format("import %s from \"./%s\";", map.get("name"), map.get("component"));
            })
            .toList();

        List<String> routes = pages.entrySet().stream()
            .map(obj -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj.getValue();
                return String.format("  { path: \"%s\", Component: %s },", obj.getKey(), map.get("name"));
            }).toList();

        String code = """
            import { RouteObject } from "react-router-dom";
            import NotFound from "./not-found";
            import Loading from "./loading";
            [imports]

            const routes: RouteObject[] = [
                [routes]
                { path: "/login", Component: NotFound },
                { path: "/__background_loading_page__", Component: Loading },
            ];

            export default routes;
            """;

        code = code.replace("[imports]", String.join("\n", imports));
        code = code.replace("[routes]", String.join("\n", routes));
        return code;
    }

    private void transformPageNames(Map<String, Object> pages) {
        pages.values().forEach(obj -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> pageMap = (Map<String, Object>) obj;
            String component = (String) pageMap.get("component");
            String[] split = component.split("/");
            String name = split[split.length - 1];
            // 将文件名转为大驼峰命名
            pageMap.put("name", CaseUtils.toCamelCase(name, true));
        });
    }
}
