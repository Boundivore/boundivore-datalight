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
package cn.boundivore.dl.base.response.impl.master;

import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description: 告警处理方式相关响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAlertHandlerVo {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertHandlerVo.AlertHandlerInterfaceListVo",
            description = "AbstractAlertHandlerVo.AlertHandlerInterfaceListVo 告警接口处理方式列表响应体"
    )
    public static class AlertHandlerInterfaceListVo implements IVo {

        private static final long serialVersionUID = -1025953221547317564L;

        @Schema(name = "AlertHandlerInterfaceList", title = "告警接口处理方式列表", required = true)
        @JsonProperty(value = "AlertHandlerInterfaceList", required = true)
        private List<AlertHandlerInterfaceVo> alertHandlerInterfaceList;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertHandlerVo.AlertHandlerMailListVo",
            description = "AbstractAlertHandlerVo.AlertHandlerMailListVo 告警邮件处理方式列表响应体"
    )
    public static class AlertHandlerMailListVo implements IVo {

        private static final long serialVersionUID = -1025953221547317564L;

        @Schema(name = "AlertHandlerMailList", title = "告警接口处理方式列表", required = true)
        @JsonProperty(value = "AlertHandlerMailList", required = true)
        private List<AlertHandlerMailVo> alertHandlerMailList;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertHandlerVo.AlertHandlerInterfaceVo",
            description = "AbstractAlertHandlerVo.AlertHandlerInterfaceVo 告警接口处理方式响应体"
    )
    public static class AlertHandlerInterfaceVo implements IVo {

        private static final long serialVersionUID = -1025953221547317564L;

        @Schema(name = "HandlerId", title = "告警处理方式 ID", required = true)
        @JsonProperty(value = "HandlerId", required = true)
        private Long handlerId;

        @Schema(name = "InterfaceUri", title = "外部接收告警接口 URI", required = true)
        @JsonProperty(value = "InterfaceUri", required = true)
        private String interfaceUri;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertHandlerVo.AlertHandlerMailVo",
            description = "AbstractAlertHandlerVo.AlertHandlerMailVo 告警邮件处理方式响应体"
    )
    public static class AlertHandlerMailVo implements IVo {

        private static final long serialVersionUID = -1025953221547317564L;

        @Schema(name = "HandlerId", title = "告警处理方式 ID", required = true)
        @JsonProperty(value = "HandlerId", required = true)
        private Long handlerId;

        @Schema(name = "MailAccount", title = "邮箱地址", required = true)
        @JsonProperty(value = "MailAccount", required = true)
        private String mailAccount;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertHandlerVo.AlertHandlerListVo",
            description = "AbstractAlertHandlerVo.AlertHandlerListVo 告警处理方式列表响应体"
    )
    public static class AlertHandlerListVo implements IVo {

        private static final long serialVersionUID = 4281271065008578185L;

        @Schema(name = "AlertId", title = "告警 ID", required = true)
        @JsonProperty(value = "AlertId", required = true)
        private Long alertId;

        @Schema(name = "AlertName", title = "告警名称", required = true)
        @JsonProperty(value = "AlertName", required = true)
        private String alertName;

        @Schema(name = "AlertHandlerList", title = "告警处理方式列表", required = true)
        @JsonProperty(value = "AlertHandlerList", required = true)
        private List<AbstractAlertHandlerVo.AlertHandlerVo> alertHandlerList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertHandlerVo.AlertAndHandlerVo",
            description = "AbstractAlertHandlerVo.AlertAndHandlerVo 告警处理方式实体响应体"
    )
    public static class AlertHandlerVo implements IVo {

        private static final long serialVersionUID = 436326579514859506L;

        @Schema(name = "AlertHandlerType", title = "告警处理类型", required = true)
        @JsonProperty(value = "AlertHandlerType", required = true)
        private AlertHandlerTypeEnum alertHandlerTypeEnum;

        @Schema(name = "AlertHandlerIdList", title = "告警处理方式 ID 列表", required = true)
        @JsonProperty(value = "AlertHandlerIdList", required = true)
        private List<Long> alertHandlerIdList;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertHandlerVo.HandlerAndAlertIdListVo",
            description = "AbstractAlertHandlerVo.HandlerAndAlertIdListVo 告警处理方式关联的告警信息列表响应体"
    )
    public static class HandlerAndAlertIdListVo implements IVo {

        private static final long serialVersionUID = 4172118776500423264L;

        @Schema(name = "HandlerId", title = "告警处理方式 ID", required = true)
        @JsonProperty(value = "HandlerId", required = true)
        private Long handlerId;

        @Schema(name = "AlertIdAndTypeList", title = "告警与处理方式列表", required = true)
        @JsonProperty(value = "AlertIdAndTypeList", required = true)
        private List<AlertIdAndTypeVo> alertIdAndTypeList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertHandlerVo.AlertIdAndTypeVo",
            description = "AbstractAlertHandlerVo.AlertIdAndTypeVo 告警信息实体响应体"
    )
    public static class AlertIdAndTypeVo implements IVo {

        private static final long serialVersionUID = -2087590179140631111L;

        @Schema(name = "AlertHandlerType", title = "告警处理类型", required = true)
        @JsonProperty(value = "AlertHandlerType", required = true)
        private AlertHandlerTypeEnum alertHandlerTypeEnum;

        @Schema(name = "AlertId", title = "告警处理方式 ID 列表", required = true)
        @JsonProperty(value = "AlertId", required = true)
        private Long alertId;

        @Schema(name = "AlertName", title = "告警名称", required = true)
        @JsonProperty(value = "AlertName", required = true)
        private String alertName;
    }

}
