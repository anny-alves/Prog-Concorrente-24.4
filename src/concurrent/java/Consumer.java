import java.util.concurrent.Semaphore;

class Consumer {
    private final Buffer buffer;
    private final int sleepTime;
    private final int id;
    private final Semaphore prod;
    private final Semaphore cons;
    
    public Consumer(int id, Buffer buffer, int sleepTime, Semaphore prod, Semaphore cons) {
        this.id = id;
        this.buffer = buffer;
        this.sleepTime = sleepTime;
        this.prod = prod;
        this.cons = cons;
    }
    
    public void process() throws InterruptedException{
        while (true) {
            try {
            cons.acquire();
            int item = buffer.remove();
            prod.release();
            if (item == -1) break;
            System.out.println("Consumer " + id + " consumed item " + item);
            Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}