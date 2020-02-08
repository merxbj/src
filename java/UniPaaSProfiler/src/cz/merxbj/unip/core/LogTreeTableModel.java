package cz.merxbj.unip.core;

import cz.merxbj.unip.common.CommonStatics;
import cz.merxbj.unip.gui.tt.AbstractTreeTableModel;
import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

public class LogTreeTableModel extends AbstractTreeTableModel {

    protected static String[] columnNames = {"", "Type", "Start", "End", "Duration"};
    protected static int[] columnWidths = {0, 100, 150, 150, 70};
    protected static DateTimeFormatter timeFormatter = CommonStatics.TIME_FORMATTER;

    public LogTreeTableModel(String rootName) {
        super(null);

        root = new LogEvent(rootName);
    }

    @Override
    public int getChildCount(Object node) {
        ArrayList<LogEvent> children = ((LogEvent) node).getChilds();
        return (children == null) ? 0 : children.size();
    }

    @Override
    public Object getChild(Object node, int index) {
        if (node instanceof LogEvent) {
            if (((LogEvent) node).hasChild()) {
                return ((LogEvent) node).getChilds().get(index);
            }
        }
        return null;
    }

    @Override
    public boolean isLeaf(Object node) {
        if (node instanceof LogEvent) {
            return !(((LogEvent) node).hasChild());
        }
        return true;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return AbstractTreeTableModel.class;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        try {
            LogEvent logEvent = (LogEvent) node;

            switch (column) {
                case 0:
                    return logEvent.getDescription();
                case 1:
                    return (logEvent.getInfoType() != null) ? logEvent.getInfoType().toString() : "";
                case 2:
                    return (logEvent.getStartTimeStamp() != null) ? timeFormatter.print(logEvent.getStartTimeStamp()) : "";
                case 3:
                    return (logEvent.getEndTimeStamp() != null) ? timeFormatter.print(logEvent.getEndTimeStamp()) : "";
                case 4:
                    return (logEvent.getDuration() > 0 ) ? timeFormatter.print(new DateTime(logEvent.getDuration(), DateTimeZone.UTC)) : "";
            }
        } catch (SecurityException se) {
        }
        return null;
    }

    @Override
    public int getColumnWidth(int column) {
        return columnWidths[column];
    }
}