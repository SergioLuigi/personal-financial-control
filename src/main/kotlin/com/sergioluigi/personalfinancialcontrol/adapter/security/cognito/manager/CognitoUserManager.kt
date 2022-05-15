package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.manager

import com.amazonaws.AmazonWebServiceResult
import com.amazonaws.ResponseMetadata
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider
import com.amazonaws.services.cognitoidp.model.*
import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.Group
import com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.configuration.CognitoConfigurationProperties
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.AcessTokenDTO
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.ConfirmSelfSignupDTO
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.LoginDTO
import com.sergioluigi.personalfinancialcontrol.adapter.security.dto.NewUserRequestDTO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class CognitoUserManager(
    private val authenticationHelper: AuthenticationHelper,
    private val cognitoConfigProperties: CognitoConfigurationProperties,
    private val cognitoClient: AWSCognitoIdentityProvider,
) {
    
    companion object{
        const val EMAIL = "email"
        const val CELL_NUMBER = "custom:cellphone"
        const val BIRTH_DATE = "birthdate"
        const val USERNAME = "USERNAME"
        const val PASSWORD = "PASSWORD"
        const val SECRET_HASH = "SECRET_HASH"
        const val PHONE_NUMBER = "phone_number"
        const val NAME = "name"
    }

    fun signup(newUserRequestDTO: NewUserRequestDTO) {
        val signUpRequest = SignUpRequest()
            .withClientId(cognitoConfigProperties.clientId)
            .withUsername(newUserRequestDTO.cpf)
            .withPassword(newUserRequestDTO.password)
            .withSecretHash(authenticationHelper.calculateSecretHash(cognitoConfigProperties.clientId,
                cognitoConfigProperties.clientSecret,
                newUserRequestDTO.cpf
                ))
            .withUserAttributes(
                AttributeType()
                    .withName(EMAIL)
                    .withValue(newUserRequestDTO.email),
                AttributeType()
                    .withName(PHONE_NUMBER)
                    .withValue(newUserRequestDTO.phoneNumber),
                AttributeType()
                    .withName(CELL_NUMBER)
                    .withValue(newUserRequestDTO.cellPhoneNumber),
                AttributeType()
                    .withName(NAME)
                    .withValue(newUserRequestDTO.name),
                AttributeType()
                    .withName(BIRTH_DATE)
                    .withValue(newUserRequestDTO.birthDate.toString()))

        val signUpResult = tryAWSAdminOperation { cognitoClient.signUp(signUpRequest) }
        
                newUserRequestDTO.groups.forEach {
                    addUserToGroup(newUserRequestDTO.cpf, it)
                }
        

    }

    fun confirmSelfSignUp(request: ConfirmSelfSignupDTO) {
        val confirmSignUp = ConfirmSignUpRequest()
            .withConfirmationCode(request.confirmationCode)
            .withUsername(request.username)
            .withClientId(cognitoConfigProperties.clientId)
            .withSecretHash(
                    authenticationHelper.calculateSecretHash(cognitoConfigProperties.clientId,
                    cognitoConfigProperties.clientSecret,
                    request.username
                ))

        tryAWSAdminOperation { cognitoClient.confirmSignUp(confirmSignUp) }
    }

    fun resendConfirmationCode(cpf: String) {
        val resendConfirmationCodeRequest = ResendConfirmationCodeRequest()
            .withUsername(cpf)
            .withClientId(cognitoConfigProperties.clientId)
            .withSecretHash(authenticationHelper.calculateSecretHash(cpf,
                cognitoConfigProperties.clientSecret,
                cognitoConfigProperties.clientId))

        tryAWSAdminOperation { cognitoClient.resendConfirmationCode(resendConfirmationCodeRequest) }
    }

    fun login(login: LoginDTO): AcessTokenDTO {
        val authResult = authenticationHelper.performSRPAuthentication(login.username,login.password)
        return AcessTokenDTO(authResult)
    }
    
    private fun addUserToGroup(cpf: String, group: String = Group.REGULAR.name) {
        val addUserToGroupRequest = AdminAddUserToGroupRequest()
            .withUsername(cpf)
            .withUserPoolId(cognitoConfigProperties.poolId)
            .withGroupName(group)

        tryAWSAdminOperation { cognitoClient.adminAddUserToGroup(addUserToGroupRequest) }
    }
    
    fun <R: AmazonWebServiceResult<T>, T: ResponseMetadata> tryAWSAdminOperation(function: () -> R): R =
        try {
            function()
        }catch (exception: AWSCognitoIdentityProviderException){
            throw ResponseStatusException(HttpStatus.valueOf(exception.statusCode),exception.localizedMessage)
        }

}