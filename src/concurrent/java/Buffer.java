import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class Buffer {
    private final List<Integer> data = new ArrayList<>();
    

    public void put(int value) throws InterruptedException{
        synchronized(this){
            data.add(value);
            System.out.println("Inserted: " + value + " | Buffer size: " + data.size());
        }
        
    }
    
    public int remove() throws InterruptedException{
        if (!data.isEmpty()) {
            int value;
            synchronized(this){
                value = data.remove(0);
                System.out.println("Removed: " + value + " | Buffer size: " + data.size());
            }
            
            return value;
        }
        return -1;
    }
}
