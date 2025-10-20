import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner in = new Scanner(System.in);
        InvertedIndex iDoc = new InvertedIndex("stopWords.txt");
        iDoc.indexDocument("collection_html\\King Lear  Entire Play.htm");
        InvertedIndex idoc2 = new InvertedIndex("stopWords.txt");
        idoc2.indexDocument("https://shakespeare.mit.edu/lear/full.html");


        InvertedIndex i = new InvertedIndex("stopWords.txt");

        try {
            i = deserialize();
            System.out.println("Индекс загружен из файла");
        } catch (FileNotFoundException e) {
            System.out.println("Файл индекса не найден, создаем новый индекс");
            // Если файла нет, индексируем коллекцию
            i.indexCollection("collection_html");
            serialize(i);

            System.out.println("Индекс создан и сохранен");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке индекса: " + e.getMessage());
            System.out.println("Создаем новый индекс");
            i.indexCollection("collection_html");
            serialize(i);
        }

        i.indexCollection("collection_html");

        System.out.println("Brutus " + i.executeQuery("Brutus"));
        System.out.println("Caesar " + i.executeQuery("Caesar"));
        System.out.println("Calpurnia " + i.executeQuery("Calpurnia"));
        System.out.println("Brutus AND Brutus " + i.executeQuery("Brutus AND Brutus"));
        System.out.println("Brutus AND Caesar " + i.executeQuery("Brutus AND Caesar"));
        System.out.println("Brutus AND Caesar AND Calpurnia " + i.executeQuery("Brutus AND Caesar AND Calpurnia"));
        System.out.println("Brutus OR Brutus " + i.executeQuery("Brutus OR Brutus"));
        System.out.println("Brutus OR Caesar " + i.executeQuery("Brutus OR Caesar"));
        System.out.println("Brutus OR Caesar OR Calpurnia " + i.executeQuery("Brutus OR Caesar OR Calpurnia"));
        System.out.println("SpiderMan " + i.executeQuery("SpiderMan"));
        System.out.println("Brutus AND SpiderMan " + i.executeQuery("Brutus AND SpiderMan"));
        System.out.println("Caesar OR SpiderMa " + i.executeQuery("Caesar OR SpiderMa"));
        System.out.println("Brutus and and" + i.executeQuery("Brutus and and"));
        System.out.println("and and Caesar " + i.executeQuery("and and Caesar"));
        System.out.println("Brutus or and or Caesar " + i.executeQuery("Brutus or and or Caesar"));
        System.out.println("below and or and Brutus " + i.executeQuery("below and or and Brutus"));

        System.out.println("\nПростые запросы:");
        System.out.println("usurper: " + i.executeQuery("usurper"));
        System.out.println("usurping: " + i.executeQuery("usurping"));
        System.out.println("usurps: " + i.executeQuery("usurps"));
        System.out.println("dagger: " + i.executeQuery("dagger"));
        System.out.println("with: " + i.executeQuery("with"));
        System.out.println("death: " + i.executeQuery("death"));

        System.out.println("\nКонъюнктивные запросы:");
        System.out.println("dagger AND poison: " + i.executeQuery("dagger AND poison"));
        System.out.println("usurper AND throne AND banishment: " + i.executeQuery("usurper AND throne AND banishment"));
        System.out.println("usurps AND thrones AND banishment AND with: " + i.executeQuery("usurps AND thrones AND banishment AND with"));
        System.out.println("the AND dagger AND poison AND treachery: " + i.executeQuery("the AND dagger AND poison AND treachery"));

        System.out.println("\nДизъюнктивные запросы:");
        System.out.println("dagger OR poison: " + i.executeQuery("dagger OR poison"));
        System.out.println("usurper OR throne: " + i.executeQuery("usurper OR throne"));
        System.out.println("usurps OR thrones OR with: " + i.executeQuery("usurps OR thrones OR with"));
        System.out.println("the OR and OR banishment OR exile: " + i.executeQuery("the OR and OR banishment OR exile"));

    }
    public static void serialize(InvertedIndex indexToSerialize) throws IOException, ClassNotFoundException {
        File file = new File("index.out");
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream("index.out");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(indexToSerialize);
            oos.close();
        }
    }

    public static InvertedIndex deserialize() throws IOException, ClassNotFoundException {
        File file = new File("index.out");
        if (!file.exists()) {
            throw new FileNotFoundException("Файла не существует: " + file.getAbsolutePath());
        }

        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream oin = new ObjectInputStream(fis);
        return (InvertedIndex) oin.readObject();
    }

}
