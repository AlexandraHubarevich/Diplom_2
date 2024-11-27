import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CreateUniqueUserTest {
    Faker faker = new Faker();
    private String email = faker.internet().emailAddress();
    private String name = faker.name().username();
    private String password = faker.internet().password(3, 10);
    private UserClient userClient = new UserClient();


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Создать уникального пользователя")
    public void createUniqueUserTest() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.сreateUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));
        Assert.assertEquals(200, responseCreate.statusCode());
        userClient.deleteUser(responseCreate.jsonPath().getString("accessToken"));

    }

    @Test
    @DisplayName("Cоздать пользователя, который уже зарегистрирован")
    public void createExistedUserTest() {

        User user = new User(email, name, password);
        Response responseCreate = userClient.сreateUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));
        User userDuplicated = new User(user.getEmail(), user.getName(), user.getPassword());
        Response responseDuplicated = userClient.сreateUniqueUser(userDuplicated);
        Assert.assertEquals("false", responseDuplicated.jsonPath().getString("success"));
        Assert.assertEquals("User already exists", responseDuplicated.jsonPath().getString("message"));
        Assert.assertEquals(403, responseDuplicated.statusCode());
        userClient.deleteUser(responseCreate.jsonPath().getString("accessToken"));

    }

    @Test
    @DisplayName("Cоздать пользователя и не заполнить одно из обязательных полей: емайл")
    public void createUserWithoutEmailTest() {
        User user = new User(null, name, password);
        Response responseCreate = userClient.сreateUniqueUser(user);
        Assert.assertEquals("false", responseCreate.jsonPath().getString("success"));
        Assert.assertEquals("Email, password and name are required fields", responseCreate.jsonPath().getString("message"));
        Assert.assertEquals(403, responseCreate.statusCode());


    }

    @Test
    @DisplayName("Cоздать пользователя и не заполнить одно из обязательных полей: юзернейм")
    public void createUserWithoutNameTest() {
        User user = new User(email, null, password);
        Response responseCreate = userClient.сreateUniqueUser(user);
        Assert.assertEquals("false", responseCreate.jsonPath().getString("success"));
        Assert.assertEquals("Email, password and name are required fields", responseCreate.jsonPath().getString("message"));
        Assert.assertEquals(403, responseCreate.statusCode());

    }

    @Test
    @DisplayName("Cоздать пользователя и не заполнить одно из обязательных полей: пароль")
    public void createUserWithoutPasswordTest() {
        User user = new User(email, name, null);
        Response responseCreate = userClient.сreateUniqueUser(user);
        Assert.assertEquals("false", responseCreate.jsonPath().getString("success"));
        Assert.assertEquals("Email, password and name are required fields", responseCreate.jsonPath().getString("message"));
        Assert.assertEquals(403, responseCreate.statusCode());

    }


}

