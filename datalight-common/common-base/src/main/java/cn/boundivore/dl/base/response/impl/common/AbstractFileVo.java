/**
 * Copyright (C) <2023> <Boundivore> <boundivore@foxmail.com>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Apache License, Version 2.0
 * as published by the Apache Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License, Version 2.0 for more details.
 * <p>
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program; if not, you can obtain a copy at
 * http://www.apache.org/licenses/LICENSE-2.0.
 */
package cn.boundivore.dl.base.response.impl.common;

import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: DataLight 充分发文件相关响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/12/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractFileVo {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractFileVo.FileUploadVo",
            description = "AbstractFileVo.FileUploadVo 上传文件响应体"
    )
    public static class FileUploadVo implements IVo {

        private static final long serialVersionUID = 4258355989656536771L;

        @ApiModelProperty(name = "UploadFileList", value = "上传成功的文件列表", required = true)
        @JsonProperty(value = "UploadFileList", required = true)
        private List<SingleUploadFileVo> uploadFileList;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractFileVo.SingleUploadFileVo",
            description = "AbstractFileVo.SingleUploadFileVo 单个上传文件响应体"
    )
    public static class SingleUploadFileVo implements IVo {

        private static final long serialVersionUID = -3025955230836468689L;

        @ApiModelProperty(name = "FileName", value = "文件名", required = true)
        @JsonProperty(value = "FileName", required = true)
        private String fileName;

        @ApiModelProperty(name = "FilePath", value = "文件路径", required = true)
        @JsonProperty(value = "FilePath", required = true)
        private String filePath;

        @ApiModelProperty(name = "FileNameSuffix", value = "文件扩展名", required = true)
        @JsonProperty(value = "FileNameSuffix", required = true)
        private String fileNameSuffix;

    }


    public static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<String, String>() {
        private static final long serialVersionUID = -6825201007237261724L;

        {
            put("", "");
            put(".aac", "audio/aac");
            put(".abw", "application/x-abiword");
            put(".arc", "application/x-freearc");
            put(".avif", "image/avif");
            put(".avi", "video/x-msvideo");
            put(".azw", "application/vnd.amazon.ebook");
            put(".bin", "application/octet-stream");
            put(".bmp", "image/bmp");
            put(".bz", "application/x-bzip");
            put(".bz2", "application/x-bzip2");
            put(".cda", "application/x-cdf");
            put(".csh", "application/x-csh");
            put(".css", "text/css");
            put(".csv", "text/csv");
            put(".doc", "application/msword");
            put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            put(".eot", "application/vnd.ms-fontobject");
            put(".epub", "application/epub+zip");
            put(".gz", "application/gzip");
            put(".gif", "image/gif");
            put(".htm, .html", "text/html");
            put(".ico", "image/vnd.microsoft.icon");
            put(".ics", "text/calendar");
            put(".jar", "application/java-archive");
            put(".jpeg", "image/jpeg");
            put(".jpg", "image/jpeg");
            put(".js", "text/javascript (Specifications: HTML and RFC 9239)");
            put(".json", "application/json");
            put(".jsonld", "application/ld+json");
            put(".mid", "audio/midi, audio/x-midi");
            put(".midi", "audio/midi, audio/x-midi");
            put(".mjs", "text/javascript");
            put(".mp3", "audio/mpeg");
            put(".mp4", "video/mp4");
            put(".mpeg", "video/mpeg");
            put(".mpkg", "application/vnd.apple.installer+xml");
            put(".odp", "application/vnd.oasis.opendocument.presentation");
            put(".ods", "application/vnd.oasis.opendocument.spreadsheet");
            put(".odt", "application/vnd.oasis.opendocument.text");
            put(".oga", "audio/ogg");
            put(".ogv", "video/ogg");
            put(".ogx", "application/ogg");
            put(".opus", "audio/opus");
            put(".otf", "font/otf");
            put(".png", "image/png");
            put(".pdf", "application/pdf");
            put(".php", "application/x-httpd-php");
            put(".ppt", "application/vnd.ms-powerpoint");
            put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            put(".rar", "application/vnd.rar");
            put(".rtf", "application/rtf");
            put(".sh", "application/x-sh");
            put(".svg", "image/svg+xml");
            put(".tar", "application/x-tar");
            put(".tif", "image/tiff");
            put(".tiff", "image/tiff");
            put(".ts", "video/mp2t");
            put(".ttf", "font/ttf");
            put(".txt", "text/plain");
            put(".vsd", "application/vnd.visio");
            put(".wav", "audio/wav");
            put(".weba", "audio/webm");
            put(".webm", "video/webm");
            put(".webp", "image/webp");
            put(".woff", "font/woff");
            put(".woff2", "font/woff2");
            put(".xhtml", "application/xhtml+xml");
            put(".xls", "application/vnd.ms-excel");
            put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            put(".xml", "application/xml");
            put(".xul", "application/vnd.mozilla.xul+xml");
            put(".zip", "application/zip");
            put(".3gp", "video/3gpp");
            put(".3g2", "video/3gpp2");
            put(".7z", "application/x-7z-compressed");
        }
    };

}
