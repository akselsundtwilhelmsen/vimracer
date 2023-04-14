package vimracer;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VimTest {

    private Vim vim;

    @BeforeEach 
    public void setup() {
        vim = new Vim();
        ArrayList<String> vimtext = new ArrayList<>();
        vimtext.add("null linjer");
        vimtext.add("en ener");
        vimtext.add("to");
        vimtext.add("tre");
        vimtext.add("");
        vimtext.add("fem");
        vimtext.add("seks");
        vim.setText(vimtext);
    }

	@Test
	@DisplayName("Test toStringBetween")
	public void testCapacity() {
        int[] from = {5,0,0,0};
        int[] to = {11,0,0,0};
        Assertions.assertEquals(vim.toStringBetween(from, to),"linjer");
        to[0] = 2;
        to[1] = 1;
        Assertions.assertEquals(vim.toStringBetween(from, to),"linjer\nen");
	}
}
