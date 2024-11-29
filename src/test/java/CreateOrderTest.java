import com.github.javafaker.Faker;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class CreateOrderTest {
    Faker faker = new Faker();
    private String email = faker.internet().emailAddress();
    private String name = faker.name().username();
    private String password = faker.internet().password(3, 10);
    private UserClient userClient = new UserClient();
    private OrderClient orderClient = new OrderClient();
    private String incorrectIngredients = "61c0c5a71d1f382001bdaaa6f";
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией")
    public void createOrderWithAuth() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.createUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));

        Response loginResponse = userClient.loginUser(new User(user.getEmail(), user.getName(), user.getPassword()));
        Assert.assertEquals("true", loginResponse.jsonPath().getString("success"));
        accessToken = loginResponse.jsonPath().getString("accessToken");

        Response ingredientList = orderClient.getIngredients();
        String allList = ingredientList.jsonPath().getString("data._id");
        String[] allIngredients = allList.split(", ");

        List<String> stringList = new ArrayList<>();
        stringList.add(allIngredients[1]);
        stringList.add(allIngredients[2]);
        Order orders = new Order(stringList);
        Response createOrder = orderClient.createOrderWithAuth(accessToken, orders);
        Assert.assertEquals("true", createOrder.jsonPath().getString("success"));
        Assert.assertEquals(200, createOrder.statusCode());

    }

    @Test
    @DisplayName("Создание заказа: без авторизации")
    public void createOrderWithoutAuth() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.createUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));
        Response loginResponse = userClient.loginUser(new User(user.getEmail(), user.getName(), user.getPassword()));
        Assert.assertEquals("true", loginResponse.jsonPath().getString("success"));
        Response ingredientList = orderClient.getIngredients();

        String allList = ingredientList.jsonPath().getString("data._id");
        String[] allIngredients = allList.split(", ");
        List<String> stringList = new ArrayList<>();
        stringList.add(allIngredients[1]);
        stringList.add(allIngredients[2]);
        Order orders = new Order(stringList);

        Response createOrder = orderClient.createOrderWithoutAuth(orders);
        Assert.assertEquals("false", createOrder.jsonPath().getString("success"));


        accessToken = loginResponse.jsonPath().getString("accessToken");

    }

    @Test
    @DisplayName("Создание заказа: с авторизацией, без ингредиентов")
    public void createOrderWithoutIngredients() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.createUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));
        Response loginResponse = userClient.loginUser(new User(user.getEmail(), user.getName(), user.getPassword()));
        Assert.assertEquals("true", loginResponse.jsonPath().getString("success"));
        accessToken = loginResponse.jsonPath().getString("accessToken");

        List<String> stringList = new ArrayList<>();

        Order orders = new Order(stringList);
        Response createOrder = orderClient.createOrderWithAuth(accessToken, orders);

        Assert.assertEquals("false", createOrder.jsonPath().getString("success"));
        Assert.assertEquals("Ingredient ids must be provided", createOrder.jsonPath().getString("message"));
        Assert.assertEquals(400, createOrder.statusCode());


    }

    @Test
    @DisplayName("Создание заказа: с авторизацией, с неверным хешем ингредиентов")
    public void createOrderWithIncorrectIngredients() {
        User user = new User(email, name, password);
        Response responseCreate = userClient.createUniqueUser(user);
        Assert.assertEquals("true", responseCreate.jsonPath().getString("success"));

        Response loginResponse = userClient.loginUser(new User(user.getEmail(), user.getName(), user.getPassword()));
        Assert.assertEquals("true", loginResponse.jsonPath().getString("success"));
        accessToken = loginResponse.jsonPath().getString("accessToken");

        List<String> stringList = new ArrayList<>();
        stringList.add(incorrectIngredients);

        Order orders = new Order(stringList);
        Response createOrder = orderClient.createOrderWithAuth(accessToken, orders);
        Assert.assertEquals(500, createOrder.statusCode());
    }

    @After
    public void tearDown() {
        userClient.deleteUser(accessToken);
    }
}
