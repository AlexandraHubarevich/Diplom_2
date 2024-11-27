import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GetOrdersTest {
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
    @DisplayName("Получение заказов конкретного пользователя: авторизованный пользователь")

    public void getOrdersForAuthUser() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.сreateUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));
        String accessToken = responseCreate.jsonPath().getString("accessToken");
        Response getOrdersResponse = userClient.getOrdersWithAuthorization(accessToken, user);
        Assert.assertEquals("true", getOrdersResponse.jsonPath().getString("success"));
        Assert.assertEquals(200, getOrdersResponse.statusCode());
        userClient.deleteUser(responseCreate.jsonPath().getString("accessToken"));
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя: неавторизованный пользователь")

    public void getOrdersForNotAuthUser() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.сreateUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));
        Response getOrdersResponse = userClient.getOrdersWithOutAuthorization(user);
        Assert.assertEquals("false", getOrdersResponse.jsonPath().getString("success"));
        Assert.assertEquals("You should be authorised", getOrdersResponse.jsonPath().getString("message"));
        Assert.assertEquals(401, getOrdersResponse.statusCode());
        userClient.deleteUser(responseCreate.jsonPath().getString("accessToken"));
    }


}