package org.dromara.generator.controller;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.text.CaseUtils;
import org.dromara.common.core.domain.R;
import org.dromara.generator.domain.GenTable;
import org.dromara.generator.service.IGenTableService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tool/file")
@Log4j2
public class FileController {

    @Value("${nodejs.baseDir}")
    private String baseDir;
    @Value("${nodejs.filter}")
    private List<String> fileFilter;

    final private ResourcePatternResolver resolver;

    private final IGenTableService genTableService;

    @PostMapping("saveFile")
    public R<String> saveFile(@RequestBody List<Map<String, String>> list, @RequestParam Long tableId) throws IOException {
        if (list == null || list.isEmpty()) {
            return R.ok();
        }

        GenTable genTable = genTableService.selectGenTableById(tableId);
        List<String> newFileFilter = Lists.newArrayList(fileFilter);
        if (genTable != null) {
            newFileFilter.add(String.format("/src/pages/%s/%s/", genTable.getModuleName(), genTable.getBusinessName()));
        }

        for (Map<String, String> params : list) {
            String filename = params.get("filename");
            String code = params.get("code");

            Path path = Path.of(baseDir, filename);
            File file = path.toFile();

            String name = file.getName();
            if (name.endsWith(".ico")
                || name.endsWith(".jpg")
                || name.endsWith(".png")
                || name.endsWith(".gif")
                ||  name.endsWith(".jpeg")
            ) {
                continue;
            }

            if (notMatchFilter(newFileFilter, file.getAbsolutePath())) {
                continue;
            }

            Files.createDirectories(path.getParent());
            Files.writeString(path, code,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        }

        return R.ok();
    }

    @PostMapping("moveFile")
    public R<String> moveFile(@RequestBody List<Map<String, String>> list) throws IOException {
        if (list == null || list.isEmpty()) {
            return R.ok();
        }

        for (Map<String, String> params : list) {
            String oldFilename = params.get("oldFilename");
            String newFilename = params.get("newFilename");

            if (Files.exists(Path.of(baseDir, oldFilename))) {
                Path newFile = Path.of(baseDir, newFilename);
                Files.createDirectories(newFile.getParent());
                Files.move(Path.of(baseDir, newFilename), newFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        return R.ok();
    }

    @PostMapping("deleteFile")
    public R<String> deleteFile(@RequestBody List<String> list) throws IOException {
        if (list == null || list.isEmpty()) {
            return R.ok();
        }

        for (String filename : list) {
            Files.deleteIfExists(Path.of(baseDir, filename));
        }

        return R.ok();
    }

    @GetMapping(value = "readFiles")
    @SuppressWarnings("unchecked")
    public R<Collection<Map<String, String>>> readFiles(Long tableId) throws IOException {
        Map<String, Map<String, String>> mapFiles = new HashMap<>();
        Resource[] resources = resolver.getResources("classpath:nodejs/**");
        for (Resource resource : resources) {
            if (!resource.isReadable() || resource.getFilename() == null) {
                continue;
            }
            String path = resource.getURL().toString();
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            mapFiles.put(path.split("/nodejs")[1], Map.of("filename", path.split("/nodejs")[1], "code", content));
        }

        // 添加模板入口
        String code = """
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
        mapFiles.put("/src/index.tsx", Map.of("filename", "/src/index.tsx", "code", code));
        GenTable genTable = genTableService.selectGenTableById(tableId);
        readFilesRecursively(mapFiles, new File(baseDir), (String path) ->
            notMatchFilter(
                Stream.concat(fileFilter.stream(), Stream.of(
                    String.format("/src/pages/%s/%s/", genTable.getModuleName(), genTable.getBusinessName())
                )).toList(), path
            ));
        Map<String, Object> options = genTable.getOptions();
        if (options.containsKey("pages")) {
            Map<String, Object> pages = (Map<String, Object>) options.get("pages");
            transformName(pages);
            List<String> imports = pages.values().stream()
                .map(obj -> {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    return String.format("import %s from \"./%s\";", map.get("name"), map.get("component"));
                })
                .toList();
            List<String> routes = pages.entrySet().stream()
                .map(obj -> {
                    Map<String, Object> map = (Map<String, Object>) obj.getValue();
                    return String.format("  { path: \"%s\", Component: %s },", obj.getKey(), map.get("name"));
                }).toList();
            code = """
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
            mapFiles.put("/src/routes.tsx", Map.of("filename", "/src/routes.tsx", "code", code));
        }
        return R.ok(mapFiles.values());
    }

    private void transformName(Map<String, Object> pages) {
        pages.values().forEach(map -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> obj = (Map<String, Object>) map;
            String component = (String) obj.get("component");
            String[] split = component.split("/");
            String name = split[split.length-1];
            if ("index".equals(name) || "index.tsx".equals(name) || "index.ts".equals(name)) {
                name = split[split.length-2];
            }
            obj.put("name", CaseUtils.toCamelCase(name, true, '_'));
        });
    }

    private void readFilesRecursively(Map<String, Map<String, String>> mapFiles, File directory, Function<String, Boolean> filter) throws IOException {
        if (directory.exists() && directory.isDirectory()) {
            String dirName = directory.getName();
            if ("node_modules".equals(dirName) || ".git".equals(dirName) || ".vscode".equals(dirName)) {
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
                if (filter.apply(absolutePath)) {
                    continue;
                }

                log.debug("read file: {}", absolutePath);
                String name = file.getName();
                String filename = file.getAbsolutePath().substring(baseDir.length());
                if (name.endsWith(".ico")
                    || name.endsWith(".jpg")
                    || name.endsWith(".png")
                    || name.endsWith(".gif")
                    ||  name.endsWith(".jpeg")
                ) {
                    mapFiles.put(filename, Map.of("filename", filename, "code", ""));
                    continue;
                }
                mapFiles.put(filename, Map.of("filename", filename, "code", Files.readString(file.toPath())));
            }
        }
    }

    protected boolean notMatchFilter(List<String> nodeFilter, String absolutePath) {
        if (nodeFilter == null) {
            return true;
        }

        for (String filter : nodeFilter) {
            if (absolutePath.contains(filter)) {
                return false;
            }
        }

        if (absolutePath.contains("/cloudflare-worker/")) {
            return true;
        }

        return !absolutePath.equals(baseDir + "/tango.config.json")
            && !absolutePath.equals(baseDir + "/package.json")
            && !absolutePath.equals(baseDir + "/tsconfig.json")
            ;
    }

}
