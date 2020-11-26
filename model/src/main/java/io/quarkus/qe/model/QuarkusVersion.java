package io.quarkus.qe.model;

public class QuarkusVersion {

    private String version;

    public QuarkusVersion() {
    }

    public QuarkusVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof QuarkusVersion))
            return false;
        QuarkusVersion other = (QuarkusVersion) obj;
        return version.equals(other.version);
    }

    @Override
    public String toString() {
        return version;
    }
}
