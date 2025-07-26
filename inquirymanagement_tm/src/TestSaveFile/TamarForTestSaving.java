package TestSaveFile;

import Data.Inquiry;
import HandleStoreFiles.ForSaving;

import java.util.List;

public class TamarForTestSaving implements ForSaving {
    String id;
    String name;

    public TamarForTestSaving(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getFolderName() {
        return getClass().getPackage().getName();
    }

    @Override
    public String getFileName() {
        return getClass().getSimpleName()+id;
    }

    @Override
    public String getData() {
        return id + "," + name;
    }

    @Override
    public Inquiry parseFromFile(List<String> values) {
     return null;
    }

}