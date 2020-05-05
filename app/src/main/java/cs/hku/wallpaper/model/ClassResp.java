package cs.hku.wallpaper.model;

import java.util.ArrayList;
import java.util.List;

public class ClassResp {
    private int status;
    private String message;
    private List<String> classify;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getClassify() {
        return classify;
    }

    public void setClassify(List<String> classify) {
        this.classify = classify;
    }
}
