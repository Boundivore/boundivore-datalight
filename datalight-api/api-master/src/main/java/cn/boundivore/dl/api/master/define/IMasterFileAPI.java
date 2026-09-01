package cn.boundivore.dl.api.master.define;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;

/**
 * Description: 文件操作接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：文件上传、下载相关", description = "IMasterFileAPI")
@FeignClient(
        name = "IMasterFileAPI",
        contextId = "IMasterFileAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterFileAPI {

//    @PostMapping(value = "/file/upload")
//    @Operation(summary = "上传文件接口 [Finished]", description = "上传文件接口")
//    Result<AbstractFileVo.FileUploadVo> upload(
//            @Parameter(name = "ClusterId", description = "集群 ID")
//            @RequestParam(value = "ClusterId", required = true)
//            @NotNull
//            Long clusterId,
//
//            @Parameter(name = "NodeId", description = "节点 ID")
//            @RequestParam(value = "NodeId", required = true)
//            @NotNull
//            Long nodeId,
//
//            @Parameter(name = "Path", description = "文件存储路径")
//            @RequestParam(value = "Path", required = true)
//            @NotNull
//            @Pattern(regexp = "^[a-zA-Z0-9/._-]+$")
//            String path,
//
//            @Parameter(name = "File", description = "待上传的文件")
//            @RequestParam(value = "File", required = true)
//            @NotNull
//            MultipartFile file
//    ) throws Exception;
//
//    @PostMapping(value = "/file/uploadBatch")
//    @Operation(summary = "上传文件接口 [批量] [Finished]", description = "上传文件接口 [批量]")
//    Result<AbstractFileVo.FileUploadVo> uploadBatch(
//            @Parameter(name = "ClusterId", description = "集群 ID")
//            @RequestParam(value = "ClusterId", required = true)
//            @NotNull
//            Long clusterId,
//
//            @Parameter(name = "NodeId", description = "节点 ID")
//            @RequestParam(value = "NodeId", required = true)
//            @NotNull
//            Long nodeId,
//
//            @Parameter(name = "PathArr", description = "文件存储路径数组，与文件数组一一对应")
//            @RequestParam(value = "PathArr", required = true)
//            @NotNull
//            @Pattern(regexp = "^[a-zA-Z0-9/._-]+$")
//            String[] pathArr,
//
//            @Parameter(name = "FileArr", description = "待上传的文件")
//            @RequestParam(value = "FileArr", required = true)
//            @NotNull
//            MultipartFile[] fileArr
//    ) throws Exception;

    @GetMapping(value = "/file/downloadConfig")
    @Operation(summary = "下载配置文件 [Finished]", description = "下载配置文件")
    void downloadConfig(
            @Parameter(name = "ClusterId", description = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            @NotNull
            Long clusterId,

            @Parameter(name = "NodeId", description = "节点 ID")
            @RequestParam(value = "NodeId", required = true)
            @NotNull
            Long nodeId,

            @Parameter(name = "ServiceName", description = "服务名称")
            @RequestParam(value = "ServiceName", required = true)
            @NotNull
            String serviceName,

            @Parameter(name = "FilePathArr", description = "待下载文件路径列表(多个路径使用英文逗号分割)")
            @RequestParam(value = "FilePathArr", required = true)
            @NotEmpty
            String filePathArr,

            HttpServletResponse response
    ) throws Exception;

}
