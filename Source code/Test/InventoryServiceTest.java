package test;
import model.Product;
import service.OrderService;
import service.ProductService;
import util.InventoryException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/**
 * Manual test scenarios exercising the service layer, including a
 * concurrency test that fires several simultaneous stock-out orders
 * at a single product using an ExecutorService, to demonstrate that
 * the lock in OrderService prevents the quantity from going negative.
 * Run with: java -cp bin test.InventoryServiceTest
 */
public class InventoryServiceTest {
    static ProductService productService = new ProductService();
    static OrderService orderService = new OrderService();
    public static void main(String[] args) throws Exception {
        test("Add valid product", () -> productService.addProduct(new Product("Test Item", "Misc", 50, 10.0, 5)));
        test("Reject negative quantity", () -> {
            try {
                productService.addProduct(new Product("Bad Item", "Misc", -5, 10.0, 5));
                throw new RuntimeException("Expected exception");
            } catch (InventoryException ok) { }
        });

        test("Reject stock-out exceeding quantity", () -> {
            productService.addProduct(new Product("Limited Item", "Misc", 5, 20.0, 2));
            List<Product> all = productService.getAllProducts();
            int id = all.get(all.size() - 1).getId();
            try {
                orderService.processOrder(id, 100, "OUT");
                throw new RuntimeException("Expected exception");
            } catch (InventoryException ok) { }
        });
        testConcurrentOrders();
        System.out.println("All tests completed.");
    }
    /**
     * Creates one product with limited stock, then launches multiple threads
     * that each try to take out more than their fair share at the same time.
     * Without the lock in OrderService, this could push quantity negative;
     * with it, only as many OUT orders succeed as stock allows and the rest
     * correctly throw InsufficientStockException.
     */
    static void testConcurrentOrders() throws Exception {
        productService.addProduct(new Product("Concurrent Item", "Misc", 10, 15.0, 2));
        List<Product> all = productService.getAllProducts();
        int id = all.get(all.size() - 1).getId();
        int threadCount = 5;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    orderService.processOrder(id, 3, "OUT");
                    System.out.println("Thread " + Thread.currentThread().getId() + ": order succeeded");
                } catch (InventoryException e) {
                    System.out.println("Thread " + Thread.currentThread().getId() + ": order rejected (" + e.getMessage() + ")");
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        Product finalState = productService.getProductById(id);
        if (finalState.getQuantity() >= 0) {
            System.out.println("PASS: Concurrent orders - quantity never went negative (final qty=" + finalState.getQuantity() + ")");
        } else {
            System.out.println("FAIL: Concurrent orders - quantity went negative (final qty=" + finalState.getQuantity() + ")");
        }
    }
    interface TestCase { void run() throws Exception; }
    static void test(String name, TestCase t) {
        try {
            t.run();
            System.out.println("PASS: " + name);
        } catch (Exception e) {
            System.out.println("FAIL: " + name + " -> " + e.getMessage());
        }
    }
}
