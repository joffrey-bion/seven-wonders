package org.luxons.sevenwonders.game.data.definitions;

import java.util.Random;

public enum WonderSidePickMethod {
    ALL_A {
        @Override
        public WonderSide pickSide(Random random, WonderSide lastPickedSide) {
            return WonderSide.A;
        }
    },
    ALL_B {
        @Override
        public WonderSide pickSide(Random random, WonderSide lastPickedSide) {
            return WonderSide.B;
        }
    },
    EACH_RANDOM {
        @Override
        public WonderSide pickSide(Random random, WonderSide lastPickedSide) {
            return random.nextBoolean() ? WonderSide.A : WonderSide.B;
        }
    },
    SAME_RANDOM_FOR_ALL {
        @Override
        public WonderSide pickSide(Random random, WonderSide lastPickedSide) {
            if (lastPickedSide == null) {
                return random.nextBoolean() ? WonderSide.A : WonderSide.B;
            } else {
                return lastPickedSide;
            }
        }
    };

    public abstract WonderSide pickSide(Random random, WonderSide lastPickedSide);
}
