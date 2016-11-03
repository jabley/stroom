/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.pipeline.server.errorhandler;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import stroom.pipeline.server.ErrorWriterProxy;
import stroom.util.shared.Location;
import stroom.util.shared.Severity;
import stroom.util.shared.StoredError;
import stroom.util.spring.StroomScope;

/**
 * Flags records that contain errors so that the RecordOutputFilter can exclude
 * them from the output.
 */
@Component
@Scope(value = StroomScope.TASK)
public class RecordErrorReceiver implements ErrorReceiver, ErrorStatistics {
    private Map<Severity, StoredErrorStats> statsMap = new TreeMap<Severity, StoredErrorStats>();

    public static final int MAX_TOTAL_WRITTEN_MARKERS = 1000;

    @Resource
    private ErrorWriterProxy errorWriter;

    @Override
    public void log(final Severity severity, final Location location, final String elementId, final String message,
            final Throwable e) {
        final String msg = MessageUtil.getMessage(message, e);

        // Record the number of errors.
        StoredErrorStats stats = statsMap.get(severity);
        if (stats == null) {
            stats = new StoredErrorStats(Severity.FATAL_ERROR.equals(severity));
            statsMap.put(severity, stats);
        }
        stats.increment();

        // Only store this message for the current record if one hasn't been
        // stored already or if the new message is more severe.
        if (stats.getCurrentError() == null) {
            stats.setCurrentError(new StoredError(severity, location, elementId, msg));
        }

        if (stats.getTotalCount() <= MAX_TOTAL_WRITTEN_MARKERS) {
            errorWriter.log(severity, location, elementId, msg);
        }
    }

    @Override
    public boolean isAllOk() {
        return getTotal(Severity.ERROR) == 0 && getTotal(Severity.FATAL_ERROR) == 0;
    }

    @Override
    public String getMessage() {
        for (final Severity severity : statsMap.keySet()) {
            final StoredErrorStats stats = statsMap.get(severity);
            if (stats != null) {
                final StoredError error = stats.getCurrentError();
                if (error != null) {
                    return error.toString();
                }
            }
        }

        return null;
    }

    @Override
    public long getTotal(final Severity severity) {
        // Make sure there is a final call to check record to add all current
        // errors to the total error count.
        checkRecord(-1);

        final StoredErrorStats stats = statsMap.get(severity);
        if (stats != null) {
            return stats.getTotalCount();

        }

        return 0;
    }

    @Override
    public long getRecords(final Severity severity) {
        // Make sure there is a final call to check record to add all current
        // errors to the total error count.
        checkRecord(-1);

        final StoredErrorStats stats = statsMap.get(severity);
        if (stats != null) {
            return stats.getRecordCount();

        }

        return 0;
    }

    @Override
    public Collection<Severity> getSeverities() {
        return statsMap.keySet();
    }

    @Override
    public long checkRecord(final long location) {
        long result = 0;
        for (final Entry<Severity, StoredErrorStats> entry : statsMap.entrySet()) {
            final Severity severity = entry.getKey();
            final StoredErrorStats storedErrorStats = entry.getValue();
            final long currentCount = storedErrorStats.checkRecord();
            if (severity.greaterThanOrEqual(Severity.ERROR)) {
                result += currentCount;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        final String message = getMessage();
        if (message != null) {
            return message;
        }

        return "";
    }

    @Override
    public void reset() {
        final StoredErrorStats storedErrorStats = statsMap.get(Severity.FATAL_ERROR);
        if (storedErrorStats != null) {
            storedErrorStats.reset();
        }
    }
}
