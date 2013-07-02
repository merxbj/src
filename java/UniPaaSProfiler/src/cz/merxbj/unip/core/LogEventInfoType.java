package cz.merxbj.unip.core;

import cz.merxbj.unip.common.CommonStatics;
import java.awt.Color;

/**
 *
 * @author mrneo
 */
public enum LogEventInfoType {
    ACTION, INFO, ERROR, UNKNOWN;
    
    public static Color getColor(LogEventInfoType type) {
        return (type == ERROR) ?  CommonStatics.ERROR : CommonStatics.DEFAULT_BACKGROUND;
    }
}