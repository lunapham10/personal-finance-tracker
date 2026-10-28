package fi.haagahelia.personal_finance_tracker.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OpaAuthorizationService {

    private final RestClient restClient;

    public OpaAuthorizationService(
            @Value("${app.opa.url}") String opaUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(opaUrl)
                .build();
    }

    public boolean isAllowed(Map<String, Object> policyInput) {
        try {
            OpaResponse response = restClient.post()
                    .uri("/v1/data/tracker/authz/allow")
                    .body(Map.of("input", policyInput))
                    .retrieve()
                    .body(OpaResponse.class);

            return response != null
                    && Boolean.TRUE.equals(response.result());

        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Authorization policy service unavailable");
        }
    }

    private record OpaResponse(Boolean result) {
    }
}