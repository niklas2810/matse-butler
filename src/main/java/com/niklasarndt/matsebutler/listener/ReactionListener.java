package com.niklasarndt.matsebutler.listener;

import com.niklasarndt.matsebutler.Butler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Niklas on 2020/07/28.
 */
public class ReactionListener extends ListenerAdapter {

    private static final List<String> REACT_EMOJIS = List.of(/*Emojis.WASTEBASKET*/);
    private final Butler butler;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExpiringMap<Long, String> removed = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(5, TimeUnit.SECONDS).build();

    public ReactionListener(Butler butler) {
        this.butler = butler;
    }

    @Override
    public void onPrivateMessageReactionAdd(@Nonnull PrivateMessageReactionAddEvent event) {
        if (!passesFilter(event.getUserIdLong(), event.getReactionEmote().getEmoji())) return;

        scheduleReactionProcessing(event.getMessageIdLong(), event.getChannel(),
                event.getReactionEmote().getEmoji());
    }

    @Override
    public void onPrivateMessageReactionRemove(@Nonnull PrivateMessageReactionRemoveEvent event) {
        if (!passesFilter(event.getUserIdLong(), event.getReactionEmote().getEmoji())) return;
        removed.put(event.getMessageIdLong(), event.getReactionEmote().getEmoji());
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() != butler.getGuildId()) return;
        if (event.getChannel().getTopic() == null ||
                !event.getChannel().getTopic().contains("allow-butler")) return;
        if (!passesFilter(event.getUserIdLong(), event.getReactionEmote().getEmoji())) return;

        scheduleReactionProcessing(event.getMessageIdLong(), event.getChannel(),
                event.getReactionEmote().getEmoji());
    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        if (event.getGuild().getIdLong() != butler.getGuildId()) return;
        if (event.getChannel().getTopic() == null ||
                !event.getChannel().getTopic().contains("allow-butler")) return;
        if (!passesFilter(event.getUserIdLong(), event.getReactionEmote().getEmoji())) return;

        removed.put(event.getMessageIdLong(), event.getReactionEmote().getEmoji());
    }

    private boolean passesFilter(long userIdLong, String emoji) {
        if (userIdLong != butler.getOwnerId()) return false;

        boolean registered = REACT_EMOJIS.contains(emoji);
        logger.debug("Received emote: {} (Registered? {})", emoji, registered);
        return registered;
    }

    private void scheduleReactionProcessing(long messageId,
                                            MessageChannel channel, String emoji) {
        channel.retrieveMessageById(messageId)
                .delay(3, TimeUnit.SECONDS)
                .flatMap(message -> runProcessingChecks(message, emoji),
                        message -> {
                            logger.debug("Executing reaction processing");

                            /*if (emoji.equals(Emojis.WASTEBASKET)) {
                                return performDeleteAction(channel, message);
                            } else if (emoji.equals(Emojis.HOURGLASS)) {
                                return performSnoozeAction(message);
                            } else {

                            }*/
                            throw new IllegalStateException("Unexpected value: " + emoji);
                        }).queue();
    }

    private boolean runProcessingChecks(Message message, String emoji) {
        if (message == null || !message.getAuthor().isBot()) {
            logger.debug("Not suitable for reaction processing");
            return false;
        }

        if (removed.containsKey(message.getIdLong()) &&
                removed.get(message.getIdLong())
                        .equals(emoji)) {
            logger.debug("User removed reaction");
            return false;
        }
/*
        if (emoji.equals(Emojis.HOURGLASS)) {
            if (message.getEmbeds().size() != 1) {
                logger.debug("This message does not contain an embed.");
                return false;
            }
            if (message.getEmbeds().get(0).getFields()
                    .stream().noneMatch(field -> Objects.equals(field.getName(),
                            "Your Reminder"))) {
                logger.debug("There is no reminder field in this embed.");
                return false;
            }
        }*/
        return true;

    }
/*


    private RestAction<Void> performDeleteAction(MessageChannel channel, Message message) {
        if (channel instanceof GuildChannel) { //Try to delete original message
            return channel.getHistoryBefore(message.getIdLong(), 1)
                    .delay(100, TimeUnit.MILLISECONDS)
                    .flatMap(history -> {

                        long distanceInSeconds = history.isEmpty() ? 0 :
                                Math.abs(history.getRetrievedHistory().get(0)
                                        .getTimeCreated().toEpochSecond() -
                                        message.getTimeCreated().toEpochSecond());

                        if (history.isEmpty()) {
                            logger.debug("Did not retrieve any previous messages");
                        } else if (history.getRetrievedHistory().get(0).getAuthor().getIdLong()
                                != butler.getOwnerId()) {
                            logger.debug("Author of previous message is not the bot owner");
                        } else if (distanceInSeconds > 5) {
                            logger.debug("Time between bot message and original message " +
                                    "is too large ({}s)", distanceInSeconds);
                        } else {
                            return channel.deleteMessageById(
                                    history.getRetrievedHistory().get(0).getId())
                                    .flatMap(empty -> message.delete());
                        }

                        return message.delete();
                    });
        } else return message.delete();
    }
*/
}
