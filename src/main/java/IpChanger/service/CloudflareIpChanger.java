package IpChanger.service;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CloudflareIpChanger {
    static List<String> dnsIds = new ArrayList<>();
    static List<String> dnsNames = new ArrayList<>();

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders httpHeaders = new HttpHeaders();
    ObjectMapper objectMapper = new ObjectMapper();

    public void geAllDnsNames() throws JsonMappingException, JsonProcessingException {

        String url = "https://api.cloudflare.com/client/v4/zones/207359c28ebf495446b657f15fb25a46/dns_records";
        httpHeaders.set("Authorization", "Bearer iHkMqqopGkxto9nHnSgzQgh81vQYVAHtXXsapw-X");
        httpHeaders.set("Content-Type", "application/json");
        httpHeaders.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String responseBody = responseEntity.getBody();
        Map<String, Object> apiResponse = objectMapper.readValue(responseBody,
                new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> dnsRecords = (List<Map<String, Object>>) apiResponse.get("result");
        dnsIds = dnsRecords.stream().map(i -> (String) i.get("id")).collect(Collectors.toList());
        dnsNames = dnsRecords.stream().map(i -> (String) i.get("name")).collect(Collectors.toList());

        System.out.println(dnsIds);
        System.out.println(dnsNames);
    }

    public void updateIp(String ip) {
        for (int i = 0; i < 11; i++) {
            Map<String, Object> body = new HashMap<>();
            String url = "https://api.cloudflare.com/client/v4/zones/207359c28ebf495446b657f15fb25a46/dns_records/"
                    + dnsIds.get(i);
            httpHeaders.set("Authorization", "Bearer iHkMqqopGkxto9nHnSgzQgh81vQYVAHtXXsapw-X");
            httpHeaders.set("Content-Type", "application/json");
            httpHeaders.set("Accept", "application/json");
            body.put("type", "A");
            body.put("name", dnsNames.get(i));
            body.put("content", ip);
            body.put("ttl", 3600);
            body.put("proxied", false);
            HttpEntity entity = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<String> updateResponse = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            if (updateResponse.getStatusCode().is2xxSuccessful()) {
                System.out.println("DNS record updated successfully: " + updateResponse.getBody());
            } else {
                System.out.println("Failed to update DNS record: " + updateResponse.getStatusCode() + " - "
                        + updateResponse.getBody());
            }
        }

    }

}
