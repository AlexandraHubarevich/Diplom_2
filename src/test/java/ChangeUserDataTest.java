import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;


public class ChangeUserDataTest {
    Faker faker = new Faker();
    private String email = faker.internet().emailAddress();
    private String name = faker.name().username();
    private String emailUpdate = faker.internet().emailAddress();
    private String nameUpdate = faker.name().username();
    private String password = faker.internet().password(3, 10);
    private UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Изменение данных пользователя: с авторизацией")
    public void changeUserDataWithAuthTest() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.сreateUniqueUser(user);
        assertEquals("true", responseCreate.jsonPath().getString("success"));

        Response responseRegister = userClient.loginUser(user);
        accessToken = responseRegister.jsonPath().getString("accessToken");
        assertEquals("true", responseRegister.jsonPath().getString("success"));

        User userUpdated = new User(emailUpdate, nameUpdate, user.getPassword());
        Response responseUpdate = userClient.updateUserNameEmailPasswordWithAuth(accessToken, userUpdated);


        String oldEmail = responseRegister.jsonPath().getString("user.email");
        String newEmail = responseUpdate.jsonPath().getString("user.email");
        String oldName = responseRegister.jsonPath().getString("user.name");
        String newName = responseUpdate.jsonPath().getString("user.name");
        assertEquals("true", responseUpdate.jsonPath().getString("success"));


        MatcherAssert.assertThat(oldEmail, is(not(newEmail)));
        MatcherAssert.assertThat(oldName, is(not(newName)));
        assertEquals(200, responseUpdate.statusCode());


    }

    @Test
    @DisplayName("Изменение данных пользователя: с авторизацией, но пишем старую почту")
    public void changeUserDataWithAuthTestWithOldEmail() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.сreateUniqueUser(user);
        assertEquals("true", responseCreate.jsonPath().getString("success"));

        Response responseRegister = userClient.loginUser(user);
        accessToken = responseRegister.jsonPath().getString("accessToken");
        assertEquals("true", responseRegister.jsonPath().getString("success"));

        User userUpdated = new User(user.getEmail(), nameUpdate, user.getPassword());
        Response responseUpdate = userClient.updateUserNameEmailPasswordWithAuth(accessToken, userUpdated);

        assertEquals("false", responseUpdate.jsonPath().getString("success"));
        assertEquals("User with such email already exists", responseUpdate.jsonPath().getString("message"));
        Assert.assertEquals(403, responseUpdate.statusCode());


    }

    @Test
    @DisplayName("Изменение данных пользователя: без авторизации")
    public void changeUserDataWithoutAuthTest() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.сreateUniqueUser(user);
        assertEquals("true", responseCreate.jsonPath().getString("success"));


        Response responseRegister = userClient.loginUser(user);
        assertEquals("true", responseRegister.jsonPath().getString("success"));
        User userUpdated = new User(emailUpdate, nameUpdate, user.getPassword());
        Response responseUpdate = userClient.updateUserNameEmailPasswordWithoutAuth(userUpdated);

        assertEquals("false", responseUpdate.jsonPath().getString("success"));
        assertEquals("You should be authorised", responseUpdate.jsonPath().getString("message"));
        assertEquals(401, responseUpdate.statusCode());
        accessToken = responseCreate.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);

    }
}
