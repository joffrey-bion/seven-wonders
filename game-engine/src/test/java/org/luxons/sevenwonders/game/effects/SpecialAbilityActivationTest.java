package org.luxons.sevenwonders.game.effects;

import java.util.Arrays;

import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.BoardElementType;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class SpecialAbilityActivationTest {

    @DataPoints
    public static SpecialAbility[] abilities() {
        return SpecialAbility.values();
    }

    @DataPoints
    public static RelativeBoardPosition[] neighbours() {
        return new RelativeBoardPosition[] {RelativeBoardPosition.LEFT, RelativeBoardPosition.RIGHT};
    }

    @DataPoints
    public static Card[] guilds() {
        BonusPerBoardElement bonus = new BonusPerBoardElement();
        bonus.setType(BoardElementType.CARD);
        bonus.setColors(Arrays.asList(Color.GREY, Color.BROWN));
        bonus.setBoards(Arrays.asList(RelativeBoardPosition.LEFT, RelativeBoardPosition.RIGHT));
        bonus.setPoints(1);

        BonusPerBoardElement bonus2 = new BonusPerBoardElement();
        bonus2.setType(BoardElementType.BUILT_WONDER_STAGES);
        bonus2.setBoards(
                Arrays.asList(RelativeBoardPosition.LEFT, RelativeBoardPosition.SELF, RelativeBoardPosition.RIGHT));
        bonus2.setPoints(1);

        return new Card[] {TestUtilsKt.createGuildCard(1, bonus), TestUtilsKt.createGuildCard(2, bonus2)};
    }

    @Theory
    public void apply_addsAbility(SpecialAbility ability) {
        SpecialAbilityActivation effect = new SpecialAbilityActivation(ability);
        Table table = TestUtilsKt.testTable(5);

        effect.apply(table, 0);

        Board board = table.getBoard(0);
        assertTrue(board.hasSpecial(ability));
    }

    @Theory
    public void computePoints_zeroExceptForCopyGuild(SpecialAbility ability) {
        Assume.assumeTrue(ability != SpecialAbility.COPY_GUILD);

        SpecialAbilityActivation effect = new SpecialAbilityActivation(ability);
        Table table = TestUtilsKt.testTable(5);

        assertEquals(0, effect.computePoints(table, 0));
    }

    @Theory
    public void computePoints_copiedGuild(Card guildCard, RelativeBoardPosition neighbour) {
        SpecialAbilityActivation effect = new SpecialAbilityActivation(SpecialAbility.COPY_GUILD);
        Table table = TestUtilsKt.testTable(5);

        Board neighbourBoard = table.getBoard(0, neighbour);
        neighbourBoard.addCard(guildCard);

        Board board = table.getBoard(0);
        board.setCopiedGuild(guildCard);

        int directPointsFromGuildCard = guildCard.getEffects().stream().mapToInt(e -> e.computePoints(table, 0)).sum();
        assertEquals(directPointsFromGuildCard, effect.computePoints(table, 0));
    }

    @Test(expected = IllegalStateException.class)
    public void computePoints_copyGuild_failWhenNoChosenGuild() {
        SpecialAbilityActivation effect = new SpecialAbilityActivation(SpecialAbility.COPY_GUILD);
        Table table = TestUtilsKt.testTable(5);
        effect.computePoints(table, 0);
    }
}
