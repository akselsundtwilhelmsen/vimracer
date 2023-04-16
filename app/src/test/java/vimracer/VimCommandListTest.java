package vimracer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VimCommandListTest {

    private VimCommandList commands;
    
    @BeforeEach 
    public void setup() {
        commands = new VimCommandList(new Vim());
    }

	@Test
	@DisplayName("Is command list executable")
	public void testIsCommandListExecutable() {
        Assertions.assertFalse(commands.isCommandListExecutable());
        commands.buildCommandList("3");
        commands.buildCommandList("4");
        Assertions.assertFalse(commands.isCommandListExecutable());
        commands.buildCommandList("l");
        Assertions.assertTrue(commands.isCommandListExecutable());
        commands.clear();
        commands.buildCommandList("d");
        Assertions.assertFalse(commands.isCommandListExecutable());
        commands.buildCommandList("3");
        commands.buildCommandList("4");
        Assertions.assertFalse(commands.isCommandListExecutable());
        commands.buildCommandList("w");
        Assertions.assertTrue(commands.isCommandListExecutable());
        commands.clear();
        commands.buildCommandList("i");
        Assertions.assertTrue(commands.isCommandListExecutable());
	}

	@Test
	@DisplayName("Can the command list be executable")
	public void testWillCommandListExecutable() {
        Assertions.assertTrue(commands.willCommandListExecutable());
        commands.buildCommandList("d");
        Assertions.assertTrue(commands.willCommandListExecutable());
        commands.buildCommandList("y");
        Assertions.assertFalse(commands.willCommandListExecutable());
        commands.buildCommandList("4");
        commands.buildCommandList("p");
        commands.buildCommandList("b");
        Assertions.assertFalse(commands.willCommandListExecutable());
        commands.clear();
        commands.buildCommandList("4");
        commands.buildCommandList("d");
        Assertions.assertTrue(commands.willCommandListExecutable());
        commands.buildCommandList("5");
        commands.buildCommandList("l");
        Assertions.assertTrue(commands.willCommandListExecutable());
	}
}
