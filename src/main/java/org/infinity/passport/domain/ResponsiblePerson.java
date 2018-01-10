package org.infinity.passport.domain;

import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Document(collection = "ResponsiblePerson")
public class ResponsiblePerson extends AbstractAuditableDomain implements Serializable {
    private static final long serialVersionUID = 8071238374282247219L;

    @Id
    @NotNull(message = "负责人名字不能为空")
    private String name;

    private String phoneNumber;

    @NotNull(message = "负责人邮箱不能为空")
    private String email;

    public ResponsiblePerson() {
    }

    public ResponsiblePerson(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponsiblePerson that = (ResponsiblePerson) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "ResponsiblePerson{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
