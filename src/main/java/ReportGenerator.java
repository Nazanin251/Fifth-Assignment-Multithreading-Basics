import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ReportGenerator {

    static class TaskRunnable implements Runnable {
        private final String path;

        private double grandTotal = 0;
        private int    totalQty   = 0;
        private int    discSumPct = 0;
        private int    lineCount  = 0;

        private Product mostExpensiveProduct = null;
        private double  highestUnitAfterDisc = 0;

        TaskRunnable(String path) { this.path = path; }

        @Override public void run() {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(getStream(path)))) {

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;


                    String[] parts = line.split(",");
                    if (parts.length < 3) continue;

                    int    pId      = Integer.parseInt(parts[0].trim());
                    int    qty      = Integer.parseInt(parts[1].trim());
                    int    discPct  = Integer.parseInt(parts[2].trim());

                    Product p = findProductById(pId);
                    if (p == null) continue;

                    double unitAfter = p.price * (1 - discPct / 100.0);
                    grandTotal      += unitAfter * qty;
                    totalQty        += qty;
                    discSumPct      += discPct;
                    lineCount++;


                    if (unitAfter > highestUnitAfterDisc) {
                        highestUnitAfterDisc = unitAfter;
                        mostExpensiveProduct = p;
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not read " + path + ": " + e.getMessage());
            }
        }


        void makeReport() {
            DecimalFormat money = new DecimalFormat("#,##0.00");
            DecimalFormat pct   = new DecimalFormat("0.00");

            System.out.println("===== Report for " + path + " =====");
            System.out.println("Total cost            : $" + money.format(grandTotal));
            System.out.println("Total items purchased : " + totalQty);

            if (lineCount > 0) {
                double avgDisc = (double) discSumPct / lineCount;
                System.out.println("Average discount      : " + pct.format(avgDisc) + " %");
            } else {
                System.out.println("Average discount      : n/a");
            }

            if (mostExpensiveProduct != null) {
                System.out.println("Most expensive (per unit, after discount): "
                        + mostExpensiveProduct.name + " – $" + money.format(highestUnitAfterDisc));
            }
            System.out.println();
        }
    }


    static class Product {
        final int    id;
        final String name;
        final double price;
        Product(int id, String name, double price) { this.id = id; this.name = name; this.price = price; }
    }


    private static final String[] ORDER_FILES = {
            "2021_order_details.txt",
            "2022_order_details.txt",
            "2023_order_details.txt",
            "2024_order_details.txt"
    };
    private static final List<Product> catalog = new ArrayList<>();


    private static InputStream getStream(String res) {
        return ReportGenerator.class.getClassLoader().getResourceAsStream(res);
    }
    private static Product findProductById(int id) {
        for (Product p : catalog) if (p.id == id) return p;
        return null;
    }


    private static void loadProducts() throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getStream("Products.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                int    id    = Integer.parseInt(parts[0].trim());
                String name  = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                catalog.add(new Product(id, name, price));
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {
        try {
            loadProducts();
        } catch (IOException e) {
            System.out.println("Cannot load Products.txt: " + e.getMessage());
            return;
        }

        List<TaskRunnable> workers = new ArrayList<>();
        List<Thread>       threads = new ArrayList<>();

        for (String f : ORDER_FILES) {
            TaskRunnable task = new TaskRunnable(f);
            workers.add(task);
            Thread t = new Thread(task);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) t.join();

        for (TaskRunnable t : workers) t.makeReport();
    }
}
