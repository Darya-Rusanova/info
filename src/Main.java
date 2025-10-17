import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner in = new Scanner(System.in);
        InvertedIndex iDoc = new InvertedIndex();
        iDoc.indexDocument("collection\\King_Lear.txt");
        InvertedIndex i = new InvertedIndex();
        try {
            i = deserialize();
            System.out.println("Индекс загружен из файла");
        } catch (FileNotFoundException e) {
            System.out.println("Файл индекса не найден, создаем новый индекс");
            // Если файла нет, индексируем коллекцию
            i.indexCollection("collection");
            serialize(i);

            System.out.println("Индекс создан и сохранен");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке индекса: " + e.getMessage());
            System.out.println("Создаем новый индекс");
            i.indexCollection("collection");
            serialize(i);
        }

        i.indexCollection("collection");
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
