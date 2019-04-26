package message;

import java.io.Serializable;

public class Message implements Serializable {

    private String content;

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
