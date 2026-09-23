public class SuffixArray {

    private final String text;
    private final int    n;
    private final int[]  sa;
    private final int[]  rank;
    private final int[]  lcp;

    public SuffixArray(String text) {
        if (text == null || text.isEmpty())
            throw new IllegalArgumentException("Input text must be non-null and non-empty.");
        this.text = text;
        this.n    = text.length();
        this.sa   = buildSuffixArray();
        this.rank = buildRankArray();
        this.lcp  = buildLCPArray();
    }

    // O(n log n) prefix-doubling
    private int[] buildSuffixArray() {
        Integer[] sa   = new Integer[n]; // Integer[] needed for Comparator
        int[]     rank = new int[n];
        int[]     tmp  = new int[n];

        for (int i = 0; i < n; i++) { sa[i] = i; rank[i] = text.charAt(i); }

        for (int gap = 1; gap < n; gap <<= 1) {
            final int   g  = gap;
            final int[] rk = rank.clone();

            java.util.Arrays.sort(sa, (a, b) -> {
                if (rk[a] != rk[b]) return Integer.compare(rk[a], rk[b]);
                int ra = (a + g < n) ? rk[a + g] : -1;
                int rb = (b + g < n) ? rk[b + g] : -1;
                return Integer.compare(ra, rb);
            });

            tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++) {
                tmp[sa[i]] = tmp[sa[i - 1]];
                boolean same = (rk[sa[i]] == rk[sa[i - 1]]);
                if (same) {
                    int ai = sa[i]     + g < n ? rk[sa[i]     + g] : -1;
                    int bi = sa[i - 1] + g < n ? rk[sa[i - 1] + g] : -1;
                    same = (ai == bi);
                }
                if (!same) tmp[sa[i]]++;
            }
            System.arraycopy(tmp, 0, rank, 0, n);
            if (rank[sa[n - 1]] == n - 1) break;
        }

        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = sa[i];
        return result;
    }

    // inverse SA: position -> rank
    private int[] buildRankArray() {
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;
        return rank;
    }

    // Kasai O(n) LCP
    private int[] buildLCPArray() {
        int[] lcp = new int[n];
        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) h++;
                lcp[rank[i]] = h;
                if (h > 0) h--;
            }
        }
        return lcp;
    }

    public int[]  getSA()   { return sa.clone(); }
    public int[]  getLCP()  { return lcp.clone(); }
    public String getText() { return text; }
    public int    length()  { return n; }

    public void printSuffixArray(int maxLen) {
        System.out.println("┌──────────────────────────────────────────────────────────────────┐");
        System.out.printf( "│  %-5s  %-8s  %-50s│%n", "Rank", "SA[i]", "Suffix");
        System.out.println("├──────────────────────────────────────────────────────────────────┤");
        for (int i = 0; i < n; i++) {
            int    pos    = sa[i];
            String suffix = text.substring(pos);
            if (suffix.length() > maxLen) suffix = suffix.substring(0, maxLen) + "…";
            suffix = suffix.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            System.out.printf("│  %-5d  %-8d  %-50s│%n", i, pos, suffix);
        }
        System.out.println("└──────────────────────────────────────────────────────────────────┘");
    }

    public void printLCPArray(int limit) {
        int rows = Math.min(limit, n);
        System.out.println("┌──────────────────────────────────────┐");
        System.out.printf( "│  %-6s  %-8s  %-8s  %-8s│%n", "i", "SA[i]", "LCP[i]", "Rank");
        System.out.println("├──────────────────────────────────────┤");
        for (int i = 0; i < rows; i++)
            System.out.printf("│  %-6d  %-8d  %-8d  %-8d│%n", i, sa[i], lcp[i], rank[sa[i]]);
        if (rows < n)
            System.out.printf("│  … (%d more rows hidden)%n", n - rows);
        System.out.println("└──────────────────────────────────────┘");
    }
}
