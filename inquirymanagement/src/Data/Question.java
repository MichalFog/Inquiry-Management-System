package Data;

import java.util.Random;

public class Question extends Inquiry {

    @Override
    public void handling() throws InterruptedException {
        Random rand = new Random();
        int estimationTime = rand.nextInt(6);
        Thread.sleep(estimationTime * 1000);
        Thread.currentThread().setPriority(10);
        System.out.println("type: " + this.getClass().getName() + " code: " + getCode() + " takes " + estimationTime + " seconds ");
    }

    @Override
    public String getFolderName() {
        return "Question";
    }

}
