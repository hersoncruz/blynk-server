package cc.blynk.server.notifications;

import cc.blynk.server.utils.JsonParser;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.07.15.
 */
public class IOSGCMMessage implements GCMMessage {

    private final String to;

    private final IOSBody notification;

    public IOSGCMMessage(String to, String message) {
        this.to = to;
        this.notification = new IOSBody(message);
    }

    @Override
    public String getToken() {
        return to;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

    private class IOSBody {
        private final String title = "Blynk Notification";
        private final String body;

        public IOSBody(String body) {
            this.body = body;
        }
    }

}
