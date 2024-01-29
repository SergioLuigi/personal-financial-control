package br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.controller;

import br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.controller.model.KeyCloakCreateUserRequest;
import br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.service.RegisterKeyCloakUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class KeyClockAuthController {

    private final RegisterKeyCloakUserService registerKeyCloakUserService;

    @PostMapping("/user")
    @ResponseStatus(NO_CONTENT)
    public void register(@RequestBody @Valid KeyCloakCreateUserRequest createUserRequest) {
        registerKeyCloakUserService.execute(createUserRequest.toUserModel());
    }
}