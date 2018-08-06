package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static com.oktaice.scim.model.ScimPatchOp.SCHEMA_PATCH_OP;

public class ScimGroupPatchOp extends ScimResource {

    @JsonProperty("Operations")
    private List<Operation> operations = new ArrayList<>();

    // sets patch op schema
    public ScimGroupPatchOp() {
        getSchemas().add(SCHEMA_PATCH_OP);
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public static class Operation {

        public static final String OPERATION_REPLACE = "replace";
        public static final String OPERATION_ADD = "add";

        private String op;
        private String path;

        @JsonProperty("value")
        List<Value> values = new ArrayList<>();

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

        public List<Value> getValues() {
            return values;
        }

        public void setValues(List<Value> values) {
            this.values = values;
        }

        public static class Value {

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

    }
}
