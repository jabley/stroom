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

package stroom.util;

import stroom.entity.shared.SQLNameConstants;
import stroom.feed.shared.Feed;
import stroom.node.shared.Volume;
import stroom.streamstore.shared.Stream;
import stroom.streamstore.shared.StreamAttributeConstants;
import stroom.streamstore.shared.StreamStatus;
import stroom.streamstore.shared.StreamType;
import stroom.util.concurrent.SimpleConcurrentMap;
import stroom.util.date.DateUtil;
import stroom.util.io.LineReader;
import stroom.util.io.StreamUtil;
import stroom.util.shared.ModelStringUtil;
import stroom.util.thread.ThreadScopeRunnable;
import stroom.util.zip.HeaderMap;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class StreamRestoreTool extends DatabaseTool {
    private String deleteFile = null;

    public void setDeleteFile(final String deleteFile) {
        this.deleteFile = deleteFile;
    }

    public static final int KEY_PAD = 30;
    public static final int COUNT_PAD = 10;

    static class KeyCount {
        public KeyCount(final String key) {
            this.key = Arrays.asList(key);
            this.count = new MutableInt();
        }

        public KeyCount(final List<String> key) {
            this.key = key;
            this.count = new MutableInt();
        }

        List<String> key;
        MutableInt count;

        public List<String> getKey() {
            return key;
        }

        public MutableInt getCount() {
            return count;
        }

        @Override
        public String toString() {
            return StringUtils.rightPad(getKey().toString(), KEY_PAD)
                    + StringUtils.leftPad(ModelStringUtil.formatCsv(getCount()), COUNT_PAD);
        }
    }

    private final BufferedReader inputReader = new BufferedReader(
            new InputStreamReader(System.in, StreamUtil.DEFAULT_CHARSET));

    private final SimpleConcurrentMap<String, KeyCount> streamTypeStreamCount = new SimpleConcurrentMap<String, KeyCount>() {
        @Override
        protected KeyCount initialValue(final String key) {
            return new KeyCount(key);
        }
    };
    private final SimpleConcurrentMap<List<String>, KeyCount> streamTypeFeedStreamCount = new SimpleConcurrentMap<List<String>, KeyCount>() {
        @Override
        protected KeyCount initialValue(final List<String> key) {
            return new KeyCount(key);
        }
    };
    private final SimpleConcurrentMap<String, SimpleConcurrentMap<String, SimpleConcurrentMap<String, KeyCount>>> streamTypeFeedDateStreamCount = new SimpleConcurrentMap<String, SimpleConcurrentMap<String, SimpleConcurrentMap<String, KeyCount>>>() {
        @Override
        protected SimpleConcurrentMap<String, SimpleConcurrentMap<String, KeyCount>> initialValue(final String key) {
            return new SimpleConcurrentMap<String, SimpleConcurrentMap<String, KeyCount>>() {
                @Override
                protected SimpleConcurrentMap<String, KeyCount> initialValue(final String key) {
                    return new SimpleConcurrentMap<String, KeyCount>() {
                        @Override
                        protected KeyCount initialValue(final String key) {
                            return new KeyCount(key);
                        }
                    };
                }
            };
        }
    };

    public String readLine(final String question) {
        try {
            System.out.print(question + " : ");
            final String line = inputReader.readLine();
            return line;
        } catch (final Exception ex) {
            handleException(ex);
            return null;
        }
    }

    @SuppressWarnings("DM_EXIT")
    private void handleException(final Exception ex) {
        ex.printStackTrace();
        writeLine(ex.getMessage());
        System.exit(1);
    }

    private Map<String, Long> pathStreamTypeMap = null;

    private Map<String, Long> getPathStreamTypeMap() throws SQLException {
        if (pathStreamTypeMap == null) {
            pathStreamTypeMap = new HashMap<>();

            final String sql = "select " + StreamType.PATH + "," + StreamType.ID + " from " + StreamType.TABLE_NAME;
            try (Statement statement = getConnection().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        pathStreamTypeMap.put(resultSet.getString(1), resultSet.getLong(2));
                    }
                    resultSet.close();
                }
                statement.close();
            }
        }
        return pathStreamTypeMap;
    }

    private Map<String, Long> pathVolumeMap = null;

    private Map<String, Long> getPathVolumeMap() throws SQLException {
        if (pathVolumeMap == null) {
            pathVolumeMap = new HashMap<>();
            final String sql = "select " + Volume.PATH + "," + Volume.ID + " from " + Volume.TABLE_NAME;
            try (Statement statement = getConnection().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        pathVolumeMap.put(resultSet.getString(1), resultSet.getLong(2));
                    }
                    resultSet.close();
                }
                statement.close();
            }
        }
        return pathVolumeMap;
    }

    private Map<Long, String> feedIdNameMap = null;

    private Map<Long, String> getFeedIdNameMap() throws SQLException {
        if (feedIdNameMap == null) {
            feedIdNameMap = new HashMap<>();
            try (Statement statement = getConnection().createStatement()) {
                try (ResultSet resultSet = statement
                        .executeQuery("select " + Feed.ID + "," + SQLNameConstants.NAME + " from " + Feed.TABLE_NAME)) {
                    while (resultSet.next()) {
                        feedIdNameMap.put(resultSet.getLong(1), resultSet.getString(2));
                    }
                    resultSet.close();
                }
                statement.close();
            }
        }
        return feedIdNameMap;
    }

    public void writeLine(final String msg) {
        System.out.println(msg);
    }

    private char readQuestion(final String question, final char[] options, final char def) {
        final String result = readLine(question + " " + Arrays.toString(options) + " " + def + "*").toLowerCase()
                .trim();

        if (result.length() == 0) {
            return def;
        }

        for (final char opt : options) {
            if (opt == result.charAt(0)) {
                return opt;
            }
        }
        return def;
    }

    @Override
    public void run() {
        new ThreadScopeRunnable() {
            @Override
            protected void exec() {
                doThreadRun();

            }
        }.run();
    }

    public void doThreadRun() {
        String fileName = null;

        if (deleteFile != null) {
            fileName = deleteFile;
        } else {
            fileName = readLine("Please enter file name to process");
        }
        try {
            final LineReader lineReader = new LineReader(new FileInputStream(fileName),
                    StreamUtil.DEFAULT_CHARSET_NAME);

            String line = null;
            while ((line = lineReader.nextLine()) != null) {
                if (line.contains(".")) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(line, "/");
                    while (stringTokenizer.hasMoreTokens()) {
                        final String part = stringTokenizer.nextToken();
                        if (part.equals("store")) {
                            break;
                        }
                    }
                    final String type = stringTokenizer.nextToken();
                    final String date = stringTokenizer.nextToken() + "/" + stringTokenizer.nextToken() + "/"
                            + stringTokenizer.nextToken();

                    final String file = line.substring(line.lastIndexOf("/"));
                    final String feed = file.substring(1, file.indexOf("="));

                    int dotCount = 0;
                    int dotPos = 0;
                    while ((dotPos = (file.indexOf('.', dotPos) + 1)) > 0) {
                        dotCount++;
                    }

                    if (dotCount == 2) {
                        streamTypeStreamCount.get(type).getCount().increment();
                        streamTypeFeedStreamCount.get(Arrays.asList(type, feed)).getCount().increment();
                        streamTypeFeedDateStreamCount.get(type).get(feed).get(date).getCount().increment();
                    }

                }

            }

            final List<KeyCount> sortedList = writeTable(streamTypeStreamCount.values(), "Stream Types");
            final Map<String, Character> streamTypeResponse = new HashMap<>();

            if (!inspect) {
                for (final KeyCount keyCount : sortedList) {
                    final char response = readQuestion(keyCount.toString() + " (D)elete, (R)estore, (I)nspect, (S)kip",
                            new char[] { 'd', 'r', 'i', 's' }, 's');

                    streamTypeResponse.put(keyCount.getKey().get(0), response);

                    if (response == 'd') {
                        processStreamTypeFeed(fileName, keyCount.getKey().get(0), null, response);
                    }

                }
            }

            writeLine("");

            final List<KeyCount> sortedStreamTypeFeed = new ArrayList<>(streamTypeFeedStreamCount.values());
            sort(sortedStreamTypeFeed);

            for (final KeyCount streamTypeFeed : sortedStreamTypeFeed) {
                final String streamType = streamTypeFeed.getKey().get(0);
                final String feed = streamTypeFeed.getKey().get(1);
                if (inspect || streamTypeResponse.get(streamType).charValue() == 'i') {
                    final String feedName = getFeedIdNameMap().get(Long.parseLong(feed));

                    final String longLabel = "Feed " + feed + " '" + feedName + "', Stream Type " + streamType;

                    writeTable(streamTypeFeedDateStreamCount.get(streamType).get(feed).values(), longLabel);

                    if (!inspect) {
                        if (autoDeleteThreshold != null && streamTypeFeedDateStreamCount.get(streamType).get(feed)
                                .keySet().size() < autoDeleteThreshold) {
                            writeLine(longLabel + " Lower than threshold ... deleting");

                            processStreamTypeFeed(fileName, streamType, feed, 'd');
                        } else {
                            final char response = readQuestion(longLabel + " (D)elete, (R)estore, (S)kip",
                                    new char[] { 'd', 'r', 's' }, 's');

                            if (response == 'd' || response == 'r') {
                                processStreamTypeFeed(fileName, streamType, feed, response);
                            }
                        }
                    }
                }
            }

        } catch (final IOException ioEx) {
            handleException(ioEx);
        } catch (final SQLException sqlException) {
            handleException(sqlException);
        } finally {
            closeConnection();
        }

    }

    private boolean mock = false;

    public void setMock(final boolean mock) {
        this.mock = mock;
    }

    private boolean inspect = false;

    public void setInspect(final boolean inspect) {
        this.inspect = inspect;
    }

    public Integer autoDeleteThreshold = null;

    public void setAutoDeleteThreshold(final Integer autoDeleteThreshold) {
        this.autoDeleteThreshold = autoDeleteThreshold;
    }

    private boolean sortKey = false;

    public void setSortKey(final boolean sortKey) {
        this.sortKey = sortKey;
    }

    private void sort(final List<KeyCount> list) {
        Collections.sort(list, (o1, o2) -> {
            if (sortKey) {
                return o1.getKey().toString().compareTo(o2.getKey().toString());
            } else {
                return o1.getCount().compareTo(o2.getCount());
            }
        });
    }

    public static final String VOLUME_PATH = "VolumePath";
    public static final String STREAM_TYPE_PATH = "StreamTypePath";
    public static final String FILE_NAME = "FileName";
    public static final String FEED_ID = "FeedId";
    public static final String DATE_PATH = "DatePath";
    public static final String DEPTH = "Depth";

    private Map<String, String> readAttributes(final String line, final String streamType, final String feedId) {
        final HashMap<String, String> rtnMap = new HashMap<>();

        final StringTokenizer stringTokenizer = new StringTokenizer(line, "/");
        final StringBuilder volumePath = new StringBuilder();
        while (stringTokenizer.hasMoreTokens()) {
            final String part = stringTokenizer.nextToken();
            if (part.equals("store")) {
                break;
            }
            volumePath.append("/");
            volumePath.append(part);
        }
        rtnMap.put(VOLUME_PATH, volumePath.toString());
        rtnMap.put(STREAM_TYPE_PATH, stringTokenizer.nextToken());
        rtnMap.put(DATE_PATH,
                stringTokenizer.nextToken() + "-" + stringTokenizer.nextToken() + "-" + stringTokenizer.nextToken());

        final String datePart = "YYYY-MM-DD";

        final String fileName = line.substring(line.lastIndexOf("/"));
        rtnMap.put(FILE_NAME, fileName);

        if (fileName.indexOf(".") > 0) {
            final int splitPos = fileName.indexOf("=");
            rtnMap.put(FEED_ID, fileName.substring(1, splitPos));
            rtnMap.put(StreamAttributeConstants.STREAM_ID, fileName.substring(splitPos + 1, fileName.indexOf(".")));

            int dotCount = 0;
            int dotPos = 0;
            while ((dotPos = (fileName.indexOf('.', dotPos) + 1)) > 0) {
                dotCount++;
            }
            rtnMap.put(DEPTH, String.valueOf(dotCount - 2));
        }

        // Inspect File? (Expensive)
        if ((streamType == null || streamType.equals(rtnMap.get(STREAM_TYPE_PATH)))
                && (feedId == null || feedId.equals(rtnMap.get(FEED_ID)))) {
            final File file = new File(line);
            if (file.exists()) {
                final String fileLastModified = DateUtil.createNormalDateTimeString(file.lastModified());

                rtnMap.put(StreamAttributeConstants.CREATE_TIME,
                        rtnMap.get(DATE_PATH) + fileLastModified.substring(datePart.length()));

            } else {
                rtnMap.put(StreamAttributeConstants.CREATE_TIME, rtnMap.get(DATE_PATH) + "T00:00:00.000Z");

            }
        }

        return rtnMap;
    }

    private Map<String, String> readManifestAttributes(final String rootFile) {
        final Map<String, String> rtnMap = new HashMap<>();
        final File manifest = new File(rootFile.substring(0, rootFile.lastIndexOf(".")) + ".mf.dat");
        if (manifest.isFile()) {
            final HeaderMap headerMap = new HeaderMap();
            try (FileInputStream inputStream = new FileInputStream(manifest)) {
                headerMap.read(inputStream, true);
            } catch (final IOException ioEx) {
            }
            rtnMap.putAll(headerMap);
        }
        return rtnMap;
    }

    public void processStreamTypeFeed(final String fileName, final String processStreamType, final String processFeedId,
            final char action) throws IOException, SQLException {
        final LineReader lineReader = new LineReader(new FileInputStream(fileName), StreamUtil.DEFAULT_CHARSET_NAME);

        String line = null;
        int lineCount = 0;
        int count = 0;

        long nextLog = System.currentTimeMillis() + 10000;

        while ((line = lineReader.nextLine()) != null) {
            final Map<String, String> streamAttributes = readAttributes(line, processStreamType, processFeedId);

            lineCount++;
            if (System.currentTimeMillis() > nextLog) {
                writeLine("Reading line " + lineCount + " " + line);
                nextLog = System.currentTimeMillis() + 10000;
            }

            if (processStreamType.equals(streamAttributes.get(STREAM_TYPE_PATH))
                    && (processFeedId == null || processFeedId.equals(streamAttributes.get(FEED_ID)))) {
                final File systemFile = new File(line);
                if (action == 'd') {
                    if (mock) {
                        writeLine("rm " + line);
                    } else {
                        writeLine("rm " + line);
                        systemFile.delete();
                    }
                }

                // Restore and a root file
                if (action == 'r' && "0".equals(streamAttributes.get(DEPTH))) {
                    streamAttributes.putAll(readManifestAttributes(line));

                    final Stream stream = new Stream();
                    stream.setId(Long.parseLong(streamAttributes.get(StreamAttributeConstants.STREAM_ID)));
                    stream.setVersion((byte) 1);

                    stream.setCreateMs(DateUtil
                            .parseNormalDateTimeString(streamAttributes.get(StreamAttributeConstants.CREATE_TIME)));
                    if (streamAttributes.containsKey(StreamAttributeConstants.EFFECTIVE_TIME)) {
                        stream.setEffectiveMs(DateUtil.parseNormalDateTimeString(
                                streamAttributes.get(StreamAttributeConstants.EFFECTIVE_TIME)));
                    }
                    if (stream.getEffectiveMs() == null) {
                        stream.setEffectiveMs(stream.getCreateMs());
                    }

                    if (streamAttributes.containsKey(StreamAttributeConstants.PARENT_STREAM_ID)) {
                        stream.setParentStreamId(
                                Long.valueOf(streamAttributes.get(StreamAttributeConstants.PARENT_STREAM_ID)));
                    }
                    stream.updateStatus(StreamStatus.UNLOCKED);
                    stream.setFeed(Feed.createStub(Long.valueOf(streamAttributes.get(FEED_ID))));
                    stream.setStreamType(
                            StreamType.createStub(getPathStreamTypeMap().get(streamAttributes.get(STREAM_TYPE_PATH))));

                    final String logInfo = StringUtils.leftPad(String.valueOf(stream.getId()), 10) + " "
                            + DateUtil.createNormalDateTimeString(stream.getCreateMs());

                    writeLine("Restore " + logInfo + " for file " + line);

                    if (!mock) {
                        try {
                            try (PreparedStatement statement1 = getConnection().prepareStatement(
                                    "insert into strm (id,ver, crt_ms,effect_ms, parnt_strm_id,stat, fk_fd_id,fk_strm_proc_id, fk_strm_tp_id) "
                                            + " values (?,1, ?,?, ?,?, ?,?, ?)")) {
                                int s1i = 1;
                                statement1.setLong(s1i++, stream.getId());

                                statement1.setLong(s1i++, stream.getCreateMs());
                                if (stream.getEffectiveMs() != null) {
                                    statement1.setLong(s1i++, stream.getEffectiveMs());
                                } else {
                                    statement1.setNull(s1i++, Types.BIGINT);
                                }

                                if (stream.getParentStreamId() != null) {
                                    statement1.setLong(s1i++, stream.getParentStreamId());
                                } else {
                                    statement1.setNull(s1i++, Types.BIGINT);
                                }
                                statement1.setByte(s1i++, stream.getStatus().getPrimitiveValue());

                                statement1.setLong(s1i++, stream.getFeed().getId());
                                statement1.setNull(s1i++, Types.BIGINT);

                                statement1.setLong(s1i++, stream.getStreamType().getId());

                                statement1.executeUpdate();
                                statement1.close();
                            }

                            try (PreparedStatement statement2 = getConnection().prepareStatement(
                                    "insert into strm_vol (ver, fk_strm_id,fk_vol_id) " + " values (1, ?,?)")) {
                                int s2i = 1;
                                statement2.setLong(s2i++, stream.getId());
                                statement2.setLong(s2i++, getPathVolumeMap().get(streamAttributes.get(VOLUME_PATH)));
                                statement2.executeUpdate();
                                statement2.close();
                            }
                        } catch (final Exception ex) {
                            writeLine("Failed " + logInfo + " " + ex.getMessage());
                        }
                    }
                    count++;
                }
            }
        }
        writeLine("Processed " + ModelStringUtil.formatCsv(count) + " count");

    }

    public ArrayList<KeyCount> writeTable(final Collection<KeyCount> values, final String heading) {
        writeLine("========================");
        writeLine(heading);
        writeLine("========================");
        final ArrayList<KeyCount> list = new ArrayList<>();
        list.addAll(values);
        sort(list);

        for (final KeyCount keyCount : list) {
            writeLine(StringUtils.rightPad(keyCount.getKey().toString(), KEY_PAD)
                    + StringUtils.leftPad(ModelStringUtil.formatCsv(keyCount.getCount()), COUNT_PAD));
        }
        writeLine("========================");
        return list;

    }

    public static void main(final String[] args) throws Exception {
        new StreamRestoreTool().doMain(args);
    }

}
