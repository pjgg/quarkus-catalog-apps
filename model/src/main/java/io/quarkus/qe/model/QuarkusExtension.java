package io.quarkus.qe.model;

import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class QuarkusExtension {
    private String name;
    private String version;

    public QuarkusExtension() {
    }

    public QuarkusExtension(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof QuarkusExtension))
            return false;
        QuarkusExtension other = (QuarkusExtension) obj;
        return StringUtils.equals(name, other.name) && StringUtils.equals(version, other.version);
    }

    @Override
    public final int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, version);
    }
}
