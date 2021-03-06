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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import stroom.util.spring.StroomScope;
import stroom.util.spring.StroomSpringProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import stroom.entity.server.util.XMLUtil;
import stroom.pipeline.server.LocationFactoryProxy;
import stroom.pipeline.server.errorhandler.ErrorListenerAdaptor;
import stroom.pipeline.server.errorhandler.ErrorReceiverProxy;
import stroom.pipeline.server.errorhandler.LoggedException;
import stroom.pipeline.server.factory.ConfigurableElement;
import stroom.pipeline.server.factory.ElementIcons;
import stroom.pipeline.shared.data.PipelineElementType;
import stroom.util.logging.StroomLogger;
import stroom.util.shared.Severity;

/**
 * A filter used to sample the output produced by SAX events at any point in the
 * XML pipeline. Many instances of this filter can be used.
 */
@Component
@Scope(StroomScope.TASK)
@Profile(StroomSpringProfiles.TEST)
@ConfigurableElement(type = "TestFilter", roles = { PipelineElementType.ROLE_TARGET,
        PipelineElementType.ROLE_HAS_TARGETS, PipelineElementType.VISABILITY_SIMPLE,
        PipelineElementType.VISABILITY_STEPPING }, icon = ElementIcons.STREAM)
public class TestFilter extends AbstractXMLFilter {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private ContentHandler handler;

    private static StroomLogger LOGGER = StroomLogger.getLogger(TestFilter.class);

    @Resource
    private ErrorReceiverProxy errorReceiverProxy;
    @Resource
    private LocationFactoryProxy locationFactory;

    private ErrorListener errorListener;

    /**
     * @throws SAXException
     *             Not thrown.
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#startProcessing()
     */
    @Override
    public void startProcessing() {
        errorListener = new ErrorListenerAdaptor(getElementId(), locationFactory, errorReceiverProxy);

        try {
            final TransformerHandler th = XMLUtil.createTransformerHandler(errorListener, false);
            th.setResult(new StreamResult(outputStream));

            handler = th;

        } catch (final TransformerConfigurationException e) {
            errorReceiverProxy.log(Severity.FATAL_ERROR,
                    locationFactory.create(e.getLocator().getLineNumber(), e.getLocator().getColumnNumber()),
                    getElementId(), e.getMessage(), e);
            throw new LoggedException(e.getMessage(), e);
        } finally {
            super.startProcessing();
        }
    }

    @Override
    public void endProcessing() {
        try {
            outputStream.flush();
            outputStream.close();

            if (LOGGER.isDebugEnabled()) {
                try {
                    LOGGER.debug(XMLUtil.prettyPrintXML(outputStream.toString()));
                } catch (final Exception ex) {
                    LOGGER.debug("Not XML");
                    LOGGER.debug(outputStream.toString());
                }
            }

        } catch (final IOException e) {
            try {
                errorListener.fatalError(new TransformerException(e.getMessage()));
            } catch (final TransformerException te) {
                LOGGER.fatal(te, te);
            }
        } finally {
            super.endProcessing();
        }
    }

    /**
     * @param locator
     *            an object that can return the location of any SAX document
     *            event
     *
     * @see org.xml.sax.Locator
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#setDocumentLocator(org.xml.sax.Locator)
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        handler.setDocumentLocator(locator);
        super.setDocumentLocator(locator);
    }

    /**
     * @throws org.xml.sax.SAXException
     *             any SAX exception, possibly wrapping another exception
     *
     * @see #endDocument
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        handler.startDocument();
        super.startDocument();
    }

    /**
     * @throws org.xml.sax.SAXException
     *             any SAX exception, possibly wrapping another exception
     *
     * @see #startDocument
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        handler.endDocument();
        super.endDocument();
    }

    /**
     * @param prefix
     *            the Namespace prefix being declared. An empty string is used
     *            for the default element namespace, which has no prefix.
     * @param uri
     *            the Namespace URI the prefix is mapped to
     * @throws org.xml.sax.SAXException
     *             the client may throw an exception during processing
     *
     * @see #endPrefixMapping
     * @see #startElement
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#startPrefixMapping(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        handler.startPrefixMapping(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    /**
     * @param prefix
     *            the prefix that was being mapped. This is the empty string
     *            when a default mapping scope ends.
     * @throws org.xml.sax.SAXException
     *             the client may throw an exception during processing
     *
     * @see #startPrefixMapping
     * @see #endElement
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#endPrefixMapping(java.lang.String)
     */
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        handler.endPrefixMapping(prefix);
        super.endPrefixMapping(prefix);
    }

    /**
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
     *
     * @see #endElement
     * @see org.xml.sax.Attributes
     * @see org.xml.sax.helpers.AttributesImpl
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
            throws SAXException {
        handler.startElement(uri, localName, qName, atts);
        super.startElement(uri, localName, qName, atts);
    }

    /**
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
     *
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        handler.endElement(uri, localName, qName);
        super.endElement(uri, localName, qName);
    }

    /**
     * @param ch
     *            the characters from the XML document
     * @param start
     *            the start position in the array
     * @param length
     *            the number of characters to read from the array
     * @throws org.xml.sax.SAXException
     *             any SAX exception, possibly wrapping another exception
     *
     * @see #ignorableWhitespace
     * @see org.xml.sax.Locator
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#characters(char[],
     *      int, int)
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        handler.characters(ch, start, length);
        super.characters(ch, start, length);
    }

    /**
     * @param ch
     *            the characters from the XML document
     * @param start
     *            the start position in the array
     * @param length
     *            the number of characters to read from the array
     * @throws org.xml.sax.SAXException
     *             any SAX exception, possibly wrapping another exception
     *
     * @see #characters
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#ignorableWhitespace(char[],
     *      int, int)
     */
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        handler.ignorableWhitespace(ch, start, length);
        super.ignorableWhitespace(ch, start, length);
    }

    /**
     * @param target
     *            the processing instruction target
     * @param data
     *            the processing instruction data, or null if none was supplied.
     *            The data does not include any whitespace separating it from
     *            the target
     * @throws org.xml.sax.SAXException
     *             any SAX exception, possibly wrapping another exception
     *
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#processingInstruction(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        handler.processingInstruction(target, data);
        super.processingInstruction(target, data);
    }

    /**
     * @param name
     *            the name of the skipped entity. If it is a parameter entity,
     *            the name will begin with '%', and if it is the external DTD
     *            subset, it will be the string "[dtd]"
     * @throws org.xml.sax.SAXException
     *             any SAX exception, possibly wrapping another exception
     *
     * @see stroom.pipeline.server.filter.AbstractXMLFilter#skippedEntity(java.lang.String)
     */
    @Override
    public void skippedEntity(final String name) throws SAXException {
        handler.skippedEntity(name);
        super.skippedEntity(name);
    }

    /**
     * @return The recorded output as a string.
     */
    public String getOutput() {
        return outputStream.toString();
    }
}
