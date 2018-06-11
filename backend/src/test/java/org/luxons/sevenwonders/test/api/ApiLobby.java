package org.luxons.sevenwonders.test.api;

import java.util.List;
import java.util.Objects;

import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.lobby.State;

public class ApiLobby {

    private long id;

    private String name;

    private String owner;

    private List<ApiPlayer> players;

    private CustomizableSettings settings;

    private State state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<ApiPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<ApiPlayer> players) {
        this.players = players;
    }

    public CustomizableSettings getSettings() {
        return settings;
    }

    public void setSettings(CustomizableSettings settings) {
        this.settings = settings;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiLobby apiLobby = (ApiLobby) o;
        return id == apiLobby.id && Objects.equals(name, apiLobby.name) && Objects.equals(owner, apiLobby.owner)
                && Objects.equals(players, apiLobby.players) && Objects.equals(settings, apiLobby.settings)
                && state == apiLobby.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, owner, players, settings, state);
    }
}
