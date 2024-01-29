package br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.controller;

import br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.controller.model.UpdateKeyCloakUserProfileRequest;
import br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.service.UpdateKeyCloakUserProfileService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
class UserController {

    private final UpdateKeyCloakUserProfileService updateKeyCloakUserProfileService;

    @PutMapping
    @ResponseStatus(NO_CONTENT)
    @RolesAllowed({"FREE", "PREMIUM"})
    public void updateUserProfile(
            @RequestBody @Valid UpdateKeyCloakUserProfileRequest updateUserProfileRequest,
            JwtAuthenticationToken principal) {
        updateKeyCloakUserProfileService.execute(principal.getName(), updateUserProfileRequest.toUserModel());
    }
}