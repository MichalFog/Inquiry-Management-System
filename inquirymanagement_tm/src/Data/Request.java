package Data;

import java.util.Random;

public class Request extends Inquiry {
    @Override
    public void handling() throws InterruptedException {
        Random rand = new Random();
        int estimationTime = rand.nextInt(6) + 10;
        if (Thread.currentThread().activeCount() > 10) {
            Thread.currentThread().yield();
        }
        Thread.sleep(estimationTime * 1000);
        System.out.println("type: " + this.getClass().getName() + " code: " + getCode() + " takes " + estimationTime + " seconds ");
    }

    @Override
    public String getFolderName() {
        return "Request";
    }


}
