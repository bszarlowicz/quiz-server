package labpackage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server extends Thread {
    private static DatagramSocket socket;
    private static QuestionDatabase loadedQuestions;
    private static ThreadPoolExecutor executor;
    private static final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();
    public Server() {
        loadedQuestions = new QuestionDatabase();
        try {
            socket = new DatagramSocket(3000);
        }
        catch(Exception err) {
            err.printStackTrace();
        }
        executor = new ThreadPoolExecutor(250, 250, 5, TimeUnit.SECONDS, blockingQueue);
    }
    @Override
    public void run() {
        System.out.println("Server started!");
        while(true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                executor.submit(new CustomPacket(socket, packet, loadedQuestions.getQuestions()));
            }
            catch(Exception err) {
                err.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new Server().start();
    }
}
