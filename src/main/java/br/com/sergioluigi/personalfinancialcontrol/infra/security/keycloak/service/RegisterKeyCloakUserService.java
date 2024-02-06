package br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.service;

import br.com.sergioluigi.personalfinancialcontrol.domain.SubscriptionType;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.application.usecase.SaveNewUser;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterKeyCloakUserService extends AbstractKeyCloakService {

    private final SaveNewUser saveNewUser;

    public RegisterKeyCloakUserService(
            Keycloak keycloak,
            SaveNewUser saveNewUser) {
        super(keycloak);
        this.saveNewUser = saveNewUser;
    }

    public void execute(UserModel userModel) {
        try {

            userModel.setSubscriptionType(SubscriptionType.FREE);
            UserRepresentation userRepresentation = buildUserRepresentation(userModel);

            var usersResource = this.getRealm().users();
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

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
