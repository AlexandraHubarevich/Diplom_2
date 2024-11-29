import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step(" .get(\"api/ingredients\")")
    public Response getIngredients() {
        Response responseIngredients = given()
                .header("Content-type", "application/json")
                .when()
                .get("api/ingredients");
        return responseIngredients;
    }

    @Step(" .post(\"api/orders\")")
    public Response createOrderWithAuth(String bearerToken, Order order) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .body(order)
                .when()
                .post("api/orders");
        return response;

    }

    @Step(" .post(\"api/orders\")")
    public Response createOrderWithoutAuth(Order order) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("api/orders");
        return response;

    }
}




