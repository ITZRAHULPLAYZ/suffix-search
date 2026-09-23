import java.util.Collections;
import java.util.List;

public class SearchResult {

    private static final int CONTEXT_WINDOW = 30;

    private final String        pattern;
    private final List<Integer> positions;
    private final long          elapsedNs;
    private final String        text;

    SearchResult(String pattern, List<Integer> positions, long elapsedNs, String text) {
        this.pattern   = pattern;
        this.positions = Collections.unmodifiableList(positions);
        this.elapsedNs = elapsedNs;
        this.text      = text;
    }

    public String        getPattern()   { return pattern; }
    public List<Integer> getPositions() { return positions; }
    public int           getCount()     { return positions.size(); }
    public boolean       isFound()      { return !positions.isEmpty(); }
    public long          getElapsedNs() { return elapsedNs; }

    public String getElapsedFormatted() {
        if (elapsedNs < 1_000_000L) return String.format("%.3f µs", elapsedNs / 1_000.0);
        return String.format("%.3f ms", elapsedNs / 1_000_000.0);
    }

    public void print(int maxOccurrences) {
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.printf( "║  Pattern  : %-52s║%n", "\"" + pattern + "\"");
        System.out.printf( "║  Found    : %-52s║%n", isFound() ? getCount() + " occurrence(s)" : "NOT FOUND");
        System.out.printf( "║  Time     : %-52s║%n", getElapsedFormatted());
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");

        if (!isFound()) {
            System.out.println("║  (no occurrences)                                                ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════╝");
            return;
        }

        StringBuilder posSb = new StringBuilder();
        for (int i = 0; i < positions.size(); i++) {
            posSb.append(positions.get(i));
            if (i < positions.size() - 1) posSb.append(", ");
            if (posSb.length() > 50) { posSb.append("…"); break; }
        }
        System.out.printf("║  Positions: %-52s║%n", posSb);
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        System.out.printf( "║  %-5s  %-57s║%n", "Pos", "Context (…[match]…)");
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");

        int shown = Math.min(maxOccurrences, positions.size());
        for (int i = 0; i < shown; i++) {
            int pos = positions.get(i);
            System.out.printf("║  %-5d  %-57s║%n", pos, contextSnippet(pos));
        }
        if (shown < positions.size())
            System.out.printf("║  … (%d more occurrences not shown)%n", positions.size() - shown);

        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
    }

    private String contextSnippet(int pos) {
        int matchEnd = Math.min(pos + pattern.length(), text.length());
        int start    = Math.max(0, pos - CONTEXT_WINDOW);
        int end      = Math.min(text.length(), matchEnd + CONTEXT_WINDOW);

        String pre   = clean(text.substring(start, pos));
        String match = clean(text.substring(pos, matchEnd));
        String post  = clean(text.substring(matchEnd, end));

        String snippet = (start > 0 ? "…" : "") + pre + "[" + match + "]" + post + (end < text.length() ? "…" : "");
        return snippet.length() > 57 ? snippet.substring(0, 54) + "…" : snippet;
    }

    private static String clean(String s) {
        return s.replace("\n", "↵").replace("\r", "").replace("\t", "→");
    }
}
