package ch.fhnw.swc.mrs.fixture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ch.fhnw.swc.mrs.data.SimpleMRSServices;
import ch.fhnw.swc.mrs.model.User;

import fit.RowFixture;

public class UserListFixture extends RowFixture {
    private SimpleMRSServices mrsServices = MRSServicesFactory.getInstance();

    @Override
    public Object[] query() throws Exception {
        Collection<User> users = mrsServices.getAllUsers();

        List<ATUser> transformedUsers = users.stream()
                .map(u -> new ATUser(u.getName(), u.getFirstName(), u.getBirthdate()))
                .toList();

        return transformedUsers.toArray();
    }

    @Override
    public Class<ATUser> getTargetClass() {
        return ATUser.class;
    }
}

