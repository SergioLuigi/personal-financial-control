package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.cognito")
class CognitoConfigurationProperties {

    lateinit var clientSecret: String

    lateinit var clientId: String

    lateinit var scope: String

    lateinit var authorizationGrantType: String

    lateinit var redirectUri: String

    @Value("\${cognito.region}")
    lateinit var region: String

    @Value("\${cognito.poolId}")
    lateinit var poolId: String
    
    var secretKey: String = System.getenv("COGNITO_CLIENT_SECRET")
    
    var poolIdHexa: String = System.getenv("COGNITO_POOL_ID_HEXA")
}