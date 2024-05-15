package cn.boundivore.dl.service.master.tools;

import cn.boundivore.dl.base.result.ErrorMessage;
import cn.boundivore.dl.base.result.Result;
import cn.hutool.core.codec.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class GrafanaHandler {

    private String grafanaBaseUrl;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private String adminUserName;
    private String adminPassword;

    /**
     * Description: 构造函数，用于初始化GrafanaHandler对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param restTemplate   RestTemplate对象，用于HTTP请求
     * @param grafanaBaseUrl Grafana的基础URL
     * @param adminUserName  管理员用户名
     * @param adminPassword  管理员密码
     */
    public GrafanaHandler(RestTemplate restTemplate, String grafanaBaseUrl, String adminUserName, String adminPassword) {
        this.objectMapper = new ObjectMapper();

        this.grafanaBaseUrl = grafanaBaseUrl;
        this.restTemplate = restTemplate;

        this.adminUserName = adminUserName;
        this.adminPassword = adminPassword;
    }

    /**
     * Description: 创建组织请求的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgName 组织名称
     * @return Result<String> 包含创建组织请求结果的 Result 对象
     */
    public Result<String> createOrgRequest(String orgName) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 6392831639178646179L;

            {
                put("name", orgName);
            }
        };

        return post("/api/orgs", map);
    }

    /**
     * Description: 创建用户请求的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param userName  用户名称
     * @param loginName 用户登录名
     * @param password  用户密码
     * @return Result<String> 包含创建用户请求结果的 Result 对象
     */
    public Result<String> createUserRequest(String userName, String loginName, String password) {

        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 7279178024405862949L;

            {
                put("name", userName);
                put("login", loginName);
                put("password", password);
            }
        };

        return post("/api/admin/users", map);
    }

    /**
     * Description: 将用户添加到组织的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param loginName 用户登录名或邮箱
     * @param role      用户在组织中的角色
     * @param orgId     组织ID
     * @return Result<String> 包含添加用户到组织请求结果的 Result 对象
     */
    public Result<String> addUser2OrgRequest(String loginName, String role, String orgId) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = -3060154817034093442L;

            {
                put("loginOrEmail", loginName);
                put("role", role);
            }
        };

        return post(String.format("/api/orgs/%s/users", orgId), map);
    }

    /**
     * Description: 从组织中删除用户的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgId  组织ID
     * @param userId 用户ID
     * @return Result<String> 包含从组织中删除用户请求结果的 Result 对象
     */
    public Result<String> deleteUserFromOrgRequest(String orgId, String userId) {
        return delete(String.format("/api/orgs/%s/users/%s", orgId, userId));
    }

    /**
     * Description: 创建Prometheus数据源请求的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgId             组织ID
     * @param prometheusBaseUri Prometheus的基础URI
     * @param loginName         登录名
     * @param password          密码
     * @return Result<String> 包含创建Prometheus数据源请求结果的 Result 对象
     */
    public Result<String> createPrometheusDataSourcesRequest(String orgId, String prometheusBaseUri, String loginName, String password) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 8033805433890538253L;

            {
                put("id", null);
                put("orgId", orgId);
                put("name", "Prometheus");
                put("type", "prometheus");
                put("typeLogoUrl", "");
                put("access", "proxy");
                put("url", prometheusBaseUri);
                put("user", "admin");
                put("password", "admin");
                put("database", "");
                put("basicAuth", false);
                put("basicAuthUser", "");
                put("basicAuthPassword", "");
                put("withCredentials", false);
                put("isDefault", true);
                put("jsonData", new HashMap<String, Object>());
                put("secureJsonFields", new HashMap<String, Object>());
                put("version", 1);
                put("readOnly", false);
            }
        };

        return post("/api/datasources", map, loginName, password);
    }

    /**
     * Description: 创建InfluxDB数据源请求的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgId           组织ID
     * @param influxDBBaseUri InfluxDB的基础URI
     * @param loginName       登录名
     * @param password        密码
     * @return Result<String> 包含创建InfluxDB数据源请求结果的 Result 对象
     */
    public Result<String> createInfluxDBDataSourcesRequest(String orgId, String influxDBBaseUri, String loginName, String password) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = -4216862015018262299L;

            {
                put("id", null);
                put("orgId", orgId);
                put("name", "InfluxDB");
                put("type", "influxdb");
                put("typeLogoUrl", "");
                put("access", "proxy");
                put("url", influxDBBaseUri);
                put("user", "");
                put("password", "");
                put("database", "_internal");
                put("basicAuth", false);
                put("basicAuthUser", "");
                put("basicAuthPassword", "");
                put("withCredentials", false);
                put("isDefault", false);
                put("jsonData", new HashMap<String, Object>());
                put("secureJsonFields", new HashMap<String, Object>());
                put("version", 1);
                put("readOnly", false);
            }
        };

        return post("/api/datasources", map, loginName, password);
    }

    /**
     * Description: 更新仪表盘请求的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param dashboard 仪表盘的JSON字符串
     * @param loginName 登录名
     * @param password  密码
     * @return Result<String> 包含更新仪表盘请求结果的 Result 对象
     */
    public Result<String> updateDashboardRequest(String dashboard, String loginName, String password) {
        return this.post("/api/dashboards/db", dashboard, loginName, password);
    }

    /**
     * Description: 更新组织中用户信息的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgId     组织ID
     * @param userId    用户ID
     * @param loginName 用户登录名或邮箱
     * @param role      用户在组织中的角色
     * @return Result<String> 包含更新用户信息请求结果的 Result 对象
     */
    public Result<String> updateUserInOrgRequest(String orgId, String userId, String loginName, String role) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = -5732824713377533657L;

            {
                put("loginOrEmail", loginName);
                put("role", role);
            }
        };

        return patch(String.format("/api/orgs/%s/users/%s", orgId, userId), map);
    }

    /**
     * Description: 获取组织中的用户信息的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgId 组织ID
     * @return Result<String> 包含获取用户信息请求结果的 Result 对象
     */
    public Result<String> getUserInOrg(String orgId) {
        return this.get(String.format("/api/orgs/%s/users", orgId));
    }

    /**
     * Description: 根据组织名称获取组织信息的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgName 组织名称
     * @return Result<String> 包含获取组织信息请求结果的 Result 对象
     */
    public Result<String> getOrgByName(String orgName) {
        return this.get(String.format("/api/orgs/name/%s", orgName));
    }


    /**
     * Description: 根据用户名或邮箱获取用户信息的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param loginName 用户登录名或邮箱
     * @return Result<String> 包含获取用户信息请求结果的 Result 对象
     */
    public Result<String> getUserByName(String loginName) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 3520939653992992981L;

            {
                put("loginOrEmail", loginName);
            }
        };
        return get("/api/users/lookup", map);
    }

    /**
     * Description: 获取所有用户信息的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<String> 包含获取所有用户信息请求结果的 Result 对象
     */
    public Result<String> searchAllUsers() {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 4003526070100400574L;

            {
                put("perpage", "1000");
                put("page", "1");
            }
        };
        return get("/api/users", map);
    }

    /**
     * Description: 获取所有组织信息的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<String> 包含获取所有组织信息请求结果的 Result 对象
     */
    public Result<String> searchAllOrgs() {
        return this.get("/api/orgs");
    }


    /**
     * Description: 删除用户的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param userId 用户ID
     * @return Result<String> 包含删除用户请求结果的 Result 对象
     */
    public Result<String> deleteUser(String userId) {
        return this.delete(String.format("/api/admin/users/%s", userId));
    }

    /**
     * Description: 删除组织的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgId 组织ID
     * @return Result<String> 包含删除组织请求结果的 Result 对象
     */
    public Result<String> deleteOrg(String orgId) {
        return this.delete(String.format("/api/orgs/%s", orgId));
    }

    /**
     * Description: 修改用户密码的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param loginName   用户登录名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return Result<String> 包含修改密码请求结果的 Result 对象
     */
    public Result<String> changePassword(String loginName, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 3166672496826320619L;

            {
                put("oldPassword", oldPassword);
                put("newPassword", newPassword);
            }
        };

        Result<String> result = this.put("/api/user/password", map, loginName, oldPassword);
        if (loginName.equals("admin") && result.getCode().equals("00000")) {
            adminPassword = newPassword;
        }

        return result;
    }

    /**
     * Description: 发送POST请求的私有方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @param bodyMap      请求体参数
     * @return Result<String> 包含POST请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> post(String relativePath, Map<String, Object> bodyMap) {
        return this.post(relativePath, bodyMap, adminUserName, adminPassword);
    }

    /**
     * Description: 发送POST请求的方法（使用字符串作为请求体）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @param body         请求体内容
     * @return Result<String> 包含POST请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> post(String relativePath, String body) {
        return this.post(relativePath, body, adminUserName, adminPassword);
    }

    /**
     * Description: 发送POST请求的方法（使用Map作为请求体）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @param bodyMap      请求体参数的Map
     * @param loginName    用户登录名
     * @param password     用户密码
     * @return Result<String> 包含POST请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> post(String relativePath, Map<String, Object> bodyMap, String loginName, String password) {
        String body = this.objectMapper.writer().writeValueAsString(bodyMap);

        HttpHeaders headers = this.headers(loginName, password);
        HttpEntity<String> entity = entity(body, headers);

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                String.format(
                        "%s%s",
                        this.grafanaBaseUrl,
                        relativePath
                ),
                HttpMethod.POST,
                entity,
                String.class
        );

        return responseEntity.getStatusCodeValue() == 200 ?
                Result.success(
                        responseEntity.getBody()
                )
                : Result.fail(
                new ErrorMessage(
                        responseEntity.getBody()
                )
        );
    }

    /**
     * Description: 发送POST请求的方法（使用字符串作为请求体，带认证信息）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @param body         请求体内容
     * @param loginName    用户登录名
     * @param password     用户密码
     * @return Result<String> 包含POST请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> post(String relativePath, String body, String loginName, String password) {
        HttpHeaders headers = this.headers(loginName, password);
        HttpEntity<String> entity = this.entity(body, headers);
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                String.format(
                        "%s%s",
                        grafanaBaseUrl,
                        relativePath
                ),
                HttpMethod.POST,
                entity,
                String.class
        );

        return responseEntity.getStatusCodeValue() == 200 ?
                Result.success(
                        responseEntity.getBody()
                )
                : Result.fail(
                new ErrorMessage(
                        responseEntity.getBody()
                )
        );
    }

    /**
     * Description: 发送GET请求的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @param paramsMap    请求参数的Map
     * @return Result<String> 包含GET请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> get(String relativePath, Map<String, Object> paramsMap) {
        HttpHeaders headers = this.headers(
                this.adminUserName,
                this.adminPassword
        );

        HttpEntity<String> entity = this.entity(headers);

        StringBuilder params = new StringBuilder();
        for (String k : paramsMap.keySet()) {
            Object v = paramsMap.get(k);
            params.append(k).append("=").append(v).append("&");
        }

        // 去掉最后一个多余的 & 符号
        if (params.length() > 0) {
            params.setLength(params.length() - 1);
        }

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                String.format(
                        "%s%s?%s",
                        grafanaBaseUrl,
                        relativePath,
                        params
                ),
                HttpMethod.GET,
                entity,
                String.class
        );

        return responseEntity.getStatusCodeValue() == 200 ?
                Result.success(
                        responseEntity.getBody()
                )
                : Result.fail(
                new ErrorMessage(
                        responseEntity.getBody()
                )
        );
    }

    /**
     * Description: 发送GET请求的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @return Result<String> 包含GET请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> get(String relativePath) {
        HttpHeaders headers = this.headers(
                this.adminUserName,
                this.adminPassword
        );
        HttpEntity<String> entity = this.entity(headers);

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                String.format(
                        "%s%s",
                        this.grafanaBaseUrl,
                        relativePath
                ),
                HttpMethod.GET,
                entity,
                String.class
        );

        return responseEntity.getStatusCodeValue() == 200 ?
                Result.success(
                        responseEntity.getBody()
                )
                : Result.fail(
                new ErrorMessage(
                        responseEntity.getBody()
                )
        );
    }

    /**
     * Description: 发送DELETE请求的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @return Result<String> 包含DELETE请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> delete(String relativePath) {
        HttpHeaders headers = headers(
                this.adminUserName,
                this.adminPassword
        );
        HttpEntity<String> entity = entity(headers);

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                String.format(
                        "%s%s",
                        this.grafanaBaseUrl,
                        relativePath
                ),
                HttpMethod.DELETE,
                entity,
                String.class
        );

        return responseEntity.getStatusCodeValue() == 200 ?
                Result.success(
                        responseEntity.getBody()
                )
                : Result.fail(
                new ErrorMessage(
                        responseEntity.getBody()
                )
        );
    }

    /**
     * Description: 发送PATCH请求的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @param bodyMap      请求体参数的Map
     * @return Result<String> 包含PATCH请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> patch(String relativePath, Map<String, Object> bodyMap) {
        String body = this.objectMapper.writer().writeValueAsString(bodyMap);

        HttpHeaders headers = this.headers(
                this.adminUserName,
                this.adminPassword
        );
        HttpEntity<String> entity = this.entity(body, headers);

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                String.format(
                        "%s%s",
                        this.grafanaBaseUrl,
                        relativePath
                ),
                HttpMethod.PATCH,
                entity,
                String.class
        );

        return responseEntity.getStatusCodeValue() == 200 ?
                Result.success(
                        responseEntity.getBody()
                )
                : Result.fail(
                new ErrorMessage(
                        responseEntity.getBody()
                )
        );
    }

    /**
     * Description: 发送PUT请求的方法（使用Map作为请求体）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @param bodyMap      请求体参数的Map
     * @return Result<String> 包含PUT请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> put(String relativePath, Map<String, Object> bodyMap) {
        return this.put(
                relativePath,
                bodyMap,
                this.adminUserName,
                this.adminPassword
        );
    }

    /**
     * Description: 发送PUT请求的方法（使用Map作为请求体，带认证信息）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativePath 相对路径
     * @param bodyMap      请求体参数的Map
     * @param loginName    用户登录名
     * @param password     用户密码
     * @return Result<String> 包含PUT请求结果的 Result 对象
     */
    @SneakyThrows
    private Result<String> put(String relativePath, Map<String, Object> bodyMap, String loginName, String password) {
        String body = this.objectMapper.writer().writeValueAsString(bodyMap);

        HttpHeaders headers = this.headers(loginName, password);
        HttpEntity<String> entity = this.entity(body, headers);

        ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                String.format(
                        "%s%s",
                        this.grafanaBaseUrl,
                        relativePath
                ),
                HttpMethod.PUT,
                entity,
                String.class
        );

        return responseEntity.getStatusCodeValue() == 200 ?
                Result.success(
                        responseEntity.getBody()
                )
                : Result.fail(
                new ErrorMessage(
                        responseEntity.getBody()
                )
        );
    }


    /**
     * Description: 创建包含请求体和请求头的HttpEntity对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param body    请求体内容
     * @param headers 请求头对象
     * @return HttpEntity<String> 包含请求体和请求头的HttpEntity对象
     */
    private HttpEntity<String> entity(String body, HttpHeaders headers) {
        return new HttpEntity<>(body, headers);
    }

    /**
     * Description: 创建仅包含请求头的HttpEntity对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param headers 请求头对象
     * @return HttpEntity<String> 仅包含请求头的HttpEntity对象
     */
    private HttpEntity<String> entity(HttpHeaders headers) {
        return new HttpEntity<>(headers);
    }

    /**
     * Description: 根据用户名和密码生成包含基本认证信息的HttpHeaders对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param user     用户名
     * @param password 密码
     * @return HttpHeaders 包含基本认证信息的HttpHeaders对象
     */
    private HttpHeaders headers(String user, String password) {
        return new HttpHeaders() {
            private static final long serialVersionUID = -8490633438205556240L;

            {
                add("Accept", "application/json");
                add("Content-Type", "application/json");
                add("Authorization", basicAuthValue(user, password));
            }
        };
    }

    /**
     * Description: 根据用户名和密码生成基本认证信息的字符串
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param user     用户名
     * @param password 密码
     * @return String 基本认证信息的字符串
     */
    private String basicAuthValue(String user, String password) {
        return String.format(
                "Basic %s",
                Base64.encode(
                        user + ":" + password
                )
        );
    }

    /**
     * Description: 切换组织的方法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param orgId 组织ID
     * @return Result<String> 包含切换组织结果的 Result 对象
     */
    public Result<String> switchOrg(String orgId) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = -5985880712620518652L;

            {
                put("orgId", orgId);
            }
        };
        return get("/", map);
    }

}
