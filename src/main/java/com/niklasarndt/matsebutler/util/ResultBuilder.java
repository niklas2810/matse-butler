package com.niklasarndt.matsebutler.util;

import com.niklasarndt.matsebutler.enums.ResultType;
import com.niklasarndt.matsebutler.modules.ButlerModuleInformation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

/**
 * Created by Niklas on 2020/07/25.
 */
public class ResultBuilder {

    public static final ResultBuilder NOT_FOUND =
            new ResultBuilder(null, ResultType.NOT_FOUND, null);
    private final ButlerModuleInformation currentModule;

    private ResultType type = ResultType.SUCCESS;
    private String output;

    private boolean usesEmbed = false;
    private EmbedBuilder embedBuilder;

    public ResultBuilder(ButlerModuleInformation currentModule) {
        this.currentModule = currentModule;
    }

    public ResultBuilder(ButlerModuleInformation currentModule, ResultType type, String output) {
        this.currentModule = currentModule;
        this.type = type;
        this.output = output;
    }

    public ButlerModuleInformation getCurrentModule() {
        return currentModule;
    }

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public String getOutput() {
        return output != null ? output : "";
    }

    public void invalidArgsLength(int min, int max, int actual) {
        type = ResultType.ERROR;
        if (min == max || min == 0) {
            output = String.format("This command does not accept parameters (your provided %d).",
                    actual);
        } else {
            output = String.format("This command only accepts %d-%d parameters (you provided %d).",
                    min, max, actual);
        }
    }

    public boolean hasOutput() {
        return (output != null && output.length() > 0) || usesEmbed && !embedBuilder.isEmpty();
    }

    public EmbedBuilder useEmbed() {
        usesEmbed = true;
        if (embedBuilder == null) embedBuilder = new EmbedBuilder();
        return embedBuilder;
    }

    public String produceString(boolean addEmoji) {
        if (usesEmbed) return "";
        return (addEmoji ? type.emoji + " " : "") + getOutput();
    }

    public String produceString() {
        return produceString(type != ResultType.SUCCESS);
    }

    public MessageEmbed produceEmbed() {
        return usesEmbed ? embedBuilder.build() : null;
    }

    public void produceIntoChannel(MessageChannel channel) {
        MessageAction action;
        if (usesEmbed) {
            MessageEmbed result = produceEmbed();
            if (!result.isSendable()) {
                throw new IllegalArgumentException(
                        String.format("The produced result is too long to be sent " +
                                        "(length: %s, max allowed: %s).",
                                result.getLength(), MessageEmbed.EMBED_MAX_LENGTH_BOT));
            } else action = channel.sendMessage(produceEmbed());
        } else action = channel.sendMessage(produceString());

        //Deletion hint
        action.flatMap(message -> message.addReaction(Emojis.WASTEBASKET)).queue();
    }

    public void success(String output) {
        this.type = ResultType.SUCCESS;
        this.output = output;
    }

    public void success(String output, Object... args) {
        success(String.format(output, args));
    }

    public void error(String output) {
        type = ResultType.ERROR;
        this.output = output;
    }

    public void error(String output, Object... args) {
        error(String.format(output, args));
    }

    public void error(Exception exception) {
        error("The command could not be executed. Reason: **%s** - %s",
                exception.getClass().getSimpleName(), exception.getMessage());
    }

    public void notFound(String output) {
        this.type = ResultType.NOT_FOUND;
        this.output = output;
    }

    public void notFound(String output, Object... args) {
        notFound(String.format(output, args));
    }

    public void warn(String output) {
        this.type = ResultType.WARNING;
        this.output = output;
    }

    public void warn(String output, Object... args) {
        warn(String.format(output, args));
    }

    public void output(ResultBuilder other) {
        this.type = other.type;
        this.output = other.output;
        this.usesEmbed = other.usesEmbed;
        this.embedBuilder = other.embedBuilder;
    }
}
