import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Xiaohong on 2014/5/10.
 */
public class TestOrmlite {
    public static void main(String[] args) {
        DateTime trafficTrigger = DateTime.now().plusHours(1)
                .withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
                .minusSeconds(1);
        System.out.println(trafficTrigger);

        System.out.println(new Duration(DateTime.now().plusMillis(10), DateTime.now()).getMillis());

        System.out.println("46002".substring(0, 3));
        System.out.println("46002".substring(3));

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            final int ti = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("执行" + ti + "正在睡眠");
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println("执行" + ti + "完成");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
    }
}
