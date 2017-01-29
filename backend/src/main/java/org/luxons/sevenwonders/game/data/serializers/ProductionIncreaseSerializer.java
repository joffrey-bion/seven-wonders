package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;

import org.luxons.sevenwonders.game.effects.ProductionIncrease;
import org.luxons.sevenwonders.game.resources.Production;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ProductionIncreaseSerializer
        implements JsonSerializer<ProductionIncrease>, JsonDeserializer<ProductionIncrease> {

    @Override
    public JsonElement serialize(ProductionIncrease productionIncrease, Type typeOfSrc,
            JsonSerializationContext context) {
        Production production = productionIncrease.getProduction();
        JsonElement json = context.serialize(production);
        if (!json.isJsonNull() && !productionIncrease.isSellable()) {
            return new JsonPrimitive(wrapInBrackets(json.getAsString()));
        }
        return json;
    }

    private String wrapInBrackets(String str) {
        return '(' + str + ')';
    }

    @Override
    public ProductionIncrease deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        ProductionIncrease productionIncrease = new ProductionIncrease();

        String resourcesStr = json.getAsString();
        boolean isSellable = !resourcesStr.startsWith("(");
        if (!isSellable) {
            resourcesStr = unwrapBrackets(resourcesStr);
            json = new JsonPrimitive(resourcesStr);
        }
        productionIncrease.setSellable(isSellable);

        Production production = context.deserialize(json, Production.class);
        productionIncrease.setProduction(production);
        return productionIncrease;
    }

    private static String unwrapBrackets(String str) {
        return str.substring(1, str.length() - 1);
    }
}
