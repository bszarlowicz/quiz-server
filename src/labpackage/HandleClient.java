package labpackage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;


public class HandleClient {
    private String playerName;
    public List<Answer> providedAnswers;
    public Question currentQuestion;
    private static ThreadPoolExecutor executor;
    private static final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();
    private Future<Boolean> timer;
    public HandleClient(String playerName) {
        timer = null;
        this.playerName = playerName;
        currentQuestion = null;
        providedAnswers = new LinkedList<>();
        executor = new ThreadPoolExecutor(25, 25, 5, TimeUnit.SECONDS, blockingQueue);
    }
    public float getScore() {
        int correct = 0;
        for (Answer providedAnswer : providedAnswers) {
            if (providedAnswer.isCorrect)
                correct++;
        }
        return correct;
    }
    public float saveScore() {
        float score = getScore();
        executor.submit(new HandleScore(playerName, score));
        return score;
    }
    public void startTimer() {
        timer = executor.submit(new HandleTime());
    }
    public boolean isTimeUp() {
        return timer.isDone();
    }
    public void writeAnswers() {
        executor.submit(new HandleAnswers(playerName, providedAnswers));
    }
}

