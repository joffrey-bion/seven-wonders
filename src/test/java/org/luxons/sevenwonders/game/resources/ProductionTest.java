package org.luxons.sevenwonders.game.resources;

import org.junit.Before;
import org.junit.Test;

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
    public void contains_newProductionContainsEmpty() throws Exception {
        Production production = new Production();
        assertTrue(production.contains(emptyResources));
    }

    @Test
    public void contains_singleFixedResource_noneAtAll() throws Exception {
        Production production = new Production();
        assertFalse(production.contains(resources2Stones));
    }

    @Test
    public void contains_singleFixedResource_notEnough() throws Exception {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 1);
        assertFalse(production.contains(resources2Stones));
    }

    @Test
    public void contains_singleFixedResource_justEnough() throws Exception {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 2);
        assertTrue(production.contains(resources2Stones));
    }

    @Test
    public void contains_singleFixedResource_moreThanEnough() throws Exception {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 3);
        assertTrue(production.contains(resources2Stones));
    }

    @Test
    public void contains_singleFixedResource_moreThanEnough_amongOthers() throws Exception {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 3);
        production.addFixedResource(ResourceType.CLAY, 2);
        assertTrue(production.contains(resources2Stones));
    }

    @Test
    public void contains_multipleFixedResources_notEnoughOfOne() throws Exception {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 3);
        production.addFixedResource(ResourceType.CLAY, 1);
        assertFalse(production.contains(resources2Stones3Clay));
    }

    @Test
    public void contains_multipleFixedResources_notEnoughOfBoth() throws Exception {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 1);
        production.addFixedResource(ResourceType.CLAY, 1);
        assertFalse(production.contains(resources2Stones3Clay));
    }

    @Test
    public void contains_multipleFixedResources_moreThanEnough() throws Exception {
        Production production = new Production();
        production.addFixedResource(ResourceType.STONE, 3);
        production.addFixedResource(ResourceType.CLAY, 5);
        assertTrue(production.contains(resources2Stones3Clay));
    }

    @Test
    public void contains_singleChoice_containsEmpty() throws Exception {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.CLAY);
        assertTrue(production.contains(emptyResources));
    }

    @Test
    public void contains_singleChoice_enough() throws Exception {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        assertTrue(production.contains(resources1Wood));
        assertTrue(production.contains(resources1Stone));
    }

    @Test
    public void contains_multipleChoices_notBoth() throws Exception {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.CLAY);
        production.addChoice(ResourceType.STONE, ResourceType.CLAY);
        production.addChoice(ResourceType.STONE, ResourceType.CLAY);
        assertFalse(production.contains(resources2Stones3Clay));
    }

    @Test
    public void contains_multipleChoices_enough() throws Exception {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.ORE);
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void contains_multipleChoices_enoughReverseOrder() throws Exception {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        production.addChoice(ResourceType.STONE, ResourceType.ORE);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void contains_multipleChoices_moreThanEnough() throws Exception {
        Production production = new Production();
        production.addChoice(ResourceType.LOOM, ResourceType.GLASS, ResourceType.PAPYRUS);
        production.addChoice(ResourceType.STONE, ResourceType.ORE);
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void contains_mixedFixedAndChoice_enough() throws Exception {
        Production production = new Production();
        production.addFixedResource(ResourceType.WOOD, 1);
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void addAll_empty() throws Exception {
        Production production = new Production();
        production.addAll(emptyResources);
        assertTrue(production.contains(emptyResources));
    }

    @Test
    public void addAll_singleResource() throws Exception {
        Production production = new Production();
        production.addAll(resources1Stone);
        assertTrue(production.contains(resources1Stone));
    }

    @Test
    public void addAll_multipleResources() throws Exception {
        Production production = new Production();
        production.addAll(resources2Stones3Clay);
        assertTrue(production.contains(resources2Stones3Clay));
    }

    @Test
    public void addAll_production_multipleFixedResources() throws Exception {
        Production production = new Production();
        production.addAll(resources2Stones3Clay);

        Production production2 = new Production();
        production2.addAll(production);

        assertTrue(production2.contains(resources2Stones3Clay));
    }

    @Test
    public void addAll_production_multipleChoices() throws Exception {
        Production production = new Production();
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);
        production.addChoice(ResourceType.STONE, ResourceType.ORE);

        Production production2 = new Production();
        production2.addAll(production);
        assertTrue(production.contains(resources1Stone1Wood));
    }

    @Test
    public void addAll_production_mixedFixedResourcesAndChoices() throws Exception {
        Production production = new Production();
        production.addFixedResource(ResourceType.WOOD, 1);
        production.addChoice(ResourceType.STONE, ResourceType.WOOD);

        Production production2 = new Production();
        production2.addAll(production);

        assertTrue(production.contains(resources1Stone1Wood));
    }
}