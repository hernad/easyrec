/*
 * Copyright 2011 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.easyrec.plugin.stats;

/**
 * @author pmarschik
 */
public class StatisticsConstants {
    public static final GeneratorStatistics STATS_FORCED_END = new ForcedEndStatistics();
    public static final GeneratorStatistics STATS_EXECUTION_FAILED = new ExecutionFailedStatistics();
    public static final GeneratorStatistics STATS_MARSHAL_FAILED = new MarshalFailedStatistics();
    public static final GeneratorStatistics STATS_UNMARSHAL_FAILED = new UnmarshalFailedStatistics();

    public static class ErrorStatistics extends GeneratorStatistics {
    }

    public static class ForcedEndStatistics extends ErrorStatistics {
    }

    public static class ExecutionFailedStatistics extends ErrorStatistics {
        private static Throwable throwable;

        public ExecutionFailedStatistics() { this(null); }

        public ExecutionFailedStatistics(Throwable throwable) {
            this.throwable = throwable;
        }

        public static Throwable getThrowable() {
            return throwable;
        }
    }

    public static class MarshalFailedStatistics extends ErrorStatistics {
    }

    public static class UnmarshalFailedStatistics extends ErrorStatistics {
    }
}
