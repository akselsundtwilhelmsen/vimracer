package vimracer;

import java.util.ArrayList;
import java.util.regex.Pattern;

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
        vimtext.add("fem that's great");
        vimtext.add("seks");
        vim.setText(vimtext);
    }

	@Test
	@DisplayName("Test toStringBetween")
	public void testtoStringBetween() {
        int[] from = {5,0,0,0};
        int[] to = {11,0,0,0};
        Assertions.assertEquals(vim.toStringBetween(from, to),"linjer");
        to[0] = 2;
        to[1] = 2;
        Assertions.assertEquals(vim.toStringBetween(from, to),"linjer\nen ener\nto");
        to[3] = 1;
        Assertions.assertEquals(vim.toStringBetween(from, to),"null linjer\nen ener\nto");
        to[1] = 0;
        Assertions.assertEquals(vim.toStringBetween(from, to),"null linjer");
	}

	@Test
	@DisplayName("Test next regex")
	public void testNextInstanceOf() {
        //test inline
        int[] from = {0,0,0,0}; 
        int[] correctPos = {3,0,0,0};
        Pattern regex = VimCommandList.WORDEnd;
        Assertions.assertEquals(vim.nextInstanceOf(regex, from,false)[0],correctPos[0]);

        correctPos[0] = 5;
        regex = VimCommandList.WORDBeginning;
        Assertions.assertEquals(vim.nextInstanceOf(regex, from,true)[0],correctPos[0]);

        //test multiline
        from[0] = 4;
        from[1] = 1;
        correctPos[0] = 11;
        correctPos[1] = 5;
        regex = Pattern.compile("great");
        Assertions.assertEquals(vim.nextInstanceOf(regex, from,false)[0],correctPos[0]);
        Assertions.assertEquals(vim.nextInstanceOf(regex, from,true)[1],correctPos[1]);
	}

	@Test
	@DisplayName("Test previous regex")
	public void testPrevInstanceOf() {
        //test inline
        int[] from = {6,1,0,0}; 
        int[] correctPos = {3,1,0,0};
        Pattern regex = VimCommandList.wordBeginning;
        Assertions.assertEquals(vim.prevInstanceOf(regex, from,true)[0],correctPos[0]);

        //test multiline
        from[0] = 0;
        from[1] = 6;
        correctPos[0] = 3;
        correctPos[1] = 1;
        regex = Pattern.compile("en");
        Assertions.assertEquals(vim.prevInstanceOf(regex, from,false)[0],correctPos[0]);
        Assertions.assertEquals(vim.prevInstanceOf(regex, from,true)[1],correctPos[1]);
	}
}
