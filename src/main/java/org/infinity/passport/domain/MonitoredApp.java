package org.infinity.passport.domain;

import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Document(collection = "MonitoredApp")
public class MonitoredApp extends AbstractAuditableDomain implements Serializable {
    private static final long serialVersionUID = 749065942851583410L;

    @Id
    @NotNull(message = "受监控的应用名不能为空")
    private String appName;

    private List<Node> nodes;

    private ResponsiblePerson responsiblePerson;

    private HealthState healthState;

    public MonitoredApp() {
    }

    public MonitoredApp(String appName, List<Node> nodes, ResponsiblePerson responsiblePerson,
                        HealthState healthState) {
        this.appName = appName;
        this.nodes = nodes;
        this.responsiblePerson = responsiblePerson;
        this.healthState = healthState;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public ResponsiblePerson getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(ResponsiblePerson responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public HealthState getHealthState() {
        return healthState;
    }

    public void setHealthState(HealthState healthState) {
        this.healthState = healthState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoredApp that = (MonitoredApp) o;
        return Objects.equals(appName, that.appName) &&
                Objects.equals(nodes, that.nodes) &&
                Objects.equals(responsiblePerson, that.responsiblePerson) &&
                healthState == that.healthState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, nodes, responsiblePerson, healthState);
    }

    @Override
    public String toString() {
        return "MonitoredApp{" +
                "appName='" + appName + '\'' +
                ", nodes=" + nodes +
                ", responsiblePerson=" + responsiblePerson +
                ", healthState=" + healthState +
                '}';
    }
}
