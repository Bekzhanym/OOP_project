package kbtu_oop_project.domain.features.misc;

import java.io.Serializable;
import java.time.LocalDate;

public class Log implements Serializable {

    private static final long serialVersionUID = 1L;
    private LocalDate timestamp;
    private String action;
    private String userId;

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
