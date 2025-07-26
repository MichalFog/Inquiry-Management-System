package clientServer;

import java.io.Serializable;
import java.util.List;

public class RequestData implements Serializable
{
    private static final long serialVersionUID = 1L;
    InquiryManagerActions action;
    List <Object> parameters;
}
