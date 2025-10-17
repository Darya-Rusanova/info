import java.io.*;
import java.util.*;

public class InvertedIndex implements Serializable {
    private HashMap<String, LinkedList<Integer>> index;
    private ArrayList<String> documents;

    public InvertedIndex(){
        this.index = new HashMap<>();
        this.documents = new ArrayList<>();
    }

    public void setIndex(HashMap<String, LinkedList<Integer>> index) {
        this.index = index;
    }

    public void setDocuments(ArrayList<String> documents) {
        this.documents = documents;
    }

    public void indexDocument(String path) throws IOException {
        File file = new File(path);
        if (!documents.contains(file.getAbsolutePath())) {
            String content = "";
            Scanner read = new Scanner(file);
            while (read.hasNext()) {
                content += read.next().toLowerCase() + " ";
            }
            read.close();
            String[] terms = content.replaceAll("[^a-z0-9]", " ").split(" ");

            int docId = documents.size();
            documents.add(file.getAbsolutePath());
            for (int i = 0; i < terms.length; i++) {
                String term = terms[i].trim();
                if (!term.isEmpty()) {
                    if (!index.containsKey(term)) {
                        index.put(term, new LinkedList<>());
                        index.get(term).add(docId);
                    }
                    else if (!index.get(term).getLast().equals(docId)) {//!!
                        index.get(term).add(docId);
                    }
                }
            }
            System.out.printf("| %5d  | %-120s | %8d  |\n", docId, file.getAbsolutePath(), index.size());
        }
    }
    public void indexCollection(String folder) throws IOException {
        System.out.println("+--------+--------------------------------------------------------------------------------------------------------------------------+-----------+");
        System.out.printf("| %5s  | %-120s | %8s |\n", "docId", "file", "indexsize");
        System.out.println("+--------+--------------------------------------------------------------------------------------------------------------------------+-----------+");

        File dir = new File(folder);
        String [] files = dir.list();
        Arrays.sort(files);
        for (String file: files) {
            if ((new File(file)).isDirectory()){
                indexCollection(folder+"\\"+ file);
            }
            else{
                indexDocument(folder+"\\"+ file);
            }
        }
        System.out.println("+--------+--------------------------------------------------------------------------------------------------------------------------+-----------+");
    }

    public LinkedList<Integer> getIntersection(LinkedList<Integer> list1, LinkedList<Integer> list2){
        LinkedList<Integer> newList = new LinkedList<>();
        Iterator<Integer> it1 = list1.iterator();
        Iterator<Integer> it2 = list2.iterator();
        Integer num1 = it1.hasNext() ? it1.next() : null;
        Integer num2 = it2.hasNext() ? it2.next() : null;

        while(num1 != null && num2 != null){
            if (num1.equals(num2)){
                newList.add(num1);
                num1 = it1.hasNext() ? it1.next() : null;
                num2 = it2.hasNext() ? it2.next() : null;
            }
            else{
                if (num1.compareTo(num2) < 0){
                    num1 = it1.hasNext() ? it1.next() : null;
                }
                else{
                    num2 = it2.hasNext() ? it2.next() : null;
                }
            }
        }
        return newList;
    }
    public LinkedList<Integer> getUnion(LinkedList<Integer> list1, LinkedList<Integer> list2){
        LinkedList<Integer> newList = new LinkedList<>();
        Iterator<Integer> it1 = list1.iterator();
        Iterator<Integer> it2 = list2.iterator();
        Integer num1 = it1.hasNext() ? it1.next() : null;
        Integer num2 = it2.hasNext() ? it2.next() : null;
        while(num1 != null || num2 != null) {
            if (num1 == null) {
                newList.add(num2);
                num2 = it2.hasNext() ? it2.next() : null;
            } else if (num2 == null) {
                newList.add(num1);
                num1 = it1.hasNext() ? it1.next() : null;
            } else {
                if (num1.equals(num2)) {
                    newList.add(num1);
                    num1 = it1.hasNext() ? it1.next() : null;
                    num2 = it2.hasNext() ? it2.next() : null;
                } else {
                    if (num1.compareTo(num2) < 0) {
                        newList.add(num1);
                        num1 = it1.hasNext() ? it1.next() : null;
                    } else {
                        newList.add(num2);
                        num2 = it2.hasNext() ? it2.next() : null;
                    }
                }
            }
        }

        return newList;
    }
    public LinkedList<Integer> executeQuery(String query) {
        LinkedList<Integer> result = new LinkedList<>();
        String[] terms = query.toLowerCase().split(" ");

        if (terms.length % 2 == 0) {
            System.out.println("Ошибка ввода");
            return result;
        }

        if (terms.length == 1) {
            return index.getOrDefault(terms[0], new LinkedList<>());
        }
        
        result = index.getOrDefault(terms[0], new LinkedList<>());

        for (int i = 1; i < terms.length; i += 2) {
            String operator = terms[i];
            String nextTerm = terms[i + 1];
            LinkedList<Integer> nextList = index.getOrDefault(nextTerm, new LinkedList<>());

            if ("and".equals(operator)) {
                result = getIntersection(result, nextList);
            } else if ("or".equals(operator)) {
                result = getUnion(result, nextList);
            } else {
                System.out.println("Неправильный формат ввода: " + operator);
                return new LinkedList<>();
            }
        }

        return result;
    }
}
