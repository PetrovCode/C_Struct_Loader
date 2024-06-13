import java.util.ArrayList;
import java.util.List;

public class Struct {
    private String structName; // име на структура
    private List<StructField> fields; // списък с полетата на структурата

    // конструктор на структурата
    public Struct(String structName){
        this.structName = structName;
        this.fields = new ArrayList<>();
    }

    // фукнция за добавяне на ново поле в структурата
    public void addField(StructField field){
        fields.add(field);
    }

    public List<StructField> getFields(){
        return fields;
    }
    public String getStructName(){
        return structName;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("struct ").append(structName).append(" {\n");
        for(StructField field: fields){
            sb.append("\t").append(field).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    // функция, която промена стойността на вече създадено поле
    public void updateField(String name, String value){
        for(StructField field: fields){
            if(field.getName().equals(name)){
                if(isValidInput(value, field)){
                    field.setValue(value);
                } else {
                    System.out.println("Invalid value for field: " + name);
                }
                return;
            }
        }
    }

    // функция, която проверява дали подадената стойност е валидна за съответното поле
    private boolean isValidInput(String input, StructField field) {
        try {
            int value = Integer.parseInt(input);
            int min = Integer.parseInt(field.getMin());
            int max = Integer.parseInt(field.getMax());
            return value >= min && value <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
