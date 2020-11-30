package io.quarkus.qe.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

public class Repository {
    private Long id;
    @NotEmpty
    private String repoUrl;
    @NotEmpty
    private String branch;
    private String createdAt;
    private String updatedAt;
    private String status;
    private String name;
    private Set<QuarkusExtension> extensions = new HashSet<>();
    private QuarkusVersion globalVersion;
    private Set<String> labels = new HashSet<>();
    private List<Log> logs = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<QuarkusExtension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Set<QuarkusExtension> extensions) {
        this.extensions = extensions;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public QuarkusVersion getGlobalVersion() {
        return globalVersion;
    }

    public void setGlobalVersion(QuarkusVersion globalVersion) {
        this.globalVersion = globalVersion;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    @Transient
    public void addLog(Log log) {
        this.logs.add(log);
    }
}
