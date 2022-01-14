package com.learningpath.authorizationserver.security.configuration;

import com.learningpath.authorizationserver.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final String CLIENT_RESOURCE_ID = "couponservice";
    private static final String PRODUCT_RESOURCE_ID = "product-service";

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private DataSource dataSource;

    @Value("${resource}")
    private String resource;
    @Value("${password}")
    private String password;
    @Value("${alias}")
    private String alias;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore()).accessTokenConverter(jwtAccessTokenConverter()).
                authenticationManager(authenticationManager).userDetailsService(userDetailsService);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient("couponclientapp").secret(passwordEncoder.encode("9999"))
                .authorizedGrantTypes("password","refresh_token").scopes("read","write").resourceIds(CLIENT_RESOURCE_ID)
                .and().withClient("productclientapp").secret(passwordEncoder.encode("9999"))
                .authorizedGrantTypes("password","refresh_token").scopes("read","write").resourceIds(PRODUCT_RESOURCE_ID);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()");
    }

    @Bean
    public TokenStore tokenStore()
    {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
     public JwtAccessTokenConverter jwtAccessTokenConverter()
     {
         JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
         KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(resource),password.toCharArray());
         KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias);
         jwtAccessTokenConverter.setKeyPair(keyPair);
         return jwtAccessTokenConverter;

     }

}
