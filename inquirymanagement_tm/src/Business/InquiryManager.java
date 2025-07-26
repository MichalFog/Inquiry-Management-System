package Business;

import Data.*;
import Data.InquiryException;
import HandleStoreFiles.HandleFiles;
import clientServer.StatusInquiry;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.*;
import java.time.Month;
import java.util.*;

import static Business.RepresentativeManager.availableRepresentatives;

public class InquiryManager extends Thread {

    public final static Queue<Inquiry> queue;
    public final static Queue<Inquiry> allInquiries = new ConcurrentLinkedQueue<>();
    public static Queue<Inquiry> inquiriesAtHandling = new ConcurrentLinkedQueue<>();
    private Inquiry tempInquiry;
    private static InquiryManager instance;
    public static Integer nextCodeVal = 0;
    public static List<Representative> representativesList = new LinkedList<>();
    private InquiryToRepresentativeProcess inquiryToRepresentativeProcess;
    static HandleFiles handleFiles = new HandleFiles();
    static List<File> historyFiles = HandleFiles.getAllHistoryCsvFiles("InquiryHistory");


    private InquiryManager() {
    }

    static {
        queue = new ConcurrentLinkedQueue<>();
        representativesList = new LinkedList<>();
        loadInquiries();
    }

    private static void loadInquiries() {
        HandleFiles handleFiles = new HandleFiles();
        String[] folders = {"Question", "Request", "Complaint", "Representative"};
        for (String folderName : folders) {
            File folder = new File(folderName);
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        try {
                            Object inquiry = handleFiles.readCsv(file.toString());
                            if (inquiry != null) {
                                if (inquiry.getClass().getSimpleName().equals("Representative")) {
                                    representativesList.add((Representative) inquiry);
                                } else {
                                    queue.add((Inquiry) inquiry);
                                    setNextCodeVal(nextCodeVal + 1);
                                    handleFiles. writeCaseNumber(nextCodeVal + 1);

                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InquiryException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static synchronized InquiryManager getInstance() {
        if (instance == null) instance = new InquiryManager();
        return instance;
    }

    public void startInquiryProcessing() {

        if (inquiryToRepresentativeProcess == null) {
            inquiryToRepresentativeProcess = new InquiryToRepresentativeProcess();
            inquiryToRepresentativeProcess.start();
        }
    }

    public static Integer getNextCodeVal() {
        return nextCodeVal;
    }

    public static void setNextCodeVal(Integer nextCodeVal) {
        InquiryManager.nextCodeVal = nextCodeVal;
    }

    public void processInquiryManager() throws InterruptedException {
        while (!queue.isEmpty()) {
            Inquiry inquiry = queue.poll();
            inquiry.handling();
            allInquiries.add(inquiry);
        }
    }

    // Saves inquiry to CSV with assigned representative and initial "open" status.
    public Inquiry registerInquiry(List<Object> params) throws IOException, InterruptedException {
        if (params != null && !params.isEmpty() && params.get(0) instanceof Inquiry) {
            Inquiry inquiry = (Inquiry) params.get(0);
            inquiry.setCode(getNextCodeVal());
            handleFiles. writeCaseNumber(nextCodeVal + 1);
            inquiry.handling();
            allInquiries.add(inquiry);
            inquiry.setStatusInquiry(StatusInquiry.OPEN);
            inquiry.setRepresentativeCode(" ");
            saveInquiryToCsv(inquiry);
            setNextCodeVal(nextCodeVal+1);
            return inquiry;
        }
        return null;
    }

    public static Representative findRepresentativeByName(String name) {
        for (Representative rep : representativesList) {
            if (rep.getFirstName().equals(name)) {
                return rep;
            }
        }
        return null;
    }

    public static Representative findRepresentativeByCode(String code) {
        for (Representative rep : representativesList) {
            if (rep.getCode().equals(code)) {
                return rep;
            }
        }
        return null;
    }

    // Get Inquiries By Representative
    public static List<Inquiry> getInquiriesByRepresentative(String name) {
        Representative rep = findRepresentativeByName(name);
        if (rep == null) {
            return Collections.emptyList();
        }
        String representativeCode = rep.getCode();
        List<Inquiry> inquiryOfRepresentative = new ArrayList<>();
        for (Inquiry inquiry : allInquiries) {
            if (inquiry.getRepresentativeCode().equals(representativeCode)) {
                inquiryOfRepresentative.add(inquiry);
            }
        }

        for (File file : historyFiles) {
            try {
                Object obj = handleFiles.readCsv(file.toString());
                if (obj instanceof Inquiry) {
                    Inquiry inquiry = (Inquiry) obj;
                    if (inquiry.getRepresentativeCode().equals(representativeCode)) {
                        inquiryOfRepresentative.add(inquiry);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inquiryOfRepresentative;
    }

    public static String getNameRepresentativeByInquiry(Integer inquiryCode) throws IOException, InquiryException {
        String representativeCode = null;
        for (Inquiry inquiry : allInquiries) {
            if (inquiry.getCode().equals(inquiryCode)) {
                representativeCode = inquiry.getRepresentativeCode();
                break;
            }
        }
        if (representativeCode == null) {
            for (File file : historyFiles) {
                Object inquiriesFromFile = handleFiles.readCsv(file.toString());
                if (inquiriesFromFile instanceof Inquiry) {
                    Inquiry inquiry = (Inquiry) inquiriesFromFile;
                    if (inquiry.getCode().equals(inquiryCode)) {
                        representativeCode = inquiry.getRepresentativeCode();
                        break;
                    }
                }
                if (representativeCode != null) break;
            }
        }
        if (representativeCode == null) return "not exist Representative to this inquiry";
        Representative rep = findRepresentativeByCode(representativeCode);
        return (rep != null) ? rep.getFirstName() : "not exist Representative to this inquiry";
    }

    public static String getStatusInquiry(Integer code) {
        for (Inquiry inquiry : allInquiries) {
            if (inquiry.getCode().equals(code)) {
                return inquiry.getStatusInquiry().toString();
            }
        }

        for (File file : historyFiles) {
            try {
                Object obj = handleFiles.readCsv(file.toString());
                if (obj instanceof Inquiry) {
                    Inquiry inquiry = (Inquiry) obj;
                    if (inquiry.getCode().equals(code)) {
                        return inquiry.getStatusInquiry().toString();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "no such inquiry";
    }


    public static int getAmountOfInquiryInMonth(Month month) {
        int amount = 0;
        for (Inquiry inquiry : allInquiries) {
            if (inquiry.getCreationDate().getMonth() == month) amount++;
        }

        for (File file : historyFiles) {
            try {
                Object obj = handleFiles.readCsv(file.toString());
                if (obj instanceof Inquiry) {
                    Inquiry inquiry = (Inquiry) obj;
                    if (inquiry.getCreationDate().getMonth() == month) {
                        amount++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return amount;
    }

    public void cancelInquiry(Integer codeInquiryToCancel) throws IOException, InterruptedException {
        Iterator<Inquiry> iterator = allInquiries.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            Inquiry inquiry = iterator.next();
            if (inquiry.getCode().equals(codeInquiryToCancel)) {
                inquiry.setStatusInquiry(StatusInquiry.CANCEL);
                saveInquiryToCsv(inquiry);
                inquiry.handling();
                inquiry.setStatusInquiry(StatusInquiry.INHISTORY);
                saveInquiryToCsv(inquiry);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Inquiry with code " + codeInquiryToCancel + " not found. maybe it's already canceled");
        }
    }

    public String createHistoryFile(Inquiry inquiry) throws IOException {
        String relativePathFromHandler = handleFiles.getDirectoryPath(inquiry);
        String fullRelativePath = "InquiryHistory" + File.separator + relativePathFromHandler;
        File file = new File(fullRelativePath);
        file.getParentFile().mkdirs();
        return fullRelativePath;
    }

    private void saveInquiryToCsv(Inquiry inquiry) throws IOException {
        String pathFile = handleFiles.getDirectoryPath(inquiry);
        handleFiles.saveCSV(inquiry, pathFile);
    }

    // connect inquiry to an available representative.
    public void inquiryToRepresentative() throws IOException {
        Inquiry inquiry = allInquiries.poll();
        if (inquiry == null) return;
        Representative representative = availableRepresentatives.poll();
        if (representative == null) return;
        inquiry.setRepresentativeCode(representative.getCode());
        inquiry.setStatusInquiry(StatusInquiry.HANDLING);
        inquiriesAtHandling.add(inquiry);
        saveInquiryToCsv(inquiry);
        availableRepresentatives.add(representative);
    }

    public void createInquiry(int code) throws IOException {
        switch (code) {
            case 1:
                tempInquiry = new Question().fillDataByUser();
                queue.add(tempInquiry);
                setNextCodeVal(nextCodeVal + 1);
                handleFiles. writeCaseNumber(nextCodeVal + 1);
                break;
            case 2:
                tempInquiry = new Request().fillDataByUser();
                queue.add(tempInquiry);
                setNextCodeVal(nextCodeVal + 1);
                handleFiles. writeCaseNumber(nextCodeVal + 1);
                break;
            case 3:
                tempInquiry = new Complaint().fillDataByUser();
                queue.add(tempInquiry);
                setNextCodeVal(nextCodeVal + 1);
                handleFiles. writeCaseNumber(nextCodeVal + 1);
                break;
            default:
                System.out.println("the isn't such a inquiry type ");
                break;
        }
        String pathFile = handleFiles.getDirectoryPath(tempInquiry);
        handleFiles.saveCSV(tempInquiry, pathFile);
    }

    public void inquiryCreation() throws IOException {
        System.out.println("enter 1 for Question , 2 for Request , 3 for Complaint, 4 for exit");
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        while (number != 4) {
            createInquiry(number);
            System.out.println("enter 1 for Question , 2 for Request , 3 for Complaint , 4 for exit");
            scanner = new Scanner(System.in);
            number = scanner.nextInt();
        }
    }

}

//    public void defineRepresentative() throws IOException {
//        System.out.println("enter 0 for enter  or -1  for exit ");
//        Scanner scanner = new Scanner(System.in);
//        int enter = scanner.nextInt();
//
//        while (enter != -1) {
//            System.out.println("enter yor name ");
//            scanner = new Scanner(System.in);
//            String nameAgent = scanner.nextLine();
//            System.out.println("enter Id ");
//            scanner = new Scanner(System.in);
//            String ID = scanner.nextLine();
//            Representative newRepresentative = new Representative(nameAgent, ID);
//            representativesList.add(newRepresentative);
////            File dir = new File("Representative");
////            dir.mkdir();
////            File file = new File("../Representative" + newRepresentative.getCode() + ".csv");
////            file.createNewFile();
//            handleFiles.saveCSV(newRepresentative, "Representative/" + newRepresentative.getCode() + ".csv");
//            System.out.println("enter 0 for enter  or -1  for exit ");
//            scanner = new Scanner(System.in);
//            enter = scanner.nextInt();
//        }
//
//    }



