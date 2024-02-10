package br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.service;

import br.com.sergioluigi.personalfinancialcontrol.domain.SubscriptionType;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.usecase.user.SaveNewUser;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.USER_ALREADY_EXISTS;

@Service
public class RegisterKeyCloakUserService extends AbstractKeyCloakService {

    private final SaveNewUser saveNewUser;

    public RegisterKeyCloakUserService(Keycloak keycloak, SaveNewUser saveNewUser) {
        super(keycloak);
        this.saveNewUser = saveNewUser;
    }

    @Transactional
    public void execute(UserModel userModel) {

        this.getKeycloakUserByUsername(userModel.getUsername())
                .ifPresent(user -> { throw new ApplicationException(USER_ALREADY_EXISTS); });

            userModel.setSubscriptionType(SubscriptionType.FREE);
            UserRepresentation userRepresentation = buildUserRepresentation(userModel);

            var usersResource = this.getRealmUsers();

            var creationResponse = usersResource.create(userRepresentation);

            var userId = CreatedResponseUtil.getCreatedId(creationResponse);
            var userResource = usersResource.get(userId);
            var roleRepresentation = getRealRoles()
                    .get(userModel.getSubscriptionType().name()).toRepresentation();

            userResource.roles().realmLevel().add(List.of(roleRepresentation));

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue("123456");
            userResource.resetPassword(passwordCred);

            saveNewUser.execute(userModel);
    }

    private static UserRepresentation buildUserRepresentation(UserModel userModel) {
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setUsername(userModel.getUsername());
        var name = userModel.getName();
        var splitName = name.split(" ");
        userRepresentation.setFirstName(splitName[0]);
        userRepresentation.setLastName(splitName[splitName.length - 1]);
        userRepresentation.setEmail(userModel.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        return userRepresentation;
    }
}
