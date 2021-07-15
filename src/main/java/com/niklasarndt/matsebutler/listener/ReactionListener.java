package com.niklasarndt.matsebutler.listener;

import com.niklasarndt.matsebutler.Butler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
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
        if (!passesFilter(event.getReactionEmote().getEmoji(), event.getUser())) return;

        scheduleReactionProcessing(event.getMessageIdLong(), event.getChannel(),
                event.getReactionEmote().getEmoji());
    }

    @Override
    public void onPrivateMessageReactionRemove(@Nonnull PrivateMessageReactionRemoveEvent event) {
        if (!passesFilter(event.getReactionEmote().getEmoji(), event.getUser())) return;
        removed.put(event.getMessageIdLong(), event.getReactionEmote().getEmoji());
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() != butler.getGuild()) return;
        if (event.getChannel().getTopic() == null ||
                !event.getChannel().getTopic().contains(butler.getConfig().getActivator())) return;
        if (!passesFilter(event.getReactionEmote().getEmoji(), event.getUser())) return;

        scheduleReactionProcessing(event.getMessageIdLong(), event.getChannel(),
                event.getReactionEmote().getEmoji());
    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        if (event.getGuild().getIdLong() != butler.getGuild()) return;
        if (event.getChannel().getTopic() == null ||
                !event.getChannel().getTopic().contains(butler.getConfig().getActivator())) return;
        if (!passesFilter(event.getReactionEmote().getEmoji(), event.getUser())) return;

        removed.put(event.getMessageIdLong(), event.getReactionEmote().getEmoji());
    }

    private boolean passesFilter(String emoji, User user) {
        if (!butler.isAdmin(user.getIdLong()) || user.isBot()) return false;
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
        return true;

    }
}
