package org.luxons.sevenwonders.game.resources;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProductionTest {

    private Resources emptyResources;

    private Resources resources1Wood;

    private Resources resources1Stone;

    private Resources resources1Stone1Wood;

    private Resources resources2Stones;

    private Resources resources2Stones3Clay;

    @Before
    public void init() {
        emptyResources = new Resources();

        resources1Wood = new Resources();
        resources1Wood.add(ResourceType.WOOD, 1);

        resources1Stone = new Resources();
        resources1Stone.add(ResourceType.STONE, 1);

        resources1Stone1Wood = new Resources();
        resources1Stone1Wood.add(ResourceType.STONE, 1);
        resources1Stone1Wood.add(ResourceType.WOOD, 1);

        resources2Stones = new Resources();
        resources2Stones.add(ResourceType.STONE, 2);

        resources2Stones3Clay = new Resources();
        resources2Stones3Clay.add(ResourceType.STONE, 2);
        resources2Stones3Clay.add(ResourceType.CLAY, 3);
    }

    @Test
    public void contains_newProductionContainsEmpty() {
        Production production = new Production();
        assertTrue(production.contains(emptyResources));
    }

    @Test
    public void contains_singleFixedResource_noneAtAll() {
        Production production = new Production();
        assertFalse(production.contains(resources2Stones));
    }

    @Test
    public void contains_singleFixedResource_notEnough() {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 1);
        assertFalse(production.contains(resources2Stones));
    }

    @Test
    public void contains_singleFixedResource_justEnough() {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 2);
        assertTrue(production.contains(resources2Stones));
    }

    @Test
    public void contains_singleFixedResource_moreThanEnough() {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 3);
        assertTrue(production.contains(resources2Stones));
    }

    @Test
    public void contains_singleFixedResource_moreThanEnough_amongOthers() {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 3);
        production.addFixedResource(ResourceType.CLAY, 2);
        assertTrue(production.contains(resources2Stones));
    }

    @Test
    public void contains_multipleFixedResources_notEnoughOfOne() {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 3);
        production.addFixedResource(ResourceType.CLAY, 1);
        assertFalse(production.contains(resources2Stones3Clay));
    }

    @Test
    public void contains_multipleFixedResources_notEnoughOfBoth() {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 1);
        production.addFixedResource(ResourceType.CLAY, 1);
        assertFalse(production.contains(resources2Stones3Clay));
    }

    @Test
    public void contains_multipleFixedResources_moreThanEnough() {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 3);
        production.addFixedResource(ResourceType.CLAY, 5);
        assertTrue(production.contains(resources2Stones3Clay));
    }

    @Test
    public void contains_singleChoice_containsEmpty() {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.CLAY);
        assertTrue(production.contains(emptyResources));
    }

    @Test
    public void contains_singleChoice_enough() {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        assertTrue(production.contains(resources1Wood));
        assertTrue(production.contains(resources1Stone));
    }

    @Test
    public void contains_multipleChoices_notBoth() {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.CLAY);
        production.addChoice(ResourceType.STONE, ResourceType.CLAY);
        production.addChoice(ResourceType.STONE, ResourceType.CLAY);
        assertFalse(production.contains(resources2Stones3Clay));
    }

    @Test
    public void contains_multipleChoices_enough() {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.ORE);
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void contains_multipleChoices_enoughReverseOrder() {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        production.addChoice(ResourceType.STONE, ResourceType.ORE);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void contains_multipleChoices_moreThanEnough() {
        Production production = new Production();
        production.addChoice(ResourceType.LOOM, ResourceType.GLASS, ResourceType.PAPYRUS);
        production.addChoice(ResourceType.STONE, ResourceType.ORE);
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void contains_mixedFixedAndChoice_enough() {
        Production production = new Production();
        production.addFixedResource(ResourceType.WOOD, 1);
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void addAll_empty() {
        Production production = new Production();
        production.addAll(emptyResources);
        assertTrue(production.contains(emptyResources));
    }

    @Test
    public void addAll_singleResource() {
        Production production = new Production();
        production.addAll(resources1Stone);
        assertTrue(production.contains(resources1Stone));
    }

    @Test
    public void addAll_multipleResources() {
        Production production = new Production();
        production.addAll(resources2Stones3Clay);
        assertTrue(production.contains(resources2Stones3Clay));
    }

    @Test
    public void addAll_production_multipleFixedResources() {
        Production production = new Production();
        production.addAll(resources2Stones3Clay);

        Production production2 = new Production();
        production2.addAll(production);

        assertTrue(production2.contains(resources2Stones3Clay));
    }

    @Test
    public void addAll_production_multipleChoices() {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        production.addChoice(ResourceType.STONE, ResourceType.ORE);

        Production production2 = new Production();
        production2.addAll(production);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void addAll_production_mixedFixedResourcesAndChoices() {
        Production production = new Production();
        production.addFixedResource(ResourceType.WOOD, 1);
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);

        Production production2 = new Production();
        production2.addAll(production);

        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void asChoices_empty() {
        Production production = new Production();
        assertTrue(production.asChoices().isEmpty());
    }

    @Test
    public void asChoices_onlyChoices() {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        production.addChoice(ResourceType.STONE, ResourceType.ORE);
        production.addChoice(ResourceType.CLAY, ResourceType.LOOM, ResourceType.GLASS);
        assertEquals(production.getAlternativeResources(), production.asChoices());
    }

    @Test
    public void asChoices_onlyFixed() {
        Production production = new Production();
        production.addFixedResource(ResourceType.WOOD, 1);
        production.addFixedResource(ResourceType.CLAY, 2);

        Set<Set<ResourceType>> expected = new HashSet<>();
        expected.add(EnumSet.of(ResourceType.WOOD));
        expected.add(EnumSet.of(ResourceType.CLAY));
        expected.add(EnumSet.of(ResourceType.CLAY));

        assertEquals(expected, production.asChoices());
    }

    @Test
    public void asChoices_mixed() {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.ORE);
        production.addChoice(ResourceType.CLAY, ResourceType.LOOM, ResourceType.GLASS);
        production.addFixedResource(ResourceType.WOOD, 1);
        production.addFixedResource(ResourceType.CLAY, 2);

        Set<Set<ResourceType>> expected = new HashSet<>();
        expected.add(EnumSet.of(ResourceType.STONE, ResourceType.ORE));
        expected.add(EnumSet.of(ResourceType.CLAY, ResourceType.LOOM, ResourceType.GLASS));
        expected.add(EnumSet.of(ResourceType.WOOD));
        expected.add(EnumSet.of(ResourceType.CLAY));
        expected.add(EnumSet.of(ResourceType.CLAY));

        assertEquals(expected, production.asChoices());
    }

    @Test
    public void equals_falseWhenNull() {
        Production production = new Production();
        production.addFixedResource(ResourceType.GLASS, 1);
        production.addChoice(ResourceType.ORE, ResourceType.WOOD);
        //noinspection ObjectEqualsNull
        assertFalse(production.equals(null));
    }

    @Test
    public void equals_falseWhenDifferentClass() {
        Production production = new Production();
        production.addFixedResource(ResourceType.GLASS, 1);
        Resources resources = new Resources();
        resources.add(ResourceType.GLASS, 1);
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(production.equals(resources));
    }

    @Test
    public void equals_trueWhenSame() {
        Production production = new Production();
        assertEquals(production, production);
    }

    @Test
    public void equals_trueWhenSameContent() {
        Production production1 = new Production();
        Production production2 = new Production();
        assertTrue(production1.equals(production2));
        production1.addFixedResource(ResourceType.GLASS, 1);
        production2.addFixedResource(ResourceType.GLASS, 1);
        assertTrue(production1.equals(production2));
        production1.addChoice(ResourceType.ORE, ResourceType.WOOD);
        production2.addChoice(ResourceType.ORE, ResourceType.WOOD);
        assertTrue(production1.equals(production2));
    }

    @Test
    public void hashCode_sameWhenSameContent() {
        Production production1 = new Production();
        Production production2 = new Production();
        assertEquals(production1.hashCode(), production2.hashCode());
        production1.addFixedResource(ResourceType.GLASS, 1);
        production2.addFixedResource(ResourceType.GLASS, 1);
        assertEquals(production1.hashCode(), production2.hashCode());
        production1.addChoice(ResourceType.ORE, ResourceType.WOOD);
        production2.addChoice(ResourceType.ORE, ResourceType.WOOD);
        assertEquals(production1.hashCode(), production2.hashCode());
    }
}
