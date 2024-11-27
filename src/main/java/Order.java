import java.util.List;

public class Order {
    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public List<String> getIngredients() {
        return ingredients;
    }
    private List <String> ingredients;
    }



