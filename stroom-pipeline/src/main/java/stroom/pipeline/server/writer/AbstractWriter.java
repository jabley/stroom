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

package stroom.pipeline.server.writer;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stroom.io.EncodingWriter;
import stroom.io.MultiOutputStream;
import stroom.io.NullOutputStream;
import stroom.io.OutputStreamWrapper;
import stroom.pipeline.destination.Destination;
import stroom.pipeline.destination.DestinationProvider;
import stroom.pipeline.server.errorhandler.ErrorReceiver;
import stroom.pipeline.server.errorhandler.ErrorReceiverProxy;
import stroom.pipeline.server.errorhandler.FatalErrorReceiver;
import stroom.pipeline.server.errorhandler.ProcessException;
import stroom.pipeline.server.factory.HasTargets;
import stroom.pipeline.server.factory.PipelineFactoryException;
import stroom.pipeline.server.factory.Target;
import stroom.pipeline.server.filter.AbstractXMLFilter;
import stroom.util.io.StreamUtil;
import stroom.util.shared.Severity;

public abstract class AbstractWriter extends AbstractXMLFilter implements Target, HasTargets {
    private static final NullOutputStream NULL_OUTPUT_STREAM = new NullOutputStream();

    private final ErrorReceiver errorReceiver;
    private final List<DestinationProvider> destinationProviders = new ArrayList<>();
    private final Map<Destination, DestinationProvider> borrowedDestinations = new HashMap<>();
    private final OutputStreamWrapper outputStream = new OutputStreamWrapper();

    private EncodingWriter writer;
    private String encoding;

    public AbstractWriter() {
        this.errorReceiver = new FatalErrorReceiver();
        outputStream.setOutputStream(NULL_OUTPUT_STREAM);
    }

    public AbstractWriter(final ErrorReceiverProxy errorReceiverProxy) {
        this.errorReceiver = errorReceiverProxy;
        outputStream.setOutputStream(NULL_OUTPUT_STREAM);
    }

    protected OutputStreamWrapper getOutputStream() {
        return outputStream;
    }

    protected Writer getWriter() {
        if (writer == null) {
            if (getDestinationProviders().size() == 0) {
                throw new ProcessException("No destination providers have been set");
            }
            if (writer != null) {
                throw new IllegalStateException("Stream writer is not null");
            }

            Charset charset = StreamUtil.DEFAULT_CHARSET;
            if (encoding != null && encoding.length() > 0) {
                try {
                    charset = Charset.forName(encoding);
                } catch (final Exception e) {
                    error("Unsupported encoding '" + encoding + "', defaulting to UTF-8", e);
                }
            }

            writer = new EncodingWriter(outputStream, charset);
        }

        return writer;
    }

    protected Charset getCharset() {
        Charset charset = StreamUtil.DEFAULT_CHARSET;
        if (encoding != null && encoding.length() > 0) {
            try {
                charset = Charset.forName(encoding);
            } catch (final Exception e) {
            }
        }
        return charset;
    }

    protected void borrowDestinations(final byte[] header, final byte[] footer) {
        if (borrowedDestinations.size() == 0) {
            final List<OutputStream> outputStreams = new ArrayList<>(destinationProviders.size());
            for (final DestinationProvider destinationProvider : destinationProviders) {
                try {
                    final Destination destination = destinationProvider.borrowDestination();
                    if (destination != null) {
                        borrowedDestinations.put(destination, destinationProvider);
                        final OutputStream outputStream = destination.getOutputStream(header, footer);
                        if (outputStream != null) {
                            outputStreams.add(outputStream);
                        }
                    }
                } catch (final Throwable t) {
                    error(t);
                }
            }

            if (outputStreams.size() == 0) {
                outputStream.setOutputStream(NULL_OUTPUT_STREAM);
            } else if (outputStreams.size() == 1) {
                outputStream.setOutputStream(outputStreams.get(0));
            } else {
                OutputStream[] arr = new OutputStream[outputStreams.size()];
                arr = outputStreams.toArray(arr);
                outputStream.setOutputStream(new MultiOutputStream(arr));
            }
        }
    }

    protected void returnDestinations() {
        // Ensure the encoding writer is flushed before we return the
        // destinations to the providers.
        if (writer != null) {
            try {
                writer.flush();
            } catch (final Throwable t) {
                fatal(t);
            }
        }

        for (final Entry<Destination, DestinationProvider> entry : borrowedDestinations.entrySet()) {
            try {
                entry.getValue().returnDestination(entry.getKey());
            } catch (final Throwable t) {
                error(t);
            }
        }
        borrowedDestinations.clear();
        outputStream.setOutputStream(NULL_OUTPUT_STREAM);
    }

    @Override
    public void addTarget(final Target target) {
        if (!(target instanceof DestinationProvider)) {
            throw new PipelineFactoryException("Attempt to link to an element that is not a destination: "
                    + getElementId() + " > " + target.getElementId());
        }

        final DestinationProvider destinationProvider = (DestinationProvider) target;
        destinationProviders.add(destinationProvider);
    }

    @Override
    public void setTarget(final Target target) {
        destinationProviders.clear();
        if (target != null) {
            addTarget(target);
        }
    }

    @Override
    public void startProcessing() {
        for (final DestinationProvider destinationProvider : destinationProviders) {
            destinationProvider.startProcessing();
        }
        super.startProcessing();
    }

    /**
     * Called by the pipeline when processing of a file is complete.
     *
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#endProcessing()
     */
    @Override
    public void endProcessing() {
        // Ensure all destinations are returned.
        returnDestinations();

        for (final DestinationProvider destinationProvider : destinationProviders) {
            try {
                destinationProvider.endProcessing();
            } catch (final Exception e) {
                fatal(e);
            }
        }

        super.endProcessing();
    }

    @Override
    public void startStream() {
        for (final DestinationProvider destinationProvider : destinationProviders) {
            destinationProvider.startStream();
        }
        super.startStream();
    }

    @Override
    public void endStream() {
        for (final DestinationProvider destinationProvider : destinationProviders) {
            destinationProvider.endStream();
        }
        super.endStream();
    }

    protected void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    protected ErrorReceiver getErrorReceiver() {
        return errorReceiver;
    }

    protected List<DestinationProvider> getDestinationProviders() {
        return destinationProviders;
    }

    protected void info(final String message, final Throwable t) {
        errorReceiver.log(Severity.INFO, null, getElementId(), message, t);
    }

    protected void warning(final String message, final Throwable t) {
        errorReceiver.log(Severity.WARNING, null, getElementId(), message, t);
    }

    protected void error(final String message, final Throwable t) {
        errorReceiver.log(Severity.ERROR, null, getElementId(), message, t);
    }

    protected void fatal(final String message, final Throwable t) {
        errorReceiver.log(Severity.FATAL_ERROR, null, getElementId(), message, t);
    }

    protected void info(final Throwable t) {
        info(t.getMessage(), t);
    }

    protected void warning(final Throwable t) {
        warning(t.getMessage(), t);
    }

    protected void error(final Throwable t) {
        error(t.getMessage(), t);
    }

    protected void fatal(final Throwable t) {
        fatal(t.getMessage(), t);
    }
}
