package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.endpoint

import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.manager.CognitoUserManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class ResendConfirmationCodeSelfSignUp(
    private val cognitoManager: CognitoUserManager
) {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/resend-confirmation-code/{cpf}")
    fun resendConfirmationCode(@PathVariable cpf: String) =
        cognitoManager.resendConfirmationCode(cpf)
}