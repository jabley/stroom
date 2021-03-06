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

package stroom.pipeline.server.filter;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import stroom.pipeline.server.errorhandler.ErrorReceiverProxy;
import stroom.pipeline.server.errorhandler.ProcessException;
import stroom.pipeline.server.factory.ConfigurableElement;
import stroom.pipeline.server.factory.ElementIcons;
import stroom.pipeline.shared.data.PipelineElementType;
import stroom.pipeline.shared.data.PipelineElementType.Category;
import stroom.pipeline.state.StreamHolder;
import stroom.streamstore.shared.Stream;
import stroom.util.shared.Severity;
import stroom.util.spring.StroomScope;

/**
 * A SAX filter used to count the number of first level elements in an XML
 * instance. The first level elements are assumed to be records in the context
 * of event processing.
 */
@Component
@Scope(StroomScope.PROTOTYPE)
@ConfigurableElement(type = "IdEnrichmentFilter", category = Category.FILTER, roles = { PipelineElementType.ROLE_TARGET,
        PipelineElementType.ROLE_HAS_TARGETS, PipelineElementType.VISABILITY_STEPPING,
        PipelineElementType.ROLE_MUTATOR }, icon = ElementIcons.ID)
public class IdEnrichmentFilter extends AbstractXMLFilter {
    private static final String URI = "";
    private static final String STRING = "string";
    private static final String STREAM_ID = "StreamId";
    private static final String EVENT_ID = "EventId";

    @Resource
    private StreamHolder streamHolder;
    @Resource
    private ErrorReceiverProxy errorReceiverProxy;

    private int depth;
    private long count;

    // These variables are used in search result output.
    private String streamId;
    private long[] eventIds;

    /**
     * This method tells filters that a stream is about to be parsed so that
     * they can complete any setup necessary.
     *
     * @throws SAXException
     *             Could be thrown by an implementing class.
     */
    @Override
    public void startStream() {
        try {
            if (this.streamId == null) {
                final Stream stream = streamHolder.getStream();
                if (stream != null) {
                    streamId = String.valueOf(stream.getId());
                } else {
                    final String msg = "No stream set in stream holder";
                    errorReceiverProxy.log(Severity.WARNING, null, getElementId(), msg, new ProcessException(msg));
                }
            }
            depth = 0;
        } finally {
            super.startStream();
        }
    }

    @Override
    public void endStream() {
        try {
            super.endStream();
        } finally {
            depth = 0;
        }
    }

    /**
     * Fired on start element.
     *
     * @param uri
     *            the Namespace URI, or the empty string if the element has no
     *            Namespace URI or if Namespace processing is not being
     *            performed
     * @param localName
     *            the local name (without prefix), or the empty string if
     *            Namespace processing is not being performed
     * @param qName
     *            the qualified name (with prefix), or the empty string if
     *            qualified names are not available
     * @param atts
     *            the attributes attached to the element. If there are no
     *            attributes, it shall be an empty Attributes object. The value
     *            of this object after startElement returns is undefined
     * @throws org.xml.sax.SAXException
     *             any SAX exception, possibly wrapping another exception
     * @see #endElement
     * @see org.xml.sax.Attributes
     * @see org.xml.sax.helpers.AttributesImpl
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
            throws SAXException {
        Attributes newAtts = atts;

        depth++;

        if (depth == 2) {
            // Modify the attributes if this is an event element so that a
            // unique ID is inserted.
            if (streamId != null) {
                String eventId = null;
                // If we are using this is search result output then we need to
                // get event ids from a list.
                if (eventIds != null) {
                    final int index = (int) count;
                    if (eventIds.length <= index) {
                        final String msg = "Unexpected number of events being extracted";
                        final ProcessException searchException = new ProcessException(msg);
                        errorReceiverProxy.log(Severity.FATAL_ERROR, null, getElementId(), msg, searchException);
                        throw searchException;
                    }
                    eventId = String.valueOf(eventIds[index]);
                } else {
                    eventId = String.valueOf(count + 1);
                }

                // This is a first level element.
                count++;

                final AttributesImpl idAtts = new AttributesImpl(newAtts);

                // Remove any existing id attribute.
                int index = atts.getIndex(STREAM_ID);
                if (index != -1) {
                    idAtts.removeAttribute(index);
                }
                index = atts.getIndex(EVENT_ID);
                if (index != -1) {
                    idAtts.removeAttribute(index);
                }

                // Add the ids to the element.
                idAtts.addAttribute(URI, STREAM_ID, STREAM_ID, STRING, streamId);
                idAtts.addAttribute(URI, EVENT_ID, EVENT_ID, STRING, eventId);

                newAtts = idAtts;
            }
        }

        super.startElement(uri, localName, qName, newAtts);
    }

    /**
     * Fired on an end element.
     *
     * @param uri
     *            the Namespace URI, or the empty string if the element has no
     *            Namespace URI or if Namespace processing is not being
     *            performed
     * @param localName
     *            the local name (without prefix), or the empty string if
     *            Namespace processing is not being performed
     * @param qName
     *            the qualified XML name (with prefix), or the empty string if
     *            qualified names are not available
     * @throws org.xml.sax.SAXException
     *             any SAX exception, possibly wrapping another exception
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        depth--;
    }

    public void setup(final String streamId, final long[] eventIds) {
        this.streamId = streamId;
        this.eventIds = eventIds;
    }
}
