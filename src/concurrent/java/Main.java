import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) throws InterruptedException{
        if (args.length != 5) {
            System.out.println("Use: java Main <num_producers> <max_items_per_producer> <producing_time> <num_consumers> <consuming_time>");
            return;
        }
        Semaphore producers = new Semaphore(100);
        Semaphore consumers = new Semaphore(0);
        List<Thread> threads = new ArrayList<Thread>();
        int numProducers = Integer.parseInt(args[0]);
        int maxItemsPerProducer = Integer.parseInt(args[1]);
        int producingTime = Integer.parseInt(args[2]);
        int numConsumers = Integer.parseInt(args[3]);
        int consumingTime = Integer.parseInt(args[4]);
        
        Buffer buffer = new Buffer();
        
        for (int i = 1; i <= numProducers; i++) {
            Producer producer = new Producer(i, buffer, maxItemsPerProducer, producingTime, producers, consumers);

            ProduceThread produceThread = new ProduceThread(producer);
            threads.add(produceThread);
        }
        
        for (int i = 1; i <= numConsumers; i++) {
            Consumer consumer = new Consumer(i, buffer, consumingTime, producers, consumers);
            ConsumerThread consumerThread = new ConsumerThread(consumer);
            threads.add(consumerThread);
    
        }

        for(Thread e : threads){
            e.start();
        }

        for(Thread e : threads){   
            e.join();
        }
    }

    static class ProduceThread extends Thread{
        Producer producer;
        public ProduceThread(Producer produce){
            this.producer = produce;
        }
        @Override
        public void run(){
            try{
            this.producer.produce();
            } catch (InterruptedException e){
            ConsumerThread.currentThread().interrupt();
        }
        }
    }

    static class ConsumerThread extends Thread{
        Consumer consumer;
        public ConsumerThread(Consumer consumer){
            this.consumer = consumer;
        }

        @Override
        public void run (){
            try{
            this.consumer.process();
            } catch (InterruptedException e){
                ConsumerThread.currentThread().interrupt();
            }

        }
    }
}
