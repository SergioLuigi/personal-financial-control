package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.endpoint

import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.manager.CognitoUserManager
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.ConfirmSelfSignupDTO
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class ConfirmSelfSignupCognitoUserEndpoint(
    private val cognitoManager: CognitoUserManager
) {

    @ResponseStatus(NO_CONTENT)
    @PostMapping("/confirm-self-signup")
    fun signup(@RequestBody @Valid confirmSelfeSignUpRequest: ConfirmSelfSignupDTO) =
        cognitoManager.confirmSelfSignUp(confirmSelfeSignUpRequest)
}