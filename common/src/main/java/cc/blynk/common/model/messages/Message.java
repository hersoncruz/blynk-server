package cc.blynk.common.model.messages;

import cc.blynk.common.utils.Config;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public abstract class Message extends MessageBase {

    public String body;

    public Message(int messageId, short command, int length, String body) {
        super(messageId, command, length);
        this.body = body;
    }

    public byte[] getBytes() {
        return body.getBytes(Config.DEFAULT_CHARSET);
    }

    @Override
    public String toString() {
        return super.toString() + ", body='" + body + "'";
    }
}
