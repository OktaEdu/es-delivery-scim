package com.oktaice.scim.model.scim;

public class ScimPatchOp extends ScimResource {

    //The ScimPatchOp class has its own SCIM schema. The identifier is PatchOp.
    public static final String SCHEMA_PATCH_OP = "urn:ietf:params:scim:api:messages:2.0:PatchOp";
    public static final String OPERATION_REPLACE = "replace";
    public static final String OPERATION_ADD = "add";

    public ScimPatchOp() {
        getSchemas().add(SCHEMA_PATCH_OP);
    }
}
