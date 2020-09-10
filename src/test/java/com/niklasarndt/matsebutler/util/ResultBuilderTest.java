package com.niklasarndt.matsebutler.util;

import com.niklasarndt.matsebutler.enums.ResultType;
import com.niklasarndt.testing.util.ButlerTest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Created by Niklas on 2020/07/27.
 */
class ResultBuilderTest extends ButlerTest {

    @Test
    public void testStringResults() {
        ResultBuilder builder = new ResultBuilder(null);
        assertEquals("", builder.produceString());
        builder.success("test");
        assertEquals("test", builder.produceString());
        builder.error("test");
        assertEquals(ResultType.ERROR.emoji + " test", builder.produceString());
        builder.useEmbed();
        assertEquals("", builder.produceString());
    }

    @Test
    public void testEmbeds() {
        ResultBuilder builder = new ResultBuilder(null);

        assertNull(builder.produceEmbed()); //Embed not selected yet

        EmbedBuilder embed = builder.useEmbed();

        assertThrows(IllegalStateException.class, builder::produceEmbed); //Denied by JDA

        embed.setTitle("test"); //Create correct embed

        assertEquals("test", builder.produceEmbed().getTitle());
        assertTrue(builder.produceEmbed().isSendable());

        assertThrows(IllegalArgumentException.class, () -> embed.setTitle(
                ".".repeat(MessageEmbed.TITLE_MAX_LENGTH + 1))); //Title too long
    }

    @Test
    public void testResultCopy() {
        ResultBuilder builder = new ResultBuilder(null);
        builder.error("Test");

        ResultBuilder copy = new ResultBuilder(null);
        copy.output(builder);

        assertEquals("Test", copy.getOutput());
        assertEquals(ResultType.ERROR, copy.getType());
    }

}