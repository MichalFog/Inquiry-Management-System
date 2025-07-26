package clientServer;

import java.io.Serializable;

public class ResponseData implements Serializable {
    private static final long serialVersionUID = 1L;

    ResponseStatus status;
    String message;
    Object result;

    public ResponseStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }

    public ResponseData(ResponseStatus status, String message, Object result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public ResponseData(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
