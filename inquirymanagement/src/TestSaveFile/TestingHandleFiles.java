package TestSaveFile;

import HandleStoreFiles.HandleFiles;

import java.util.Arrays;

public class TestingHandleFiles {
    public static void main(String[] args) throws Exception {

        TamarForTestSaving p1 = new TamarForTestSaving("1234","aaa");
        TamarForTestSaving p2 = new TamarForTestSaving("5432","bbb");
        TamarForTestSaving p3 = new TamarForTestSaving("9999","ccc");
        TamarForTestSaving p4 = new TamarForTestSaving("0090","ccdc");

        HandleFiles handleFiles = new HandleFiles();
//        handleFiles.saveFile(p3);
//        handleFiles.saveFiles(Arrays.asList(p1,p2,p3,p4));
//        handleFiles.deleteFile(p2);

        System.out.println(handleFiles.getCSVDataRecursive(p1));
       TamarForTestSaving  p5 = new TamarForTestSaving("123456789","sucess BH!");
        handleFiles.saveCSV(p5, "id");

    }
}
