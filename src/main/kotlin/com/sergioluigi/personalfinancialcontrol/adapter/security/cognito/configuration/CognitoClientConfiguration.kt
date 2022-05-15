package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.configuration

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CognitoClientConfiguration (
    private val cognitoRequestProperties: CognitoConfigurationProperties
) {

    @Bean
    fun cognitoClient(): AWSCognitoIdentityProvider =
        AWSCognitoIdentityProviderClientBuilder
            .standard()
            .withCredentials(EnvironmentVariableCredentialsProvider())
            .withRegion(cognitoRequestProperties.region)
            .build()

}