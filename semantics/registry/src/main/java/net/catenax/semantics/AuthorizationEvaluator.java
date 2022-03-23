package net.catenax.semantics;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Map;

import static net.catenax.semantics.AuthorizationEvaluator.Roles.*;

public class AuthorizationEvaluator {

    private final String clientId;

    public AuthorizationEvaluator(String clientId) {
        this.clientId = clientId;
    }

    public boolean hasRoleViewDigitalTwin() {
        return containsRole(ROLE_VIEW_DIGITAL_TWIN);
    }

    public boolean hasRoleAddDigitalTwin() {
        return containsRole(ROLE_ADD_DIGITAL_TWIN);
    }

    public boolean hasRoleUpdateDigitalTwin() {
        return containsRole(ROLE_UPDATE_DIGITAL_TWIN);
    }

    public boolean hasRoleDeleteDigitalTwin() {
        return containsRole(ROLE_DELETE_DIGITAL_TWIN);
    }


    private boolean containsRole(String role){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof JwtAuthenticationToken)){
            return false;
        }

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) (authentication);
        Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();

        Object resourceAccess = claims.get("resource_access");
        if (!(resourceAccess instanceof Map)) {
            return false;
        }

        Object resource = ((Map<String, Object>) resourceAccess).get(clientId);
        if(!(resource instanceof Map)){
            return false;
        }

        Object roles =  ((Map<String, Object>)resource).get("roles");
        if(!(roles instanceof Collection)){
            return false;
        }

        Collection<String> rolesList = (Collection<String> ) roles;
        return rolesList.contains(role);
    }

    /**
     * Represents the roles defined for the registry.
     */
    public static final class Roles {
        public static final String ROLE_VIEW_DIGITAL_TWIN = "view_digital_twin";
        public static final String ROLE_UPDATE_DIGITAL_TWIN = "update_digital_twin";
        public static final String ROLE_ADD_DIGITAL_TWIN = "add_digital_twin";
        public static final String ROLE_DELETE_DIGITAL_TWIN = "delete_digital_twin";
    }

}

