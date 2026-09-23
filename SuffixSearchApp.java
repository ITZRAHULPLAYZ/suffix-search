import java.util.Scanner;

public class SuffixSearchApp {

    private static final String DEMO_TEXT = "banana";

    private static final String[] DEMO_PATTERNS = { "an", "ana", "nan", "ban", "a", "nana", "xyz" };

    private static final String DEFAULT_TEXT =
            "The quick brown fox jumps over the lazy dog. " +
            "Pack my box with five dozen liquor jugs. " +
            "How vexingly quick daft zebras jump! " +
            "The five boxing wizards jump quickly.";

    private static final int[] BENCH_SIZES = { 500, 1_000, 5_000, 10_000, 50_000, 100_000, 500_000 };

    public static void main(String[] args) {
        banner();
        phase1();
        phase2();
        phase3();
        System.out.println();
        System.out.println("════════════════════════════════════════════════");
        System.out.println("  SuffixSearch session complete. Goodbye!");
        System.out.println("════════════════════════════════════════════════");
    }

    private static void phase1() {
        section("PHASE 1 – Suffix Array Demo  (text = \"" + DEMO_TEXT + "\")");

        long t0 = System.nanoTime();
        SuffixArray sa = new SuffixArray(DEMO_TEXT);
        System.out.printf("  Constructed in %.3f µs%n%n", (System.nanoTime() - t0) / 1_000.0);

        System.out.println("Suffix Array:");
        sa.printSuffixArray(40);
        System.out.println();

        System.out.println("LCP Array:");
        sa.printLCPArray(sa.length());
        System.out.println();

        System.out.println("Demo queries:");
        System.out.println();
        SearchEngine engine = new SearchEngine(sa);
        for (String pat : DEMO_PATTERNS) {
            engine.search(pat).print(10);
            System.out.println();
        }
    }

    private static void phase2() {
        section("PHASE 2 – Interactive Search Session");

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter text to index (ENTER for default sample):");
        System.out.print("  > ");
        System.out.flush();

        String text = sc.hasNextLine() ? sc.nextLine().trim() : "";
        if (text.isEmpty()) {
            text = DEFAULT_TEXT;
            System.out.println("  (using default sample text)");
        }
        System.out.println();

        long t0 = System.nanoTime();
        SuffixArray sa = new SuffixArray(text);
        System.out.printf("  Suffix array built in %.3f ms  (%d chars)%n%n",
                (System.nanoTime() - t0) / 1_000_000.0, text.length());

        if (text.length() <= 200) {
            System.out.println("Suffix Array:");
            sa.printSuffixArray(50);
            System.out.println();
        }

        SearchEngine engine = new SearchEngine(sa);
        System.out.println("Enter patterns to search (q / quit to exit):");
        while (sc.hasNextLine()) {
            System.out.print("  Pattern> ");
            System.out.flush();
            String pat = sc.nextLine().trim();
            if (pat.equalsIgnoreCase("quit") || pat.equalsIgnoreCase("q")) break;
            if (pat.isEmpty()) continue;
            System.out.println();
            try { engine.search(pat).print(5); }
            catch (IllegalArgumentException e) { System.out.println("  Error: " + e.getMessage()); }
            System.out.println();
        }
    }

    private static void phase3() {
        section("PHASE 3 – Performance Benchmarks");
        System.out.println("Sizes: 500 → 500 000 chars  (this may take a few seconds…)");
        System.out.println();
        new PerformanceAnalyzer().run(BENCH_SIZES);
    }

    private static void banner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                          ║");
        System.out.println("║          ███████╗██╗   ██╗███████╗███████╗██╗██╗  ██╗                   ║");
        System.out.println("║          ██╔════╝██║   ██║██╔════╝██╔════╝██║╚██╗██╔╝                   ║");
        System.out.println("║          ███████╗██║   ██║█████╗  █████╗  ██║ ╚███╔╝                    ║");
        System.out.println("║          ╚════██║██║   ██║██╔══╝  ██╔══╝  ██║ ██╔██╗                    ║");
        System.out.println("║          ███████║╚██████╔╝██║     ██║     ██║██╔╝ ██╗                   ║");
        System.out.println("║          ╚══════╝ ╚═════╝ ╚═╝     ╚═╝     ╚═╝╚═╝  ╚═╝                   ║");
        System.out.println("║                                                                          ║");
        System.out.println("║          ███████╗███████╗ █████╗ ██████╗  ██████╗██╗  ██╗               ║");
        System.out.println("║          ██╔════╝██╔════╝██╔══██╗██╔══██╗██╔════╝██║  ██║               ║");
        System.out.println("║          ███████╗█████╗  ███████║██████╔╝██║     ███████║               ║");
        System.out.println("║          ╚════██║██╔══╝  ██╔══██║██╔══██╗██║     ██╔══██║               ║");
        System.out.println("║          ███████║███████╗██║  ██║██║  ██║╚██████╗██║  ██║               ║");
        System.out.println("║          ╚══════╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝               ║");
        System.out.println("║                                                                          ║");
        System.out.println("║   Efficient Substring Search via Suffix Arrays       v1.0               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("══════════════════════════════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("══════════════════════════════════════════════════════════════════");
        System.out.println();
    }
}
