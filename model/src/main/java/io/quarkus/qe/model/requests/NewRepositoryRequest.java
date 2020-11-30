package io.quarkus.qe.model.requests;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public class NewRepositoryRequest {
    @NotEmpty
    private String repoUrl;
    @NotEmpty
    private String branch;
    private List<String> labels;

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
