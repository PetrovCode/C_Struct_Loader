import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class xmlBuilder {
    private String structName; // име на структурата
    private List<StructField> fields; // списък с полетата на структурата


    // конструктор на XML класа
    public xmlBuilder(Struct struct){
        this.structName = struct.getStructName();
        this.fields = new ArrayList<>();

        // добавяне на полетата към списъка на структурата
        for(StructField field: struct.getFields()){
            fields.add(field);
        }
    }

    // синтаксис към XML файла
    @Override
    public String toString(){
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<ObjectDescription>\n");
        xmlBuilder.append("\t<ClassName>").append(structName).append("</ClassName>\n");
        xmlBuilder.append("\t<Fields>\n");
        for(StructField field: fields){
            xmlBuilder.append("\t\t<Field>\n");
            xmlBuilder.append("\t\t\t<Name>").append(field.getName()).append("</Name>\n");
            xmlBuilder.append("\t\t\t<Type>").append(field.getType()).append("</Type>\n");
            xmlBuilder.append("\t\t\t<Value>").append(field.getValue()).append("</Value>\n");
            xmlBuilder.append("\t\t</Field>\n");
        }
        xmlBuilder.append("\t</Fields>\n");
        xmlBuilder.append("</ObjectDescription>\n");
        return xmlBuilder.toString();
    }

    // запазване на XML като нов файл
    public void saveToFile(String filename){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){

            writer.write(toString());

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // зареждане на структура от XML файл
    public static Struct loadFromFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        File xmlFile = new File(filename); // зареждане на XML файл

        // създаване на обект, който се използва за парсване на XML документ
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        // създаване на обект, който се използва за парсване на XML файла и създаване на обект 'Document'
        DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
        // парсване на XML файла, представена от 'xmlFile'. Връща обект от клас 'Document', който представлява целия XML документ
        Document document = documentBuilder.parse(xmlFile);
        // нормализиране на XML документа за да се гарантира стандартен формат на структурата
        document.getDocumentElement().normalize();

        // извличане на текстовото съдържание на първия елемент с таг 'ClassName' от документа
        String className = document.getElementsByTagName("ClassName").item(0).getTextContent();
        // извличане на всички елементи с таг 'Field' от документа
        NodeList fieldNodes = document.getElementsByTagName("Field");

        // създаване на структура с извлеченото име от документа
        Struct struct = new Struct(className);

        // цикъл който итерира през всеки възел (node) в списъка 'fieldNodes'
        for(int i = 0; i < fieldNodes.getLength(); i++){
            // извличане на i-тия възел от списъка
            Node fieldNode = fieldNodes.item(i);

            // проверка дали текущият елемент е възел
            // гарантира дали елемента е наистина възел а не друг тип като текст или коментар
            if(fieldNode.getNodeType() == Node.ELEMENT_NODE){
                // преобразуване на fieldNode като Element за да даде достъп до функции специфични за клас 'Element'
                Element fieldElement = (Element) fieldNode;
                // извлича текстовото съдържание на елемента с таг 'Name', 'Type'  и 'Value'
                String name = fieldElement.getElementsByTagName("Name").item(0).getTextContent();
                String type = fieldElement.getElementsByTagName("Type").item(0).getTextContent();
                String value = fieldElement.getElementsByTagName("Value").item(0).getTextContent();

                // създаване на нов обект 'StructField' с параметрите извлечени от XML файла
                StructField field = new StructField(type, name, "", "");
                // задаване на стойността на полето
                field.setValue(value);
                // добавяне на полето към структурата
                struct.addField(field);
            }
        }

        return struct;
    }

}
