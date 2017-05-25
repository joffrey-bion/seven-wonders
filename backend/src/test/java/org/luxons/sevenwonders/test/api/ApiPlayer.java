package org.luxons.sevenwonders.test.api;

import java.util.Objects;

public class ApiPlayer {

    private String username;

    private String displayName;

    private int index;

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiPlayer apiPlayer = (ApiPlayer) o;
        return index == apiPlayer.index && Objects.equals(username, apiPlayer.username) && Objects.equals(displayName,
                apiPlayer.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, displayName, index);
    }
}
