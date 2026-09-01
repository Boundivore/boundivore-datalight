package cn.boundivore.dl.api.worker.define;

import cn.boundivore.dl.base.response.impl.common.AbstractFileVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.WORKER_URL_PREFIX;

/**
 * Description: 文件操作接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/12/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Worker 接口：文件上传、下载相关", description = "IWorkerFileAPI")
@FeignClient(
        name = "IWorkerFileAPI",
        contextId = "IWorkerFileAPI",
        path = WORKER_URL_PREFIX
)
public interface IWorkerFileAPI {

//    @PostMapping(value = "/file/upload")
//    @Operation(summary = "上传文件接口 [Finished]", description = "上传文件接口")
//    Result<AbstractFileVo.FileUploadVo> upload(
//            @Parameter(name = "File", description = "待上传的文件")
//            @RequestParam(value = "File", required = true)
//            @NotNull
//            MultipartFile file,
//
//            @Parameter(name = "Path", description = "文件存储路径")
//            @RequestParam(value = "Path", required = true)
//            @NotNull
//            @Pattern(regexp = "^[a-zA-Z0-9/._-]+$")
//            String path
//    ) throws Exception;
//
//    @PostMapping(value = "/file/uploadBatch")
//    @Operation(summary = "上传文件接口 [批量] [Finished]", description = "上传文件接口 [批量]")
//    Result<AbstractFileVo.FileUploadVo> uploadBatch(
//            @Parameter(name = "FileArr", description = "待上传的文件")
//            @RequestParam(value = "FileArr", required = true)
//            @NotNull
//            MultipartFile[] fileArr,
//
//            @Parameter(name = "PathArr", description = "文件存储路径数组，与文件数组一一对应")
//            @RequestParam(value = "PathArr", required = true)
//            @NotNull
//            @Pattern(regexp = "^[a-zA-Z0-9/._-]+$")
//            String[] pathArr
//    ) throws Exception;

    @GetMapping(value = "/file/download")
    @Operation(summary = "下载文件接口 [Finished]", description = "下载文件接口")
    void download(
            @Parameter(name = "FilePathList", description = "待下载文件路径列表")
            @RequestParam(value = "FilePathList", required = true)
            @NotEmpty
            List<String> filePathList,

            HttpServletResponse response
    ) throws Exception;

}
