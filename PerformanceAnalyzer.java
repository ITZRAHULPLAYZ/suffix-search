import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Random;

public class PerformanceAnalyzer {

    private static final String   ALPHABET       = "abcdefghijklmnopqrstuvwxyz ";
    private static final String[] BENCH_PATTERNS = { "a", "the", "ab", "zz", "ba" };
    private static final int      WARMUP_REPS    = 2;
    private static final int      MEASURE_REPS   = 3;

    public void run(int[] sizes) {
        printHeader();
        for (int size : sizes) printRow(benchmark(size));
        printFooter();
        printNotes();
    }

    private BenchRecord benchmark(int size) {
        for (int w = 0; w < WARMUP_REPS; w++) new SuffixArray(generateText(size));

        long totalBuild = 0, totalSearch = 0, totalBrute = 0;
        long memBefore  = 0, memAfter   = 0;

        for (int r = 0; r < MEASURE_REPS; r++) {
            String text = generateText(size);

            System.gc();
            memBefore += usedHeapBytes();

            long t0     = System.nanoTime();
            SuffixArray sa = new SuffixArray(text);
            totalBuild += System.nanoTime() - t0;
            memAfter   += usedHeapBytes();

            SearchEngine engine = new SearchEngine(sa);

            long searchNs = 0, bruteNs = 0;
            for (String pat : BENCH_PATTERNS) {
                long s = System.nanoTime(); engine.search(pat);      searchNs += System.nanoTime() - s;
                bruteNs += engine.bruteForceTime(pat);
            }
            totalSearch += searchNs;
            totalBrute  += bruteNs;
        }

        BenchRecord rec = new BenchRecord(size);
        rec.buildNs     = totalBuild  / MEASURE_REPS;
        rec.searchNs    = totalSearch / MEASURE_REPS;
        rec.bruteNs     = totalBrute  / MEASURE_REPS;
        rec.memBeforeKB = (memBefore  / MEASURE_REPS) / 1024;
        rec.memAfterKB  = (memAfter   / MEASURE_REPS) / 1024;
        rec.memDeltaKB  = rec.memAfterKB - rec.memBeforeKB;
        return rec;
    }

    private String generateText(int length) {
        Random rng = new Random(42); // fixed seed for reproducibility
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(ALPHABET.charAt(rng.nextInt(ALPHABET.length())));
        return sb.toString();
    }

    private long usedHeapBytes() {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return heap.getUsed();
    }

    private void printHeader() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                             PERFORMANCE ANALYSIS  –  SuffixSearch                                   ║");
        System.out.println("╠═══════════════╦═══════════════╦══════════════╦══════════════╦═══════════╦═══════════╦═══════════════╣");
        System.out.printf( "║  %-13s║  %-13s║  %-12s║  %-12s║  %-9s║  %-9s║  %-13s║%n",
                "Text Size", "Build (SA)", "BinSrch (5q)", "Brute (5q)", "Speedup", "Mem Δ KB", "SA Heap KB");
        System.out.println("╠═══════════════╬═══════════════╬══════════════╬══════════════╬═══════════╬═══════════╬═══════════════╣");
    }

    private void printRow(BenchRecord r) {
        double speedup = r.bruteNs > 0 ? (double) r.bruteNs / r.searchNs : 0;
        System.out.printf("║  %-13s║  %-13s║  %-12s║  %-12s║  %-9s║  %-9d║  %-13d║%n",
                fmtSize(r.textSize), fmtNs(r.buildNs), fmtNs(r.searchNs),
                fmtNs(r.bruteNs), String.format("%.1fx", speedup), r.memDeltaKB, r.memAfterKB);
    }

    private void printFooter() {
        System.out.println("╚═══════════════╩═══════════════╩══════════════╩══════════════╩═══════════╩═══════════╩═══════════════╝");
    }

    private void printNotes() {
        System.out.println();
        System.out.println("Notes:");
        System.out.println("  • Build (SA)   – time to construct suffix array + LCP array");
        System.out.println("  • BinSrch (5q) – total binary-search time for " + BENCH_PATTERNS.length + " patterns");
        System.out.println("  • Brute (5q)   – total String.indexOf time for the same patterns");
        System.out.println("  • Speedup      – Brute / BinSrch  (>1x means SA is faster per query)");
        System.out.println("  • Mem Δ KB     – heap growth from SA construction (avg " + MEASURE_REPS + " runs)");
        System.out.println("  • Timings averaged over " + MEASURE_REPS + " runs after " + WARMUP_REPS + " JIT warm-up runs.");
        System.out.println();
    }

    private static String fmtSize(int n) {
        if (n >= 1_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n >= 1_000)     return n / 1_000 + "K";
        return String.valueOf(n);
    }

    private static String fmtNs(long ns) {
        if (ns < 1_000)         return ns + " ns";
        if (ns < 1_000_000)     return String.format("%.2f µs", ns / 1_000.0);
        if (ns < 1_000_000_000) return String.format("%.2f ms", ns / 1_000_000.0);
        return String.format("%.2f s", ns / 1_000_000_000.0);
    }

    private static class BenchRecord {
        int  textSize;
        long buildNs, searchNs, bruteNs, memBeforeKB, memAfterKB, memDeltaKB;
        BenchRecord(int size) { this.textSize = size; }
    }
}
