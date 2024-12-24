package cn.boundivore.dl.api.worker.define;

import cn.boundivore.dl.base.response.impl.common.AbstractFileVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
@Api(value = "IWorkerFileAPI", tags = {"Worker 接口：文件上传、下载相关"})
@FeignClient(
        name = "IWorkerFileAPI",
        contextId = "IWorkerFileAPI",
        path = WORKER_URL_PREFIX
)
public interface IWorkerFileAPI {

//    @PostMapping(value = "/file/upload")
//    @ApiOperation(notes = "上传文件接口", value = "上传文件接口 [Finished]")
//    Result<AbstractFileVo.FileUploadVo> upload(
//            @ApiParam(name = "File", value = "待上传的文件")
//            @RequestParam(value = "File", required = true)
//            @NotNull
//            MultipartFile file,
//
//            @ApiParam(name = "Path", value = "文件存储路径")
//            @RequestParam(value = "Path", required = true)
//            @NotNull
//            @Pattern(regexp = "^[a-zA-Z0-9/._-]+$")
//            String path
//    ) throws Exception;
//
//    @PostMapping(value = "/file/uploadBatch")
//    @ApiOperation(notes = "上传文件接口 [批量]", value = "上传文件接口 [批量] [Finished]")
//    Result<AbstractFileVo.FileUploadVo> uploadBatch(
//            @ApiParam(name = "FileArr", value = "待上传的文件")
//            @RequestParam(value = "FileArr", required = true)
//            @NotNull
//            MultipartFile[] fileArr,
//
//            @ApiParam(name = "PathArr", value = "文件存储路径数组，与文件数组一一对应")
//            @RequestParam(value = "PathArr", required = true)
//            @NotNull
//            @Pattern(regexp = "^[a-zA-Z0-9/._-]+$")
//            String[] pathArr
//    ) throws Exception;

    @GetMapping(value = "/file/download")
    @ApiOperation(notes = "下载文件接口", value = "下载文件接口 [Finished]")
    void download(
            @ApiParam(name = "FilePathList", value = "待下载文件路径列表")
            @RequestParam(value = "FilePathList", required = true)
            @NotEmpty
            List<String> filePathList,

            HttpServletResponse response
    ) throws Exception;

}
