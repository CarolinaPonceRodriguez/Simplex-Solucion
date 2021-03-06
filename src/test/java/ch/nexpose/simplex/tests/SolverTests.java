package ch.nexpose.simplex.tests;

/**
 * Created by cansik on 29/11/15.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ch.nexpose.simplex.SimplexProblem;
import ch.nexpose.simplex.SimplexSolver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SolverTests {

    private final double EPSILON = 1e-15;

    private SimplexSolver solver;

    public String readAllText(String filename)
    {
    	 String p = "LP_problems/"+filename+".csv";
        Path path = Paths.get(p);

        String contents = "Not Data";
        try {
            contents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contents;
    }

    @Before
    public void init()
    {
        solver = new SimplexSolver();
        solver.setMostrarValores(true);
    }

    @Test
    public void basicExampleTest() {
        SimplexProblem p = new SimplexProblem();
        p.parse(readAllText("BasicExample3"));

        double result = solver.solve(p);
        double expected = 26.0;

        assertEquals(expected, result, EPSILON);
        
        double expectedX1 = 4.857142857142857;
        assertEquals(expectedX1, solver.getResultadoVariable("X1"), EPSILON);
    }

    @Test
    public void landWirtschaftTest() {
        SimplexProblem p = new SimplexProblem();
        p.parse(readAllText("Landwirtschaft"));

        double result = solver.solve(p);
        double expected = 5400;

        assertEquals(expected, result, EPSILON);
    }

    @Test
    public void zweiSeafteTest() {
        SimplexProblem p = new SimplexProblem();
        p.parse(readAllText("ZweiSaefte"));

        double result = solver.solve(p);
        double expected = 506.66666666666663;

        assertEquals(expected, result, EPSILON);
    }

    @Test
    public void eisenBahnTest() {
        SimplexProblem p = new SimplexProblem();
        p.parse(readAllText("Eisenbahn"));

        double result = solver.solve(p);
        double expected = 191;

        assertEquals(expected, result, EPSILON);
    }
}
