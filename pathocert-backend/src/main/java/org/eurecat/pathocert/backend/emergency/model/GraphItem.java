package org.eurecat.pathocert.backend.emergency.model;

import java.util.Objects;

public class GraphItem {
    public String group;
    public String value;

    public String label;

    public String source;
    public String target;

    public GraphItem(String group, String data1, String data2) {
        this.group = group;
        if (Objects.equals(group, "nodes")) {
            this.value = data1;
            this.label = data2;
            this.source = "";
            this.target = "";
        } else {
            this.value = "";
            this.label = "";
            this.source = data1;
            this.target = data2;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GraphItem)) {
            return false;
        }
        GraphItem other = (GraphItem) obj;
        return this.group.equals(other.group) &&
                this.value.equals(other.value) &&
                this.label.equals(other.label) &&
                this.source.equals(other.source) &&
                this.target.equals(other.target);
    }

    @Override
    public String toString() {
        return this.group + ":" + this.value + "," + this.label + "," + this.source + "," + this.target + ",";
    }
}
