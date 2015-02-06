package cc.blynk.server.auth.session;

import cc.blynk.server.auth.User;
import cc.blynk.server.exceptions.UserNotAuthenticated;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionsHolder {

    private static final Logger log = LogManager.getLogger(SessionsHolder.class);

    private Map<User, Session> userSession = new ConcurrentHashMap<>();

    private Map<Channel, User> channelToken = new ConcurrentHashMap<>();

    public void addAppChannelToGroup(User user, Channel channel, int msgId) {
        Session group = getSessionByUser(user);
        group.addAppChannel(channel, msgId);
        channelToken.put(channel, user);
    }

    public void addHardwareChannelToGroup(User user, Channel channel, int msgId) {
        Session group = getSessionByUser(user);
        group.addHardwareChannel(channel, msgId);
        channelToken.put(channel, user);
    }

    public Session getUserSession(Channel channel, int messageId) {
        User user = findUserByChannel(channel, messageId);
        return userSession.get(user);
    }

    public User findUserByChannel(Channel inChannel, int msgId) {
        User user = channelToken.get(inChannel);
        if (user == null) {
            throw new UserNotAuthenticated("User not logged.", msgId);
        }
        return user;
    }

    public Map<User, Session> getUserSession() {
        return userSession;
    }

    //todo synchronized?
    private Session getSessionByUser(User user) {
        Session group = userSession.get(user);
        //only one side came
        if (group == null) {
            log.debug("Creating unique session for user: {}", user);
            group = new Session();
            userSession.put(user, group);
        }

        return group;
    }

}