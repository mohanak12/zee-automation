package com.thed;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity Wrapper
 */
public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String loginName;
    private String location;
    private Set<Role> roles = new HashSet<Role>(0);

    // lastName,firstName,email,username,loginName,location,roleId,roleName
    public User(String[] userData) {

        this.firstName = userData[1];
        this.lastName = userData[0];
        this.email = userData[2];
        this.username = userData[3];

        if (userData.length > 4) {
            this.loginName = userData[4];
        }
        if (userData.length > 5) {
            this.location = userData[5];
        }
        Role role = new Role(3l, "tester");

        if (userData.length > 6) {
            try {
                role.id = new Long(userData[6]);

            } catch (Exception e) {
                //parsing issue
            }
        }

        if (userData.length > 7) {
            role.name = userData[7];
        }
        roles.add(role);
    }
}


class Role {
    public Long id;
    public String name;

    public Role(Long roleId, String name) {
        this.id = roleId;
        this.name = name;
    }
}
