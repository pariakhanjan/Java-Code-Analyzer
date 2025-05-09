import java.util.ArrayList;
import java.util.HashMap;

public class Test {
    private static final double PI_VALUE = 3.14159;

    public static void main(String[] args) {
        int counter = 0;
        HashMap<String, Integer> dataMap = new HashMap<>();

        ArrayList<String> items = new ArrayList<>();
        items.add("first");
        items.add("second");
        items.add("third");

        for (String item : items) {
            if (item.length() > 5) {
                System.out.println("Long word: " + item);
            } else {
                dataMap.put(item, item.length());
            }
            counter++;
        }

        switch (counter) {
            case 1:
                System.out.println("Only one item");
                break;
            case 2:
                System.out.println("Two items");
                break;
            default:
                System.out.println("Many items");
        }

        calculateArea(5.0);
    }

    private static double calculateArea(double radius) {
        return PI_VALUE * radius * radius;
    }

    private void processData(HashMap<String, Integer> input) {
        for (var entry : input.entrySet()) {
            System.out.printf("Key: %s, Value: %d%n",
                    entry.getKey(), entry.getValue());
        }
    }
}