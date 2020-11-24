package io.quarkus.qe.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

public class Repository {
    public transient static final String UNDEFINED_VALUE = "N/A";
    private Long id;
    @NotEmpty
    private String repoUrl;
    @NotEmpty
    private String branch;
    private String name;
    private Set<QuarkusExtension> extensions = new HashSet<>();
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

    @Transient
    public void addLog(Log log) {
        this.logs.add(log);
    }
}
