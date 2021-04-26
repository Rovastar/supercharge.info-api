package com.redshiftsoft.tesla.web.mvc.userlogin;

import kdw.common.secure.passwordhashing.PasswordHashLogic;
import org.junit.Test;

public class UserLoginController_UT {

    private final UserLoginController controller = new UserLoginController();

    @Test
    public void test() {
    }

    @Test
    public void createUser() {
        final PasswordHashLogic hashLogic = new PasswordHashLogic();
        String salt = hashLogic.generateNewSalt();

        String password = "password";
        String hash = hashLogic.hash(password, salt);

        System.out.println("salt:" + salt);
        System.out.println("hash:" + hash);
    }

}