package org.infinity.passport.domain;

import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Objects;

@Document(collection = "Node")
public class Node extends AbstractAuditableDomain implements Serializable {
    private static final long serialVersionUID = 6870791787609602812L;

    private String serverAddress;

    private int port;

    private String healthContextPath;

    private HealthState healthState;

    public Node() {
    }

    public Node(String serverAddress, int port, String healthContextPath) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.healthContextPath = healthContextPath;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHealthContextPath() {
        return healthContextPath;
    }

    public void setHealthContextPath(String healthContextPath) {
        this.healthContextPath = healthContextPath;
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
        Node node = (Node) o;
        return port == node.port &&
                Objects.equals(serverAddress, node.serverAddress) &&
                Objects.equals(healthContextPath, node.healthContextPath) &&
                Objects.equals(healthState, node.healthState);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serverAddress, port, healthContextPath, healthState);
    }

    @Override
    public String toString() {
        return "Node{" +
                "serverAddress='" + serverAddress + '\'' +
                ", port=" + port +
                ", healthContextPath='" + healthContextPath + '\'' +
                ", healthState=" + healthState +
                '}';
    }
}
