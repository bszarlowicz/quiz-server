package labpackage;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class HandleScore implements Runnable {
    private final static String path = "wyniki.txt";
    private BufferedWriter writer;
    private String name;
    private float score;
    public HandleScore(String name, float score) {
        this.name = name;
        this.score = score;
    }
    @Override
    public void run() {

        synchronized (this) {
            try {
                writer = new BufferedWriter(new FileWriter(path, true));
                writer.append(name + ": " + score + System.lineSeparator());
                writer.close();
            }
            catch(Exception err) {
                err.printStackTrace();
            }
        }
    }
}
