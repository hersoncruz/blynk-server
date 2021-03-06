package cc.blynk.server.model.widgets.others;

import cc.blynk.server.model.Pin;
import cc.blynk.server.model.enums.GraphPeriod;
import cc.blynk.server.model.widgets.Widget;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.08.15.
 */
public class Logger extends Widget {

    public Pin[] pins;

    public GraphPeriod period;

    public boolean showLegends;

}
