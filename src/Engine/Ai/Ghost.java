/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Ai;

import Engine.DataTransport;
import Engine.GameState;
import Engine.GameState.State;
import java.util.ArrayList;

/**
 *
 * @author Yves
 */
public class Ghost
{

    ArrayList<GhostBase> tests = new ArrayList<>();
    ArrayList<State> args = new ArrayList<>();

    public Ghost()
    {
        tests.add(new TestTripplePatternX());
        args.add(State.OTHER);
        tests.add(new TestTripplePatternY());
        args.add(State.OTHER);
        tests.add(new TestTripplePatternXY());
        args.add(State.OTHER);
        tests.add(new TestTripplePatternX());
        args.add(State.MINE);
        tests.add(new TestTripplePatternY());
        args.add(State.MINE);
        tests.add(new TestTripplePatternXY());
        args.add(State.MINE);
        tests.add(new TestDoublePatternX());
        args.add(State.MINE);
        tests.add(new TestDoublePatternY());
        args.add(State.MINE);
        tests.add(new TestDoublePatternX());
        args.add(State.OTHER);
        tests.add(new TestDoublePatternY());
        args.add(State.OTHER);
    }

    /**
     * Method to call the AI. The order of calculation for a turn is: - AI try
     * to win in x-, y- and both diagonal-directions - AI tries to prevent the
     * gain of the player - If only two stones of the player is in a foursome
     * pattern, a stone is set in. So the player is blocked - If only two stones
     * of the AI is in a foursome pattern, a stone is set in to try to win
     *
     * @author Yves Studer
     * @param field representation of the current field
     * @return DataTransoprt object with the x-value
     */
    public DataTransport DoTurn(GameState field)
    {
        for (int i = 0; i < tests.size(); i++)
        {
            DataTransport tmp = tests.get(i).doTest(field, args.get(i));
            if (tmp != null)
            {
                return tmp;
            }
        }
        return tests.get(0).createRandomTurn(field);
    }
}
