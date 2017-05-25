package org.luxons.sevenwonders.test.api;

import java.util.List;
import java.util.Objects;

import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.cards.Requirements;

public class ApiCard {

    private String name;

    private Color color;

    private Requirements requirements;

    private String chainParent;

    private List<String> chainChildren;

    private String image;

    private ApiCardBack back;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public String getChainParent() {
        return chainParent;
    }

    public void setChainParent(String chainParent) {
        this.chainParent = chainParent;
    }

    public List<String> getChainChildren() {
        return chainChildren;
    }

    public void setChainChildren(List<String> chainChildren) {
        this.chainChildren = chainChildren;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ApiCardBack getBack() {
        return back;
    }

    public void setBack(ApiCardBack back) {
        this.back = back;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiCard apiCard = (ApiCard) o;
        return Objects.equals(name, apiCard.name) && color == apiCard.color && Objects.equals(requirements,
                apiCard.requirements) && Objects.equals(chainParent, apiCard.chainParent) && Objects.equals(
                chainChildren, apiCard.chainChildren) && Objects.equals(image, apiCard.image) && Objects.equals(back,
                apiCard.back);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, requirements, chainParent, chainChildren, image, back);
    }
}
