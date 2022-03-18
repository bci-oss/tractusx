/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.catenax.semantics;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;

@Profile("!local")
@Configuration
public class OAuthSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Represents the roles defined for the registry.
     */
    public static final class Roles {
       public static final String ROLE_VIEW_DIGITAL_TWIN = "view_digital_twin";
       public static final String ROLE_UPDATE_DIGITAL_TWIN = "update_digital_twin";
       public static final String ROLE_CREATE_DIGITAL_TWIN = "add_digital_twin";
       public static final String ROLE_DELETE_DIGITAL_TWIN = "delete_digital_twin";
    }

    /**
     * Applies the jwt token based security configuration.
     *
     * The OpenAPI generator does not support roles.
     * API Paths are authorized in this method with path and method based matchers.
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .authorizeRequests(auth -> auth
            .antMatchers(HttpMethod.OPTIONS).permitAll()
             // fetch endpoint is allowed for reader
            .antMatchers(HttpMethod.POST,"/**/registry/**/fetch").hasRole(Roles.ROLE_VIEW_DIGITAL_TWIN)
             // others are HTTP method based
            .antMatchers(HttpMethod.GET,"/**/registry/**").hasRole(Roles.ROLE_VIEW_DIGITAL_TWIN)
            .antMatchers(HttpMethod.POST,"/**/registry/**").hasRole(Roles.ROLE_CREATE_DIGITAL_TWIN)
            .antMatchers(HttpMethod.PUT,"/**/registry/**").hasRole(Roles.ROLE_UPDATE_DIGITAL_TWIN)
            .antMatchers(HttpMethod.DELETE,"/**/registry/**").hasRole(Roles.ROLE_DELETE_DIGITAL_TWIN)

             // lookup
             // query endpoint is allowed for reader
            .antMatchers(HttpMethod.POST,"/**/lookup/**/query/**").hasRole(Roles.ROLE_VIEW_DIGITAL_TWIN)
             // others are HTTP method based
            .antMatchers(HttpMethod.GET,"/**/lookup/**").hasRole(Roles.ROLE_VIEW_DIGITAL_TWIN)
            .antMatchers(HttpMethod.POST,"/**/lookup/**").hasRole(Roles.ROLE_CREATE_DIGITAL_TWIN)
            .antMatchers(HttpMethod.PUT,"/**/lookup/**").hasRole(Roles.ROLE_UPDATE_DIGITAL_TWIN)
            .antMatchers(HttpMethod.DELETE,"/**/lookup/**").hasRole(Roles.ROLE_DELETE_DIGITAL_TWIN)

          )
          .oauth2ResourceServer()
          .jwt()
                .jwtAuthenticationConverter(new CustomJwtAuthenticationConverter("catenax-portal"));
    }
}
