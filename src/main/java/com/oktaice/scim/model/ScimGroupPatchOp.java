package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScimGroupPatchOp extends ScimPatchOp {

    @JsonProperty("Operations")
    private List<Operation> operations = new ArrayList<>();

    // sets patch op schema
    public ScimGroupPatchOp() {
        super();
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public static class Operation {

        private String op;
        private String path;
        private Object value;

        @JsonIgnore
        private ObjectMapper mapper = new ObjectMapper();

        private List<MemberValue> memberValues = new ArrayList<>();
        private GroupValue groupValue;

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;

            if (value instanceof Map) {
                groupValue = mapper.convertValue(value, GroupValue.class);
            } else if (value instanceof List) {
                memberValues = mapper.convertValue(value, new TypeReference<List<MemberValue>>(){});
            }
        }

        public List<MemberValue> getMemberValues() {
            return memberValues;
        }

        public GroupValue getGroupValue() {
            return groupValue;
        }

        public static class MemberValue {

            private String value;
            private String display;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getDisplay() {
                return display;
            }

            public void setDisplay(String display) {
                this.display = display;
            }
        }

        public static class GroupValue {

            private String id;
            private String displayName;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }
        }

    }
}
