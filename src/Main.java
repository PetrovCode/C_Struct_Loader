import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    // функция за валидация на въведените стойности от потребителя
    private static boolean isValidInput(String input, StructField field){
        try{
            int value = Integer.parseInt(input);
            int min = Integer.parseInt(field.getMin());
            int max = Integer.parseInt(field.getMax());
            return value >= min && value <= max;
        } catch (NumberFormatException e){
            return false;
        }
    }

    // функция за въвеждане на стойности за структурата
    private static void inputValuesForStructs(Struct struct){
        Scanner sc = new Scanner(System.in);
        System.out.println("\nInput values for struct " + struct.getStructName() + ":");
        for(StructField field: struct.getFields()){
            if(field.getName().contains("[")){
                // regex за проверка дали полето е масив
                Pattern pattern = Pattern.compile("\\[(\\d+)U?\\]");
                Matcher matcher = pattern.matcher(field.getName());
                if(matcher.find()) {
                    int arraySize = Integer.parseInt(matcher.group(1));
                    for(int i = 0; i < arraySize; i++){
                        System.out.println("Enter value for " + field.getName() + " [" + i + "] (" + field.getMin() + " - " + field.getMax() + "): ");
                        String input = sc.nextLine();
                        while(!isValidInput(input, field)){
                            System.out.println("Invalid input. Enter value for " + field.getName() + " [" + i + "] (" + field.getMin() + " - " + field.getMax() + "): ");
                            input = sc.nextLine();
                        }
                        field.getArrayValues().add(input);
                    }
                }
            }else{
                System.out.println("Enter value for " + field.getName() + " (" + field.getMin() + " - " + field.getMax() + "): ");
                String input = sc.nextLine();
                while(!isValidInput(input, field)){
                    System.out.println("Invalid input. Enter value for " + field.getName() + " (" + field.getMin() + " - " + field.getMax() + "): ");
                    input = sc.nextLine();
                }
                field.setValue(input);
            }
        }
    }
    // функция за премахване на кавичките при въвеждане пътя на файла
    private static String removeQuotes(String input){

        Pattern pattern = Pattern.compile("^\"?(.*?)\"?$");
        Matcher matcher = pattern.matcher(input);

        if(matcher.find()){
            return matcher.group(1);
        }
        return input;
    }
    // генериране на структура от C файл
    private static List<Struct> generateStructures(String C_filepath){
        System.out.println("");
        List<Struct> structs = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(C_filepath))){

            String line;
            String structName = null;
            // regex за името на структурата и нейните полета
            Pattern structPattern = Pattern.compile("struct\\s+(\\w+)");
            Pattern fieldPattern = Pattern.compile("\\s+(\\w+)\\s+(\\w+\\s*(?:\\[\\s*\\d+U?\\])*);\\s+\\/\\*\\s+min:\\s+(-?\\d+),\\s+max:\\s+(\\d+)\\s+\\*\\/");


            while((line = br.readLine()) != null){
                System.out.println(line);
                Matcher structMatcher = structPattern.matcher(line);
                Matcher fieldMatcher = fieldPattern.matcher(line);
                // ако е намерено име на поле да бъде запазено в променливата 'structName'
                if(structMatcher.find()){
                    structName = structMatcher.group(1);
                }else if(fieldMatcher.find()){
                    if(structName != null){
                        // запазване на стоностите от низа чрез regex групиране
                        String type = fieldMatcher.group(1);
                        String name = fieldMatcher.group(2);
                        String min = fieldMatcher.group(3);
                        String max = fieldMatcher.group(4);
                        StructField field = new StructField(type, name, min, max);
                        // проверка дали 'structs' е празен или последният елемент НЕ съдържа 'structName'
                        if(structs.isEmpty() || !structs.get(structs.size() - 1).toString().contains(structName)){
                            // създава се нова структура с името 'structName' и се добавя настоящото поле
                           Struct struct = new Struct(structName);
                           struct.addField(field);
                           structs.add(struct);
                        } else {
                            // полето се добавя към последната структура от списъка 'structs'
                            structs.get(structs.size() - 1).addField(field);
                        }
                    }
                }
            }
        }catch (IOException e){
            System.err.println("Error: " + e.getMessage());
            showMenu();
        }
        return structs;
    }
    // функция за въвеждане на данни
    private static void inputData(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter path to the file:");
        String path_C = removeQuotes(sc.nextLine());

        List<Struct> structs = generateStructures(path_C);

        for(Struct struct: structs){
            inputValuesForStructs(struct);
        }
        System.out.println("Input for field was successful.");
        System.out.println("Content of XML:\n");
        // създаване на отделен XML файл за всяка структура
        for(Struct struct: structs){
            xmlBuilder xml = new xmlBuilder(struct);
            System.out.println(xml);
            xml.saveToFile("output_" + struct.getStructName() + ".xml");
        }
        System.out.println("New XML file was created for each structure.\n");
    }
    // функция за зареждане на данни
    private static void loadData() throws ParserConfigurationException, IOException, SAXException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter path to the XML file:");
        String path = removeQuotes(sc.nextLine());

        try{
            // зареждане на структура от XML файл
            Struct struct = xmlBuilder.loadFromFile(path);
            System.out.println("Data loaded from XML for struct: " + struct.getStructName());
            for(StructField field: struct.getFields()){
                System.out.println("Field: " + field.getName() + ", Value: " + field.getValue());
            }
        } catch (Exception e){
            System.err.println("Error: " + e.getMessage());
            showMenu();
        }
    }

    public static void showMenu(){
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Menu ===");
        System.out.println("1. Load structures from C file");
        System.out.println("2. Load data from XML file");
        System.out.print("=> ");

        String option = sc.nextLine();

        enterMenuOption(option);
    }

    public static void enterMenuOption(String option){
        switch (option){
            case "1":
                inputData();
                break;
            case "2":
                try {
                    loadData();
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    System.err.println("Error: " + e.getMessage());
                }
                break;
            default:
                System.err.println("Error: No such option from the menu.");
        }
        showMenu();
    }

    public static void main(String[] args) {

        showMenu();

    }
}