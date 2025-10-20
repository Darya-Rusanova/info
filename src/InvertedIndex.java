import org.jsoup.Jsoup;


import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.Files;

import org.tartarus.snowball.ext.englishStemmer;
public class InvertedIndex implements Serializable {
    private HashMap<String, LinkedList<Integer>> index;
    private ArrayList<String> documents;
    private HashSet<String> stopWords;
    private englishStemmer stemmer;

    public InvertedIndex(){
        this.index = new HashMap<>();
        this.documents = new ArrayList<>();
        this.stemmer = new englishStemmer();
        this.stopWords = new HashSet<>();
    }
    public InvertedIndex(String stopWordsPath) throws IOException {
        this();
        File file = new File(stopWordsPath);
        if (file.exists()) {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                stopWords.add(scanner.next().toLowerCase().trim());
            }
            scanner.close();
        }
    }


    public void setIndex(HashMap<String, LinkedList<Integer>> index) {
        this.index = index;
    }

    public void setDocuments(ArrayList<String> documents) {
        this.documents = documents;
    }

    private boolean isStopWord(String word) {
        return stopWords.contains(word.toLowerCase());
    }

    private String stemTerm(String term){
        stemmer.setCurrent(term);
        stemmer.stem();
        return stemmer.getCurrent();
    }


    public void indexDocument(String path) throws IOException {
        String documentPath;
        StringBuilder content = new StringBuilder();

        boolean flag = false;
        if (path.startsWith("http://") || path.startsWith("https://")) {
            flag = true;
            documentPath = path;
        } else {
            File file = new File(path);
            documentPath = file.getAbsolutePath();
        }

        if (!documents.contains(documentPath)) {//2

            if (flag) {
                //content = new StringBuilder(Jsoup.connect(path).proxy("proxy.isu.ru", 3128).get().body().text().toLowerCase());
                content = new StringBuilder(Jsoup.connect(path).get().body().text().toLowerCase());
            } else {
                String mimeType = Files.probeContentType(Paths.get(documentPath));
                if (mimeType.equals("text/html")){
                    content = new StringBuilder(Jsoup.parse(new File(documentPath)).body().text().toLowerCase());
                }
                else {
                    Scanner read = new Scanner(new File(documentPath));
                    while (read.hasNext()) {
                        content.append(read.next().toLowerCase()).append(" ");//1
                    }
                    read.close();
                }
            }

            String[] terms = content.toString().replaceAll("[^a-z0-9]", " ").split(" ");

            int docId = documents.size();
            documents.add(documentPath);
            for (int i = 0; i < terms.length; i++) {
                String term = terms[i].trim();
                if (!term.isEmpty() && !isStopWord(term)) {
                    String stemmedTerm = stemTerm(term);
                    //System.out.println(stemmedTerm);
                    if (!stemmedTerm.isEmpty()) {
                        if (!index.containsKey(stemmedTerm)) {
                            index.put(stemmedTerm, new LinkedList<>());
                            index.get(stemmedTerm).add(docId);
                        } else if (!index.get(stemmedTerm).getLast().equals(docId)) {
                            index.get(stemmedTerm).add(docId);
                        }
                    }
                }
            }
            System.out.printf("| %5d  | %-120s | %8d  |\n", docId, documentPath, index.size());
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

        String[] terms = query.toLowerCase().split(" ");
        String firstTerm = null;
        int firstIndex = 0;
        for (int i = 0; i < terms.length; i+=2) {
            if (!isStopWord(terms[i])) {
                firstTerm = stemTerm(terms[i]);
                firstIndex = i;
                break;
            }
        }

        if (firstTerm == null) {
            return new LinkedList<>();
        }

        LinkedList<Integer> result = index.getOrDefault(firstTerm, new LinkedList<>());

        for (int i = firstIndex + 1; i < terms.length; i += 2) {
            String operator = terms[i];
            String nextTerm = terms[i + 1];
            LinkedList<Integer> nextList;

            if (isStopWord(nextTerm)){
                continue;
            } else {
                nextTerm = stemTerm(nextTerm);
                nextList = index.getOrDefault(nextTerm, new LinkedList<>());
            }

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
