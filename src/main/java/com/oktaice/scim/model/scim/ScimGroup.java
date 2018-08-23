package com.oktaice.scim.model.scim;

import java.util.ArrayList;
import java.util.List;

public class ScimGroup extends ScimResource {

    public static final String GROUP_SCHEMA = SCHEMA_BASE + ":core:2.0:Group";

    private String displayName;
    private List<Member> members = new ArrayList<>();

    public ScimGroup() {
        super();
        getSchemas().add(GROUP_SCHEMA);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public static class Member {

        private String display;
        private String value;

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
