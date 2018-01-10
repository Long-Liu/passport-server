package org.infinity.passport.config;

import java.security.KeyPair;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.infinity.passport.domain.Authority;
import org.infinity.passport.security.AjaxLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Refer 
 * http://projects.spring.io/spring-security-oauth/docs/oauth2.html
 * https://stackoverflow.com/questions/33812805/spring-security-oauth2-not-working-without-jwt
 */
@Configuration
public class UaaConfiguration {

    /**
     * Apply the token converter (and enhancer) for token store.
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * This bean generates an token enhancer, which manages the exchange
     * between JWT access tokens and Authentication in both direction.
     *
     * @return an access token converter configured with the authorization
     *         server's public/private keys
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("config/keystore.jks"),
                "67G-fpf-tg3-pdj-u68".toCharArray()).getKeyPair("key-alias");
        converter.setKeyPair(keyPair);
        return converter;
    }

    /**
     * Resource server is used to process API calls
     */
    @Configuration
    @EnableResourceServer
    @SessionAttributes("authorizationRequest")
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

        // @Autowired
        // private TokenStore tokenStore;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied"))
            .and()
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .and()
//                    .csrf().disable()
                .headers()
                .frameOptions()
                .disable()
            .and()
                .authorizeRequests()
                // Do not need authentication
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/swagger-resources/configuration/ui").permitAll()
                .antMatchers("/management/health").permitAll()
                // Need authentication
                .antMatchers("/api/**").authenticated()
                // Need 'DEVELOPER' authority
                .antMatchers("/v2/api-docs/**").hasAuthority(Authority.DEVELOPER)
                .antMatchers("/management/**").hasAuthority(Authority.DEVELOPER);
            // @formatter:on
        }

        //        @Override
        //        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        //            resources.resourceId("uaa").tokenStore(tokenStore);
        //        }
    }

    /**
     * Authorization server负责获取用户的授权并且发布token
     * AuthorizationServerEndpointsConfiguration
     */
    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager   authenticationManager;

        @Autowired
        private UserDetailsService      userDetailsService;

        @Autowired
        private JwtAccessTokenConverter jwtAccessTokenConverter;

        /**
         * Access Token is valid for 5 minutes
         * Refresh Token is valid for 7 days
         */
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            // @formatter:off
            clients.inMemory()
                .withClient("internal_client")
                .secret("7GF-td8-98s-9hq-HU8")
                .scopes("internal-app")
                .autoApprove(true)
                .authorizedGrantTypes("password", "authorization_code")
                .accessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7))
                .refreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7))
                .and()
                .withClient("third_party_client")
                .secret("3fP-efd-40g-4Re-fvG")
                .scopes("openid")
                .autoApprove(false)
                .authorizedGrantTypes("implicit", "refresh_token", "password", "authorization_code")
                .accessTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(5))
                .refreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7));
            // @formatter:on
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            // Note: authenticationManager, tokenStore, userDetailsService must
            // be injected here
            // 如果没有userDetailsService在使用refresh token刷新access token时报错
            endpoints.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter)
                    .userDetailsService(userDetailsService);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            // 如果没有下面一条语句会在使用authorization code获取access token时报Full
            // authentication is required to access this resource错误
            oauthServer.allowFormAuthenticationForClients();

            // 下面语句好像没起作用
            oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
        }
    }
}
