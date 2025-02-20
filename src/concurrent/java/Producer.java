import java.util.concurrent.Semaphore;

class Producer {
    private final Buffer buffer;
    private final int maxItems;
    private final int sleepTime;
    private final int id;
    private final Semaphore prod;
    private final Semaphore cons;
    
    public Producer(int id, Buffer buffer, int maxItems, int sleepTime, Semaphore prod, Semaphore cons) {
        this.id = id;
        this.buffer = buffer;
        this.maxItems = maxItems;
        this.sleepTime = sleepTime;
        this.prod = prod;
        this.cons = cons;
    }
    
    public void produce() throws InterruptedException{     
        for (int i = 0; i < maxItems; i++) {
            try {
                prod.acquire();
                Thread.sleep(sleepTime);
                int item = (int) (Math.random() * 100);
                System.out.println("Producer " + id + " produced item " + item);
                buffer.put(item);
                cons.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
