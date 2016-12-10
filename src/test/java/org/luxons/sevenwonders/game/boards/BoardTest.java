package org.luxons.sevenwonders.game.boards;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.test.TestUtils;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class BoardTest {

    @DataPoints
    public static int[] goldAmounts() {
        return new int[]{-5, -1, 0, 1, 2, 5, 10};
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @Theory
    public void initialGold_respectsSettings(int goldAmountInSettings) {
        Settings settings = new Settings();
        settings.setInitialGold(goldAmountInSettings);
        Board board = new Board(TestUtils.createWonder(), settings);
        assertEquals(goldAmountInSettings, board.getGold());
    }

    @Theory
    public void initialProduction_containsInitialResource(ResourceType type) {
        Board board = new Board(TestUtils.createWonder(type), new Settings());
        Resources resources = TestUtils.createResources(type);
        assertTrue(board.getProduction().contains(resources));
    }

}