package cs.hku.wallpaper.model;

import java.util.List;

public class ImgResp {
    private int status;
    private String message;
    private List<String> filename;

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

    public List<String> getFilename() {
        return filename;
    }

    public void setFilename(List<String> filename) {
        this.filename = filename;
    }
}
