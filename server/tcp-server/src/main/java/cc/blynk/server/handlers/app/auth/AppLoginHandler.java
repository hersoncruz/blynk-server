package cc.blynk.server.handlers.app.auth;

import cc.blynk.common.handlers.DefaultExceptionHandler;
import cc.blynk.common.model.messages.protocol.appllication.LoginMessage;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.dao.ReportingDao;
import cc.blynk.server.dao.SessionDao;
import cc.blynk.server.dao.UserDao;
import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.exceptions.UserNotAuthenticated;
import cc.blynk.server.exceptions.UserNotRegistered;
import cc.blynk.server.handlers.app.AppHandler;
import cc.blynk.server.handlers.common.UserNotLoggerHandler;
import cc.blynk.server.handlers.hardware.auth.HandlerState;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.workers.notifications.NotificationsProcessor;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * Handler responsible for managing hardware and apps login messages.
 * Initializes netty channel with a state tied with user.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
@ChannelHandler.Sharable
public class AppLoginHandler extends SimpleChannelInboundHandler<LoginMessage> implements DefaultExceptionHandler {

    private final ServerProperties props;
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final ReportingDao reportingDao;
    private final NotificationsProcessor notificationsProcessor;

    public AppLoginHandler(ServerProperties props, UserDao userDao, SessionDao sessionDao, ReportingDao reportingDao, NotificationsProcessor notificationsProcessor) {
        this.props = props;
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.reportingDao = reportingDao;
        this.notificationsProcessor = notificationsProcessor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage message) throws Exception {
        //warn: split may be optimized
        String[] messageParts = message.body.split(" ", 2);

        if (messageParts.length == 2) {
            appLogin(ctx, message.id, messageParts[0], messageParts[1]);
        } else {
           throw new IllegalCommandException("Wrong income message format.", message.id);
        }

        ctx.writeAndFlush(produce(message.id, OK));
    }

    private void appLogin(ChannelHandlerContext ctx, int messageId, String username, String pass) {
        String userName = username.toLowerCase();
        User user = userDao.getByName(userName);

        if (user == null) {
            throw new UserNotRegistered(String.format("User not registered. Username '%s', %s", userName, ctx.channel().remoteAddress()), messageId);
        }

        if (!user.pass.equals(pass)) {
            throw new UserNotAuthenticated(String.format("User credentials are wrong. Username '%s', %s", userName, ctx.channel().remoteAddress()), messageId);
        }

        ctx.pipeline().remove(this);
        ctx.pipeline().remove(UserNotLoggerHandler.class);
        ctx.pipeline().remove(RegisterHandler.class);
        ctx.pipeline().addLast(new AppHandler(props, userDao, sessionDao, reportingDao, notificationsProcessor, new HandlerState(user)));

        sessionDao.addAppChannel(user, ctx.channel());

        log.info("{} app joined.", user.name);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       handleGeneralException(ctx, cause);
    }
}
