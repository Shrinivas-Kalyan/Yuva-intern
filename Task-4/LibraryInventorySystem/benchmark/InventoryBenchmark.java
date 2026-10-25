import com.library.inventory.BookInventory;
import java.util.Arrays;

/**
 * Times add / search / delete on N books using only the public API that both the
 * Week 3 (ArrayList) and Week 4 (LinkedHashMap) BookInventory classes share.
 * Run it once against each version's compiled classes and compare the output.
 *
 * Usage: java -cp <classes>:<this class> InventoryBenchmark
 */
public class InventoryBenchmark {

    private static final int[] SIZES = {1_000, 5_000, 20_000};
    private static final int RUNS = 7;

    public static void main(String[] args) {
        System.out.println("java.version=" + System.getProperty("java.version"));
        // Warm-up so the JIT compiler has optimised both versions before timing.
        run(2_000, new long[3]);
        run(2_000, new long[3]);

        System.out.printf("%-8s %-10s %12s%n", "N", "operation", "median ms");
        for (int n : SIZES) {
            long[][] samples = new long[3][RUNS];
            for (int r = 0; r < RUNS; r++) {
                long[] t = new long[3];
                run(n, t);
                for (int op = 0; op < 3; op++) {
                    samples[op][r] = t[op];
                }
            }
            String[] names = {"add", "search", "delete"};
            for (int op = 0; op < 3; op++) {
                Arrays.sort(samples[op]);
                System.out.printf("%-8d %-10s %12.2f%n", n, names[op],
                        samples[op][RUNS / 2] / 1_000_000.0);
            }
        }
    }

    /** Fills t[0..2] with nanoseconds for: add n books, search n books, delete n books. */
    private static void run(int n, long[] t) {
        String[] isbns = new String[n];
        for (int i = 0; i < n; i++) {
            isbns[i] = String.format("978%010d", i); // 13 digits, unique
        }
        BookInventory inventory = new BookInventory();

        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            inventory.addBook(isbns[i], "Title " + i, "Author", 2000);
        }
        t[0] = System.nanoTime() - start;

        long found = 0;
        start = System.nanoTime();
        for (int i = n - 1; i >= 0; i--) { // newest first = worst case for a linear scan
            found += inventory.findByIsbn(isbns[i]).isPresent() ? 1 : 0;
        }
        t[1] = System.nanoTime() - start;
        if (found != n) {
            throw new IllegalStateException("search failed");
        }

        start = System.nanoTime();
        for (int i = n - 1; i >= 0; i--) {
            inventory.deleteBook(isbns[i]);
        }
        t[2] = System.nanoTime() - start;
        if (!inventory.isEmpty()) {
            throw new IllegalStateException("delete failed");
        }
    }
}
