package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.endpoint

import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.manager.CognitoUserManager
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.NewUserRequestDTO
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class SelfSignupCognitoUserEndpoint(
    private val cognitoManager: CognitoUserManager
) {

    @PostMapping("/signup")
    @ResponseStatus(NO_CONTENT)
    fun signup(@RequestBody @Valid newUserRequestDTO: NewUserRequestDTO) =
        cognitoManager.signup(newUserRequestDTO)
}