package Data;

import HandleStoreFiles.ForSaving;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public abstract class Inquiry implements ForSaving, Serializable {
    protected static final long serialVersionUID = 1L;
    String className;
    private Integer code;
    private String description;
    LocalDateTime creationDate;

    public Inquiry() {
        className = this.getClass().getSimpleName();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code =code ;
    }


    public Inquiry fillDataByUser() {
//        setCode(InquiryManager.getNextCodeVal());
        System.out.println("enter your description quiry ");
        Scanner scanner = new Scanner(System.in);
        this.description = scanner.nextLine();
        this.creationDate = LocalDateTime.now();
        return this;
    }

    public abstract void handling() throws InterruptedException;

    public String getFileName() {
        return code.toString();
    }

    public String getData() {
        return className + "," + code + "," + description + "," + creationDate;
    }

    public Inquiry parseFromFile(List<String> values) {
        className = values.get(0);
        code = Integer.valueOf(values.get(1));
        this.description = values.get(2);
        String dateTimeString = values.get(3);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime myDateTime = LocalDateTime.parse(dateTimeString, formatter);
        this.creationDate = myDateTime;
        return this;
    }

}
