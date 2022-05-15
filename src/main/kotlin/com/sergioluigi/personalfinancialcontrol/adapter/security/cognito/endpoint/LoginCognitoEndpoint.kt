package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.endpoint

import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.manager.CognitoUserManager
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.AcessTokenDTO
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.LoginDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class LoginCognitoEndpoint(
    private val cognitoManager: CognitoUserManager
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid login: LoginDTO): AcessTokenDTO = cognitoManager.login(login)

}