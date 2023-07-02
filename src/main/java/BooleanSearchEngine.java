import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> allWords = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        for (File pdf : Objects.requireNonNull(pdfsDir.listFiles())) {
            var doc = new PdfDocument(new PdfReader(pdf));
            int pageNumber = doc.getNumberOfPages();
            for (int i = 1; i <= pageNumber; i++) {
                var page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> frequency = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    var anyWord = word.toLowerCase();
                    frequency.put(anyWord, frequency.getOrDefault(anyWord, 0) + 1);
                }
                for (String word : frequency.keySet()) {
                    PageEntry pageEntry = new PageEntry(pdf.getName(), i, frequency.get(word));
                    if (allWords.containsKey(word)) {
                        allWords.get(word).add(pageEntry);
                    } else {
                        allWords.put(word, new ArrayList<>());
                        allWords.get(word).add(pageEntry);

                    }

                    allWords.values().forEach(Collections::sort);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> result;
        String anyWord = word.toLowerCase();
        result = allWords.get(anyWord);
        return Objects.requireNonNullElse(result, Collections.emptyList());
    }
}
