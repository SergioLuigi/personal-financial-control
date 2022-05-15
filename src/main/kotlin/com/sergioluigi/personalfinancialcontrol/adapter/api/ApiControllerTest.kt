package com.sergioluigi.personalfinancialcontrol.adapter.api

import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.Group
import com.sergioluigi.personalfinancialcontrol.adapter.security.getGrantedAuthorities
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
@RequestMapping("/test")
class ApiControllerTest {

    @GetMapping
    @Secured(Group.ROLE_REGULAR)
    fun getTest(principal: Principal) = principal.getGrantedAuthorities()
}