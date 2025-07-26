package Data;

import HandleStoreFiles.ForSaving;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Complaint extends Inquiry {
    String assignedBranch;

    public String getAssignedBranch() {
        return assignedBranch;
    }

    public void setAssignedBranch(String assignedBranch) {
        this.assignedBranch = assignedBranch;
    }

    @Override
    public Inquiry fillDataByUser() {
        super.fillDataByUser();
        System.out.println("enter your assigned Branch");
        Scanner scanner = new Scanner(System.in);
        assignedBranch = scanner.nextLine();
        return this;
    }

    @Override
    public void handling() throws InterruptedException {
        Random rand = new Random();
        int estimationTime = rand.nextInt(21) + 20;
        if (Thread.currentThread().activeCount() > 10) {
            Thread.currentThread().yield();
        }
        Thread.sleep(estimationTime * 1000);
        System.out.println("type: " + this.getClass().getName() + " code: " + getCode() + " takes " + estimationTime + " seconds ");
    }

    @Override
    public String getFolderName() {
        return "Complaint";
    }

    @Override
    public String getData() {
        return className + "," +getCode()+","+ assignedBranch + "," + getDescription() + "," + getCreationDate();
    }

    @Override
    public Inquiry parseFromFile(List<String> values) {
        setClassName(values.get(0));
        setCode(Integer.valueOf(values.get(1)));
        assignedBranch = values.get(2);
        setDescription(values.get(3));
        String dateTimeString = values.get(4);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime myDateTime = LocalDateTime.parse(dateTimeString, formatter);
        this.creationDate = myDateTime;
        return this;
    }
}
