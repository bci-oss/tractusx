package net.catenax.semantics.registry;

import com.nimbusds.jose.shaded.json.JSONArray;
import net.catenax.semantics.AuthorizationEvaluator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

public class AuthenticationUtils {

    private static RequestPostProcessor authenticationWithRoles(String ... roles){
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user")
                .claim("resource_access", Map.of("catenax-portal", Map.of("roles", toJsonArray(roles) )))
                .build();
        Collection<GrantedAuthority> authorities = Collections.emptyList();
        return authentication(new JwtAuthenticationToken(jwt, authorities));
    }

    private static JSONArray toJsonArray(String ... elements){
        JSONArray jsonArray = new JSONArray();
        for (String element : elements){
            jsonArray.appendElement(element);
        }
        return jsonArray;
    }

    public static RequestPostProcessor allRoles(){
        return authenticationWithRoles(
                AuthorizationEvaluator.Roles.ROLE_VIEW_DIGITAL_TWIN,
                AuthorizationEvaluator.Roles.ROLE_ADD_DIGITAL_TWIN,
                AuthorizationEvaluator.Roles.ROLE_UPDATE_DIGITAL_TWIN,
                AuthorizationEvaluator.Roles.ROLE_DELETE_DIGITAL_TWIN
        );
    }

    public static RequestPostProcessor readTwin(){
        return authenticationWithRoles(AuthorizationEvaluator.Roles.ROLE_VIEW_DIGITAL_TWIN);
    }

    public static RequestPostProcessor addTwin(){
        return authenticationWithRoles(AuthorizationEvaluator.Roles.ROLE_ADD_DIGITAL_TWIN);
    }

    public static RequestPostProcessor updateTwin(){
        return authenticationWithRoles(AuthorizationEvaluator.Roles.ROLE_UPDATE_DIGITAL_TWIN);
    }

    public static RequestPostProcessor deleteTwin(){
        return authenticationWithRoles(AuthorizationEvaluator.Roles.ROLE_DELETE_DIGITAL_TWIN);
    }

}
