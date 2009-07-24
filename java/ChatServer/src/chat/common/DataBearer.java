package chat.common;

import java.io.*;

public class DataBearer implements Serializable {

    public DataBearer(String data) {
        this.data = data;
    }

    public String data;
}
