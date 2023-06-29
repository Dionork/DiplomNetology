import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.*;
import java.util.*;

public class DataProcessor {
    public static final String STOP_WORDS_FILE = "stop-ru.txt";

    public Set<String> getStopWords() throws IOException {
        var words = new TreeSet<String>();
        var file = new File(STOP_WORDS_FILE);

        try (var in = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String word;

            while ((word = in.readLine()) != null) {
                words.add(word);
            }
        }
        return words;
    }

    public Map<String, Set<IndexedPage>> pdfDataHandler(File pdfDir) throws IOException {
        Map<String, Set<IndexedPage>> indexedData = new HashMap<>();
        Set<IndexedPage> pagesFromDoc;
        Map<String, Integer>  wordDistribution;

        PdfDocument document;
        PdfPage page;
        String text;
        String fileName;
        String[] words;

        for (File file: Objects.requireNonNull(pdfDir.listFiles())) {
            fileName = file.getName();
            pagesFromDoc = new HashSet<>();
            indexedData.put(fileName, pagesFromDoc);
            document = new PdfDocument(new PdfReader(file));

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                page = document.getPage(i + 1);
                text = PdfTextExtractor.getTextFromPage(page);
                words = text.toLowerCase().split("\\P{IsAlphabetic}+");

                wordDistribution = new HashMap<>();
                for (String word: words) {
                    wordDistribution.put(word, wordDistribution.getOrDefault(word, 0) + 1);
                }
                indexedData.get(fileName).add(new IndexedPage(i, wordDistribution));
            }
        }
        return indexedData;
    }
}
