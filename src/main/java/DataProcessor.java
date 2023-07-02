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


        for (File file: Objects.requireNonNull(pdfDir.listFiles())) {
            String fileName = file.getName();
            pagesFromDoc = new HashSet<>();
            indexedData.put(fileName, pagesFromDoc);
            PdfDocument document = new PdfDocument(new PdfReader(file));

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PdfPage page = document.getPage(i + 1);
                String text = PdfTextExtractor.getTextFromPage(page);
                String[] words = text.toLowerCase().split("\\P{IsAlphabetic}+");

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
