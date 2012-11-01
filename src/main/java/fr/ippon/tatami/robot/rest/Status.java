package fr.ippon.tatami.robot.rest;

import java.util.Date;

/**
 * A status.
 *
 * @author Julien Dubois
 */
public class Status {

    private String statusId;

    private String login;

    private String username;

    private String domain;

    private String groupId;

    private String content;

    private Date statusDate;

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        if (statusId != null ? !statusId.equals(status.statusId) : status.statusId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return statusId != null ? statusId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Status{" +
                "statusId='" + statusId + '\'' +
                ", login='" + login + '\'' +
                ", username='" + username + '\'' +
                ", domain='" + domain + '\'' +
                ", groupId='" + groupId + '\'' +
                ", content='" + content + '\'' +
                ", statusDate=" + statusDate +
                '}';
    }
}
