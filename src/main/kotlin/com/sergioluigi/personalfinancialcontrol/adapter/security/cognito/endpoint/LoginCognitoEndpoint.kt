package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.endpoint

import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.manager.CognitoUserManager
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.LoginDTO
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.TokenDTO
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Profile("dev")
@RestController
@RequestMapping("/users")
class LoginCognitoEndpoint(
    private val cognitoManager: CognitoUserManager
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid login: LoginDTO): TokenDTO = cognitoManager.login(login)

}