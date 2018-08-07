package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ScimUserPatchOp extends ScimResource {

    public static final String SCHEMA_PATCH_OP = "urn:ietf:params:scim:api:messages:2.0:PatchOp";

    @JsonProperty("Operations")
    List<Operation> operations = new ArrayList<>();

    public ScimUserPatchOp() {
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

        private String op;
        private Value value;

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public Value getValue() {
            return value;
        }

        public void setValue(Value value) {
            this.value = value;
        }

        public static class Value {

            private Boolean active;

            public Boolean getActive() {
                return active;
            }

            public void setActive(Boolean active) {
                this.active = active;
            }
        }
    }
}
