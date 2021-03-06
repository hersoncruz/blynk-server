package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.ReportingDao;
import cc.blynk.server.dao.SessionDao;
import cc.blynk.server.dao.UserDao;
import cc.blynk.server.reporting.average.AverageAggregator;
import cc.blynk.server.workers.notifications.NotificationsProcessor;

import static cc.blynk.server.utils.ReportingUtil.getReportingFolder;

/**
 * Just a holder for all necessary objects for server instance creation.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 28.09.15.
 */
public class Holder {

    public final TransportTypeHolder transportType;

    public final FileManager fileManager;

    public final SessionDao sessionDao;

    public final UserDao userDao;

    public final ReportingDao reportingDao;

    public final GlobalStats stats;

    public final ServerProperties props;

    public final NotificationsProcessor notificationsProcessor;

    public final AverageAggregator averageAggregator;

    public Holder(ServerProperties serverProperties) {
        this(serverProperties, new NotificationsProcessor(
                serverProperties.getIntProperty("notifications.queue.limit", 10000)
        ));
    }

    //needed for tests only
    public Holder(ServerProperties serverProperties, NotificationsProcessor notificationsProcessor) {
        this.props = serverProperties;

        String dataFolder = serverProperties.getProperty("data.folder");

        this.transportType = new TransportTypeHolder(serverProperties);
        this.fileManager = new FileManager(dataFolder);
        this.sessionDao = new SessionDao();
        this.userDao = new UserDao(fileManager.deserialize());
        this.stats = new GlobalStats();
        this.averageAggregator = new AverageAggregator(getReportingFolder(dataFolder));
        this.reportingDao = new ReportingDao(averageAggregator, serverProperties);

        this.notificationsProcessor = notificationsProcessor;
    }
}
