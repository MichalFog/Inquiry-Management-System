package clientServer;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class RequestData implements Serializable {
    private static final long serialVersionUID = 1L;;
    public InquiryManagerActions action;
    public List<Object> parameters;

    public RequestData(InquiryManagerActions action, List<Object> parameters) {
        this.action = action;
        this.parameters = parameters;
    }

    public RequestData(InquiryManagerActions action) {
        this(action, new ArrayList());
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }
}
