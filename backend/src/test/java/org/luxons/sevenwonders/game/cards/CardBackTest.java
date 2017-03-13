package org.luxons.sevenwonders.game.cards;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CardBackTest {

    @Test
    public void initializedWithImage() throws Exception {
        String imagePath = "whateverimage.png";
        CardBack back = new CardBack(imagePath);
        assertEquals(imagePath, back.getImage());
    }
}
