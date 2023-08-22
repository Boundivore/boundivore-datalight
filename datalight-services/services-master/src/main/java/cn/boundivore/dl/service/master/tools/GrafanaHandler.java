package cn.boundivore.dl.service.master.tools;

import cn.boundivore.dl.base.result.ErrorMessage;
import cn.boundivore.dl.base.result.Result;
import cn.hutool.core.codec.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    public GrafanaHandler(RestTemplate restTemplate, String grafanaBaseUrl, String adminUserName, String adminPassword) {
        this.objectMapper = new ObjectMapper();

        this.grafanaBaseUrl = grafanaBaseUrl;
        this.restTemplate = restTemplate;

        this.adminUserName = adminUserName;
        this.adminPassword = adminPassword;
    }

    public Result<String> createOrgRequest(String orgName) {
        val map = new HashMap<String, Object>() {
            {
                put("name", orgName);
            }
        };

        return post("/api/orgs", map);
    }

    public Result<String> createUserRequest(String userName, String loginName, String password) {

        val map = new HashMap<String, Object>() {
            {
                put("name", userName);
                put("login", loginName);
                put("password", password);
            }
        };

        return post("/api/admin/users", map);
    }

    public Result<String> addUser2OrgRequest(String loginName, String role, String orgId) {
        val map = new HashMap<String, Object>() {
            {
                put("loginOrEmail", loginName);
                put("role", role);
            }
        };

        return post(String.format("/api/orgs/%s/users", orgId), map);
    }


    public Result<String> deleteUserFromOrgRequest(String orgId, String userId) {
        return delete(String.format("/api/orgs/%s/users/%s", orgId, userId));
    }

    public Result<String> createPrometheusDataSourcesRequest(String orgId, String prometheusBaseUri, String loginName, String password) {
        val map = new HashMap<String, Object>() {
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

    public Result<String> createInfluxDBDataSourcesRequest(String orgId, String influxDBBaseUri, String loginName, String password) {
        val map = new HashMap<String, Object>() {
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

    public Result<String> updateDashboardRequest(String dashboard, String loginName, String password) {
        return post("/api/dashboards/db", dashboard, loginName, password);
    }

    public Result<String> updateUserInOrgRequest(String orgId, String userId, String loginName, String role) {
        val map = new HashMap<String, Object>() {
            {
                put("loginOrEmail", loginName);
                put("role", role);
            }
        };

        return patch(String.format("/api/orgs/%s/users/%s", orgId, userId), map);
    }


    public Result<String> getUserInOrg(String orgId) {
        return get(String.format("/api/orgs/%s/users", orgId));
    }

    public Result<String> getOrgByName(String orgName) {
        return get(String.format("/api/orgs/name/%s", orgName));
    }


    public Result<String> getUserByName(String loginName) {
        val map = new HashMap<String, Object>() {
            {
                put("loginOrEmail", loginName);
            }
        };
        return get("/api/users/lookup", map);
    }

    public Result<String> searchAllUsers() {
        val map = new HashMap<String, Object>() {
            {
                put("perpage", "1000");
                put("page", "1");
            }
        };
        return get("/api/users", map);
    }

    public Result<String> searchAllOrgs() {
        return get("/api/orgs");
    }


    public Result<String> deleteUser(String userId) {
        return delete(String.format("/api/admin/users/%s", userId));
    }


    public Result<String> deleteOrg(String orgId) {
        return delete(String.format("/api/orgs/%s", orgId));
    }

//    public Result<String> switchOrg(String orgId){
//        val map = new HashMap<String, Object>(){
//            {
//                put("orgId", orgId);
//            }
//        };
//        return get("/", map);
//    }


    public Result<String> changePassword(String loginName, String oldPassword, String newPassword) {
        val map = new HashMap<String, Object>() {
            {
                put("oldPassword", oldPassword);
                put("newPassword", newPassword);
            }
        };

        val result = put("/api/user/password", map, loginName, oldPassword);
        if (loginName.equals("admin") && result.getCode().equals("00000")) {
            adminPassword = newPassword;
        }

        return result;
    }


    @SneakyThrows
    private Result<String> post(String relativePath, Map<String, Object> bodyMap) {
        return post(relativePath, bodyMap, adminUserName, adminPassword);
    }

    @SneakyThrows
    private Result<String> post(String relativePath, String body) {
        return post(relativePath, body, adminUserName, adminPassword);
    }


    @SneakyThrows
    private Result<String> post(String relativePath, Map<String, Object> bodyMap, String loginName, String password) {
        val body = objectMapper.writer().writeValueAsString(bodyMap);

        val headers = headers(loginName, password);
        val entity = entity(body, headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.POST, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }

    @SneakyThrows
    private Result<String> post(String relativePath, String body, String loginName, String password) {

        val headers = headers(loginName, password);
        val entity = entity(body, headers);
        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.POST, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }


    @SneakyThrows
    private Result<String> get(String relativePath, Map<String, Object> paramsMap) {
        val headers = headers(adminUserName, adminPassword);
        val entity = entity(headers);

        val params = new StringBuilder();
        for (String k : paramsMap.keySet()) {
            val v = paramsMap.get(k);
            params.append(k).append("=").append(v);
        }

        val responseEntity = restTemplate.exchange(String.format("%s%s?%s", grafanaBaseUrl, relativePath, params.toString()), HttpMethod.GET, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }

    @SneakyThrows
    private Result<String> get(String relativePath) {
        val headers = headers(adminUserName, adminPassword);
        val entity = entity(headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.GET, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }


    @SneakyThrows
    private Result<String> delete(String relativePath) {
        val headers = headers(adminUserName, adminPassword);
        val entity = entity(headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.DELETE, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }

    @SneakyThrows
    private Result<String> patch(String relativePath, Map<String, Object> bodyMap) {
        val body = objectMapper.writer().writeValueAsString(bodyMap);

        val headers = headers(adminUserName, adminPassword);
        val entity = entity(body, headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.PATCH, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }

    @SneakyThrows
    private Result<String> put(String relativePath, Map<String, Object> bodyMap) {
        return put(relativePath, bodyMap, adminUserName, adminPassword);
    }

    @SneakyThrows
    private Result<String> put(String relativePath, Map<String, Object> bodyMap, String loginName, String password) {
        val body = objectMapper.writer().writeValueAsString(bodyMap);

        val headers = headers(loginName, password);
        val entity = entity(body, headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.PUT, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }


    private HttpEntity<String> entity(String body, HttpHeaders headers) {
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<String> entity(HttpHeaders headers) {
        return new HttpEntity<>(headers);
    }

    private HttpHeaders headers(String user, String password) {
        return new HttpHeaders() {
            {
                add("Accept", "application/json");
                add("Content-Type", "application/json");
                add("Authorization", basicAuthValue(user, password));
            }
        };
    }

    private String basicAuthValue(String user, String password) {
        return String.format("Basic %s", Base64.encode(user + ":" + password));
    }
}
