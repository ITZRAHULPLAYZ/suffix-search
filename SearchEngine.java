import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchEngine {

    private final String text;
    private final int[]  sa;
    private final int    n;

    public SearchEngine(SuffixArray suffixArray) {
        this.text = suffixArray.getText();
        this.sa   = suffixArray.getSA();
        this.n    = suffixArray.length();
    }

    public SearchResult search(String pattern) {
        if (pattern == null || pattern.isEmpty())
            throw new IllegalArgumentException("Pattern must be non-null and non-empty.");

        long start   = System.nanoTime();
        int  lo      = lowerBound(pattern);
        int  hi      = upperBound(pattern);
        long elapsed = System.nanoTime() - start;

        List<Integer> positions = new ArrayList<>();
        if (lo <= hi) {
            for (int i = lo; i <= hi; i++) positions.add(sa[i]);
            Collections.sort(positions);
        }
        return new SearchResult(pattern, positions, elapsed, text);
    }

    private int lowerBound(String pattern) {
        int lo = 0, hi = n;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (compareSuffix(sa[mid], pattern) < 0) lo = mid + 1;
            else                                      hi = mid;
        }
        return lo;
    }

    private int upperBound(String pattern) {
        int lo = 0, hi = n;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (compareSuffix(sa[mid], pattern) <= 0) lo = mid + 1;
            else                                       hi = mid;
        }
        return lo - 1;
    }

    // 0 = suffix at saPos starts with pattern
    private int compareSuffix(int saPos, String pattern) {
        for (int k = 0; k < pattern.length(); k++) {
            if (saPos + k >= n) return -1;
            int cmp = Character.compare(text.charAt(saPos + k), pattern.charAt(k));
            if (cmp != 0) return cmp;
        }
        return 0;
    }

    public long bruteForceTime(String pattern) {
        long start = System.nanoTime();
        int idx = 0;
        while ((idx = text.indexOf(pattern, idx)) != -1) idx++;
        return System.nanoTime() - start;
    }
}
