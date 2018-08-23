package com.oktaice.scim.model.scim.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScimPageFilter {

    public static final String USER_FIRST_NAME = "givenName";
    public static final String USER_LAST_NAME = "familyName";
    public static final String USER_USERNAME = "userName";
    public static final String USER_ACTIVE = "active";

    public static final String GROUP_UUID = "id";
    public static final String GROUP_NAME = "displayName";

    private Integer startIndex = 1;
    private Integer count = 100;
    private String filter = "";

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        if (startIndex > 0) {
            this.startIndex = startIndex;
        }
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        if (count > 0) {
            this.count = count;
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Matcher parseFilter() {
        String regex = "(\\w+) eq \"([^\"]*)\"";
        Pattern response = Pattern.compile(regex);
        return response.matcher(filter);
    }
}
