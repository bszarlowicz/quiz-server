package labpackage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class HandleAnswers implements Runnable {
    private final static String path = "bazaOdpowiedzi.txt";
    private BufferedWriter writer;
    private String name;
    private List<Answer> answers;
    public HandleAnswers(String name, List<Answer> answers) {
        this.name = name;
        this.answers = answers;
    }
    @Override
    public void run() {
        synchronized (this) {
            try {
                writer = new BufferedWriter(new FileWriter(path, true));
                writer.append(name + " - ");
                for(Answer item : answers) {
                    writer.append(item.toString());
                }
                writer.append(System.lineSeparator());
                writer.close();
            }
            catch(Exception err) {
                err.printStackTrace();
            }
        }
    }
}
