package ru.sergalas.magaz.web.security;


import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.IOException;

import java.io.IOException;

@RequiredArgsConstructor
public class OAuthClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private final String registrationId;

    @Setter
    private SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            OAuth2AuthorizedClient authorizedClient = this.authorizedClientManager.authorize(
                    OAuth2AuthorizeRequest.withClientRegistrationId(this.registrationId)
                            .principal(this.securityContextHolderStrategy.getContext().getAuthentication())
                            .build());

            if (authorizedClient != null) {
                OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                request.getHeaders().setBearerAuth(accessToken.getTokenValue());
                System.out.println("Token added to request: " + accessToken.getTokenValue()); // Логирование
            } else {
                System.out.println("Failed to authorize client"); // Логирование
            }
        }

        return execution.execute(request, body);
    }
}
