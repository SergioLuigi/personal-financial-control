package br.com.sergioluigi.personalfinancialcontrol.infra.security.keycloak.service;

import br.com.sergioluigi.personalfinancialcontrol.application.usecase.FindUserByUsername;
import br.com.sergioluigi.personalfinancialcontrol.application.usecase.UpdateUser;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.*;

@Service
public class UpdateKeyCloakUserProfileService extends AbstractKeyCloakService {

    private final FindUserByUsername findUserByUsername;
    private final UpdateUser updateUser;

    public UpdateKeyCloakUserProfileService(
            Keycloak keycloak,
            FindUserByUsername findUserByUsername,
            UpdateUser updateUser) {
        super(keycloak);
        this.findUserByUsername = findUserByUsername;
        this.updateUser = updateUser;
    }

    public void execute(String username, UserModel userModel) {

        var databaseUser = findUserByUsername.execute(username);

        var keyCloakUser = Optional.ofNullable(getKeycloakUserByUsername(username))
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

        if (!databaseUser.getEmail().equals(keyCloakUser.getEmail()) ||
                !databaseUser.getUsername().equals(keyCloakUser.getUsername())) {
            throw new ApplicationException(ASYNC_USER_DATA);
        }

        var splitName = userModel.getName().split(" ");

        keyCloakUser.setFirstName(splitName[0]);
        keyCloakUser.setLastName(splitName[splitName.length - 1]);

        databaseUser.setName(userModel.getName());

        try {

            getRealm().users().get(keyCloakUser.getId()).update(keyCloakUser);
            updateUser.execute(databaseUser);

        } catch (Exception e) {
            throw new ApplicationException(ERROR_DURING_USER_UPDATE);
        }
    }
}
