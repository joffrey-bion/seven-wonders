package org.luxons.sevenwonders.game.wonders;

import java.util.Arrays;
import java.util.List;

import org.luxons.sevenwonders.game.resources.ResourceType;

public class Wonder {

    private String name;

    private ResourceType initialResource;

    private List<WonderLevel> levels;

    private String image;

    public Wonder() {
    }

    public Wonder(String name, ResourceType initialResource, WonderLevel... levels) {
        this.initialResource = initialResource;
        this.levels = Arrays.asList(levels);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getInitialResource() {
        return initialResource;
    }

    public void setInitialResource(ResourceType initialResource) {
        this.initialResource = initialResource;
    }

    public List<WonderLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<WonderLevel> levels) {
        this.levels = levels;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
