package labpackage;


import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class HandleTime implements Callable<Boolean> {
    @Override
    public Boolean call() {
        try {
            TimeUnit.SECONDS.sleep(90);
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        return true;
    }
}
