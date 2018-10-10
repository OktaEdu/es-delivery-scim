package com.oktaice.scim.model.scim;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ScimUserPatchOp extends ScimPatchOp {

    /**
     * The body of an HTTP PATCH request MUST contain the attribute "Operations",
     * whose value is an array of one or more PATCH operations.
     */
    @JsonProperty("Operations")
    List<Operation> operations = new ArrayList<>();

    // sets patch op schema
    public ScimUserPatchOp() {
        super();
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    /**
     * The Operation object contains the string attribute op,
     * and the value attribute that currently only used to set the active status for a user.
     */
    public static class Operation {

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
