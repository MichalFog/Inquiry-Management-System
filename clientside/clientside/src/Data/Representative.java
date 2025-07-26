package Data;

import java.util.UUID;

public class Representative {
    String code;
    String firstName;
    String tz;

    public Representative(String firstName, String tz) {
        this.code = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.tz = tz;
    }
    public Representative() {
        this.firstName = "tamar";
        this.tz = "1231";
    }

        public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}