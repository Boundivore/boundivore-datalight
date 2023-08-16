package cn.ucloud.udp.utils;

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

    /**
     * 1.create organization
     *
     * @return
     */
    public Result<String> createOrgRequest(String orgName) {
        val map = new HashMap<String, Object>() {
            {
                put("name", orgName);
            }
        };

        return post("/api/orgs", map);
    }

    /**
     * 2.create user
     *
     * @param userName
     * @param loginName
     * @return
     */
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

    /**
     * 3.add user to organization
     *
     * @param loginName
     * @param role
     * @param orgId
     * @return
     */
    public Result<String> addUser2OrgRequest(String loginName, String role, String orgId) {
        val map = new HashMap<String, Object>() {
            {
                put("loginOrEmail", loginName);
                put("role", role);
            }
        };

        return post(String.format("/api/orgs/%s/users", orgId), map);
    }


    /**
     * 4、delete user from organization
     *
     * @param orgId
     * @param userId
     * @return
     */
    public Result<String> deleteUserFromOrgRequest(String orgId, String userId) {
        return delete(String.format("/api/orgs/%s/users/%s", orgId, userId));
    }


    /**
     * 5、create Prometheus datasources
     *
     * @param orgId
     * @param prometheusBaseUri
     * @return
     */
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

    /**
     * 5.1、create InfluxDB datasources
     *
     * @param orgId
     * @param influxDBBaseUri
     * @return
     */
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

    /**
     * 6、update panel by templated
     *
     * @param dashboard
     * @return
     */
    public Result<String> updateDashboardRequest(String dashboard, String loginName, String password) {
        return post("/api/dashboards/db", dashboard, loginName, password);
    }

    /**
     * 7、update role of the user in organization
     *
     * @param orgId
     * @param userId
     * @param loginName
     * @param role
     * @return
     */
    public Result<String> updateUserInOrgRequest(String orgId, String userId, String loginName, String role) {
        val map = new HashMap<String, Object>() {
            {
                put("loginOrEmail", loginName);
                put("role", role);
            }
        };

        return patch(String.format("/api/orgs/%s/users/%s", orgId, userId), map);
    }


    /**
     * get users in org
     *
     * @param orgId
     * @return
     */
    public Result<String> getUserInOrg(String orgId) {
        return get(String.format("/api/orgs/%s/users", orgId));
    }

    /**
     * get org by orgName
     *
     * @param orgName
     * @return
     */
    public Result<String> getOrgByName(String orgName) {
        return get(String.format("/api/orgs/name/%s", orgName));
    }


    /**
     * get user by userName
     *
     * @param loginName
     * @return
     */
    public Result<String> getUserByName(String loginName) {
        val map = new HashMap<String, Object>() {
            {
                put("loginOrEmail", loginName);
            }
        };
        return get("/api/users/lookup", map);
    }

    /**
     * search all users
     *
     * @return
     */
    public Result<String> searchAllUsers() {
        val map = new HashMap<String, Object>() {
            {
                put("perpage", "1000");
                put("page", "1");
            }
        };
        return get("/api/users", map);
    }

    /**
     * search all organization
     *
     * @return
     */
    public Result<String> searchAllOrgs() {
        return get("/api/orgs");
    }


    /**
     * delete user
     *
     * @param userId
     * @return
     */
    public Result<String> deleteUser(String userId) {
        return delete(String.format("/api/admin/users/%s", userId));
    }

    /**
     * delete organization
     *
     * @param orgId
     * @return
     */
    public Result<String> deleteOrg(String orgId) {
        return delete(String.format("/api/orgs/%s", orgId));
    }

    /**
     * The API is buggy and should not be used for now
     * @param orgId
     * @return
     */
//    public Result<String> switchOrg(String orgId){
//        val map = new HashMap<String, Object>(){
//            {
//                put("orgId", orgId);
//            }
//        };
//        return get("/", map);
//    }


    /**
     * changes the password for the user
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
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
    private synchronized Result<String> post(String relativePath, Map<String, Object> bodyMap) {
        return post(relativePath, bodyMap, adminUserName, adminPassword);
    }

    @SneakyThrows
    private synchronized Result<String> post(String relativePath, String body) {
        return post(relativePath, body, adminUserName, adminPassword);
    }


    @SneakyThrows
    private synchronized Result<String> post(String relativePath, Map<String, Object> bodyMap, String loginName, String password) {
        val body = objectMapper.writer().writeValueAsString(bodyMap);

        val headers = headers(loginName, password);
        val entity = entity(body, headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.POST, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }

    @SneakyThrows
    private synchronized Result<String> post(String relativePath, String body, String loginName, String password) {

        val headers = headers(loginName, password);
        val entity = entity(body, headers);
        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.POST, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }


    @SneakyThrows
    private synchronized Result<String> get(String relativePath, Map<String, Object> paramsMap) {
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
    private synchronized Result<String> get(String relativePath) {
        val headers = headers(adminUserName, adminPassword);
        val entity = entity(headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.GET, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }


    @SneakyThrows
    private synchronized Result<String> delete(String relativePath) {
        val headers = headers(adminUserName, adminPassword);
        val entity = entity(headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.DELETE, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }

    @SneakyThrows
    private synchronized Result<String> patch(String relativePath, Map<String, Object> bodyMap) {
        val body = objectMapper.writer().writeValueAsString(bodyMap);

        val headers = headers(adminUserName, adminPassword);
        val entity = entity(body, headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.PATCH, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }

    @SneakyThrows
    private synchronized Result<String> put(String relativePath, Map<String, Object> bodyMap) {
        return put(relativePath, bodyMap, adminUserName, adminPassword);
    }

    @SneakyThrows
    private synchronized Result<String> put(String relativePath, Map<String, Object> bodyMap, String loginName, String password) {
        val body = objectMapper.writer().writeValueAsString(bodyMap);

        val headers = headers(loginName, password);
        val entity = entity(body, headers);

        val responseEntity = restTemplate.exchange(String.format("%s%s", grafanaBaseUrl, relativePath), HttpMethod.PUT, entity, String.class);

        return responseEntity.getStatusCodeValue() == 200 ? Result.success(responseEntity.getBody()) : Result.fail(new ErrorMessage(responseEntity.getBody()));
    }


    private synchronized HttpEntity<String> entity(String body, HttpHeaders headers) {
        return new HttpEntity<>(body, headers);
    }

    private synchronized HttpEntity<String> entity(HttpHeaders headers) {
        return new HttpEntity<>(headers);
    }

    private synchronized HttpHeaders headers(String user, String password) {
        return new HttpHeaders() {
            {
                add("Accept", "application/json");
                add("Content-Type", "application/json");
                add("Authorization", basicAuthValue(user, password));
            }
        };
    }

    private synchronized String basicAuthValue(String user, String password) {
        return String.format("Basic %s", Base64.encode(user + ":" + password));
    }
}
