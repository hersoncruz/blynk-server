package cc.blynk.server.model;

import cc.blynk.server.model.widgets.Widget;
import cc.blynk.server.model.widgets.others.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class DashBoard {

    public int id;

    public String name;

    public Long timestamp;

    public Widget[] widgets;

    public String boardType;

    public boolean keepScreenOn;

    public List<Timer> getTimerWidgets() {
        if (widgets == null || widgets.length == 0) {
            return Collections.emptyList();
        }

        List<Timer> timerWidgets = new ArrayList<>();
        for (Widget widget : widgets) {
            if (widget instanceof Timer) {
                Timer timer = (Timer) widget;
                if ((timer.startTime != null && timer.startValue != null && !timer.startValue.equals("")) ||
                    (timer.stopTime != null && timer.stopValue != null && !timer.stopValue.equals(""))) {
                    timerWidgets.add(timer);
                }
            }
        }

        return timerWidgets;
    }

    public  <T> T getWidgetByType(Class<T> clazz) {
        if (widgets == null || widgets.length == 0) {
            return null;
        }

        for (Widget widget : widgets) {
            if (clazz.isInstance(widget)) {
                return clazz.cast(widget);
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashBoard dashBoard = (DashBoard) o;

        if (id != dashBoard.id) return false;
        if (name != null ? !name.equals(dashBoard.name) : dashBoard.name != null) return false;
        if (!Arrays.equals(widgets, dashBoard.widgets)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (widgets != null ? Arrays.hashCode(widgets) : 0);
        return result;
    }
}
