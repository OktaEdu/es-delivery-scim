package com.oktaice.scim.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ScimUserPatchOp extends ScimPatchOp {

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
