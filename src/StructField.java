import java.util.ArrayList;
import java.util.List;

public class StructField {
    private final String type; // тип на полето
    private final String name; // име на полето
    private String value; // стойност на полето
    private String min; // минимална стойност на полето
    private String max; // максимална стойност на полето
    private List<String> arrayValues = new ArrayList<>(); // списък със стойности ако полето е масив

    // конструктор на полето
    public StructField(String type, String name, String min, String max) {
        this.type = type;
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public String getName(){
        return name;
    }
    public String getMin(){
        return min;
    }
    public String getMax(){
        return max;
    }
    public List<String> getArrayValues(){
        return arrayValues;
    }
    public String getType(){
        return mapType(type);
    }
    public void setValue(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }

    // функция за разпознаване на различните типове от C файла
    private String mapType(String type){
        switch (type){
            case "uint8_t":
                return "int";
            case "int16_t":
                return "short";
            case "uint32_t":
                return "long";
            default:
                return "Unknown";
        }
    }
}
