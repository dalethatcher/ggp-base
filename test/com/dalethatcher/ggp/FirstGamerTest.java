package com.dalethatcher.ggp;

import com.google.common.collect.Lists;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.game.TestGameRepository;
import org.ggp.base.util.gdl.grammar.GdlFunction;
import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.match.Match;
import org.junit.Test;

import static org.ggp.base.util.gdl.grammar.GdlPool.getConstant;
import static org.ggp.base.util.gdl.grammar.GdlPool.getFunction;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FirstGamerTest {
    @Test
    public void picksLegalMove() throws Exception {
        Game game = new TestGameRepository().getGame("single_move_only");
        Match match = new Match("id", 0, 0, 0, game);

        FirstGamer firstGamer = new FirstGamer();
        firstGamer.setMatch(match);
        firstGamer.setRoleName(getConstant("player"));
        firstGamer.metaGame(1000);

        GdlTerm move = firstGamer.selectMove(1000);

        GdlFunction expectedMove = getFunction(getConstant("move"), Lists.newArrayList(getConstant("0"), getConstant("1")));
        assertThat(move, is(expectedMove));
    }
}
