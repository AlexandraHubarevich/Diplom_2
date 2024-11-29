import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


public class UserClient {


    @Step(" .post(\"api/auth/register\")")
    public Response createUniqueUser(User user) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("api/auth/register");
        return response;

    }

    @Step(".delete(\"api/auth/user\")")
    public Response deleteUser(String bearerToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .and()
                .when()
                .delete("api/auth/user");
    }

    @Step(".post(\"/api/auth/login\")")
    public Response loginUser(User user) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
        return response;
    }

    @Step(".patch(\"/api/auth/user\")")
    public Response updateUserNameEmailPasswordWithAuth(String bearerToken, User user) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .body(user)
                .when()
                .patch("/api/auth/user");
        return response;
    }

    @Step(".patch(\"/api/auth/user\")")
    public Response updateUserNameEmailPasswordWithoutAuth(User user) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch("/api/auth/user");
        return response;
    }


    @Step(".get(\"/api/orders\")")
    public Response getOrdersWithAuthorization(String bearerToken, User user) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .body(user)
                .when()
                .get("/api/orders");
        return response;
    }

    @Step(".get(\"/api/orders\")")
    public Response getOrdersWithOutAuthorization(User user) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .get("/api/orders");
        return response;
    }
}
