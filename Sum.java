import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.HashMap;

public class Sum {
    private static long total_sum;
    private static Semaphore multiplex;
    private static Semaphore mutex = new Semaphore(1); 
    private static HashMap<Long,List<String>> mapa = new HashMap<>();

    public static int sum(FileInputStream fis) throws IOException {
        
	int byteRead;
        int sum = 0;
        
        while ((byteRead = fis.read()) != -1) {
        	sum += byteRead;
        }

        return sum;
    }

    public static long sum(String path) throws IOException {

        Path filePath = Paths.get(path);
        if (Files.isRegularFile(filePath)) {
       	    FileInputStream fis = new FileInputStream(filePath.toString());
            return sum(fis);
        } else {
            throw new RuntimeException("Non-regular file: " + path);
        }
    }

    public static void main(String[] args) throws Exception {
        int num_threads = args.length / 2;
        if (num_threads == 0){
            num_threads = 1;
        }
        
        List<SumThread> threads = new ArrayList<>();

        multiplex = new Semaphore(num_threads);
        mutex = new Semaphore(1);

        if (args.length < 1) {
            System.err.println("Usage: java Sum filepath1 filepath2 filepathN");
            System.exit(1);
        }

	//many exceptions could be thrown here. we don't care
        for (String path : args) {
            //cria thread
            SumThread thread = new SumThread(path);
            threads.add(thread);
            
            //limita o acesso com o multiplex
            multiplex.acquire();
            thread.start();
            multiplex.release();
                  
        }

        for (SumThread t : threads){
            t.join();
        }

        System.out.println(total_sum);
        for (Long l : mapa.keySet()) {
            List<String> lista = mapa.get(l);
            if (lista.size() > 1){
                System.out.println(l);
                System.out.println(lista.toString());
            }
        }
    }

    static class SumThread extends Thread {
        private String path;
        public long sum;

        public SumThread(String path){
            this.path = path;
        }

        @Override
        public void run() {
            try{
            this.sum = sum(path);
            }
            catch (IOException e){}

            System.out.println(path + " : " + sum);
            try{

            mutex.acquire();

            total_sum += this.sum;
            
            List<String> old = mapa.get(this.sum);
            if (old == null){
                ArrayList<String> list = new ArrayList<>();
                list.add(path);
                mapa.put(this.sum, list);
            }
            else{
                old.add(path);
                mapa.put(this.sum, old);
            }

            mutex.release();
            } catch (InterruptedException e){}

        }

    }
}


