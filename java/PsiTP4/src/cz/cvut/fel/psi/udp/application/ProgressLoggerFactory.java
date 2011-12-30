/*
 * SinkFactory
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package cz.cvut.fel.psi.udp.application;

import java.util.EnumMap;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public final class ProgressLoggerFactory {

    private static CommandLine.LogLevel logLevel;
    private static EnumMap<CommandLine.LogLevel, ProgressLogger> loggerCache;

    static {
        loggerCache = new EnumMap<CommandLine.LogLevel, ProgressLogger>(CommandLine.LogLevel.class);
    }
    
    private ProgressLoggerFactory() {
    }

    public static void setLogLevel(final CommandLine.LogLevel newLogLevel) {
        logLevel = newLogLevel;
    }

    public static ProgressLogger getLogger() {

        if (loggerCache.containsKey(logLevel)) {
            return loggerCache.get(logLevel);
        }

        ProgressLogger newLogger = null;
        switch (logLevel) {
            case Verboose:
                newLogger = new VerbooseProgressLogger();
                break;
            case Normal:
                newLogger = new NormalProgressSink();
                break;
            case Simple:
                newLogger = new SimpleProgressLogger();
                break;
            default:
                throw new RuntimeException("Unexpected logging level! Cannnot create logger!");
        }

        loggerCache.put(logLevel, newLogger);

        return newLogger;
    }
}
