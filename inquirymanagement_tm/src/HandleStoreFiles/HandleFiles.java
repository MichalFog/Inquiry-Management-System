package HandleStoreFiles;

import Data.*;
import clientServer.StatusInquiry;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class HandleFiles {
    private static final String FILE_PATH = "case_number.txt";

    public void saveFile(ForSaving forSaving) throws IOException, InquiryRunTimeException {
        File dir = new File(forSaving.getFolderName());
        dir.mkdir();
        File file = new File(getDirectoryPath(forSaving));
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new InquiryRunTimeException(0, "Failed to create new file: " + e.getMessage());
        }
        OutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        outputStreamWriter.write(forSaving.getData());
        outputStreamWriter.flush();
    }


    public void deleteFile(ForSaving forSaving) {

        File f = new File(forSaving.getFileName());
        f.delete();
    }

    public void updateFile(ForSaving forSaving) throws IOException {
        saveFile(forSaving);
    }

    private String getFileName(ForSaving forSaving) throws IOException {
        return forSaving.getFileName();
    }

    public String getDirectoryPath(ForSaving forSaving) throws IOException {
        Class clazz = forSaving.getClass();
        String className = clazz.getSimpleName().toLowerCase();
        char type = className.charAt(0);
        File dir = new File(forSaving.getFolderName());
        dir.mkdir();
        return  forSaving.getFolderName()+ "\\" + type + "." + forSaving.getFileName() + ".csv";
    }

    public void saveFiles(List<ForSaving> forSavingList) throws IOException {
        for (ForSaving i : forSavingList) {
            saveFile(i);
        }
    }

    public ForSaving readFile(File f) throws IOException, InquiryException {
        List<String> data = new ArrayList<>();
        String line;

        try (InputStream inputStream = new FileInputStream(f);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                for (String part : parts) {
                    data.add(part.trim());
                }
            }
        }
        if (data.isEmpty()) {
            throw new InquiryException(0, "Empty CSV file.");
        }
        String inquiry = "Data." + data.get(0);
        try {
            Class<?> clazz = Class.forName(inquiry);
            ForSaving instance = (ForSaving) clazz.getDeclaredConstructor().newInstance();
            return instance.parseFromFile(data);
        } catch (ClassNotFoundException e) {
            System.out.println("Unknown inquiry type: " + inquiry);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCSVDataRecursive(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        List<Field> fields = getAllFields(obj.getClass());
        stringBuilder.append(obj.getClass().getSimpleName()).append(',');
        fields.sort(Comparator.comparing(field -> {
            String fieldName = field.getName();
            if (fieldName.equals("className")) return 0;
            if (fieldName.equals("code")) return 1;
            if (fieldName.equals("assignedBranch")) return 2;
            if (fieldName.equals("description")) return 3;
            if (fieldName.equals("creationDate")) return 4;
            if(fieldName.equals("statusInquiry")) return 5;
            if(fieldName.equals("representativeCode")) return 6;
            return 7;
        }));


        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && !field.getName().equals("className")) {
                field.setAccessible(true);
                Object value = field.get(obj);
                stringBuilder.append(value.toString()).append(',');
            }
        }

        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

public boolean saveCSV(Object obj, String filePath) throws InquiryRunTimeException {
    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath))) {
        String csvData = getCSVDataRecursive(obj);
        bufferedOutputStream.write(csvData.getBytes());
        bufferedOutputStream.flush();
        return true;
    } catch (Exception e) {
        throw new InquiryRunTimeException(0, "Failed to save CSV file: " + e.getMessage());
    }
}


    public Object readCsv(String filePath) throws IOException, InquiryException {
        List<String> data = new ArrayList<>();
        String line;

        try (InputStream inputStream = new FileInputStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                for (String part : parts) {
                    data.add(part.trim());
                }
            }
        }
        if (data.isEmpty()) {
            throw new InquiryException(0, "Empty CSV file.");
        }

        String className = "Data." + data.get(0);
        try {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();

            if (instance instanceof Inquiry) {
                Inquiry inquiry = (Inquiry) instance;
                inquiry.setClassName(data.get(0));
                inquiry.setCode(Integer.parseInt(data.get(1)));
                if (instance instanceof Complaint) {
                    Complaint complaint = (Complaint) instance;
                    complaint.setAssignedBranch(data.get(2));
                    complaint.setDescription(data.get(3));
                    complaint.setCreationDate(LocalDateTime.parse(data.get(4)));
                    complaint.setStatusInquiry(StatusInquiry.valueOf(data.get(5)));
                    complaint.setRepresentativeCode(data.get(6));

                } else {
                    inquiry.setDescription(data.get(2));
                    inquiry.setCreationDate(LocalDateTime.parse(data.get(3)));
                    inquiry.setStatusInquiry(StatusInquiry.valueOf(data.get(4)));
                    inquiry.setRepresentativeCode(data.get(5));
                }

            }
            else if (instance instanceof Representative) {
                Representative representative = (Representative) instance;
                representative.setCode(data.get(1));
                representative.setFirstName(data.get(2));
                representative.setTz(data.get(3));
            }
            return instance;

        } catch (ClassNotFoundException e) {
            System.out.println("Unknown inquiry type: " + className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<File> getAllHistoryCsvFiles(String historyFolderPath) {
        List<File> csvFiles = new ArrayList<>();
        File historyDir = new File(historyFolderPath);
        File[] subDirs = historyDir.listFiles(File::isDirectory); // תתי תיקיות בלבד

        if (subDirs != null) {
            for (File subDir : subDirs) {
                File[] files = subDir.listFiles((dir, name) -> name.endsWith(".csv"));
                if (files != null) {
                    csvFiles.addAll(Arrays.asList(files));
                }
            }
        }

        return csvFiles;
    }


//החזרת מספר פניה
    public static int readCaseNumber() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();
            return line != null ? Integer.parseInt(line) : 0;
        } catch (IOException | NumberFormatException e) {
            return 0; // אם אין קובץ או שיש בעיה בקריאה, מחזיר 0
        }
    }

    public static void writeCaseNumber(int newNumber) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(Integer.toString(newNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


