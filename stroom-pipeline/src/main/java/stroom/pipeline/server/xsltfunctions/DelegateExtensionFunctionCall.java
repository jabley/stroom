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

package stroom.pipeline.server.xsltfunctions;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;

public class DelegateExtensionFunctionCall extends ExtensionFunctionCall {
    private final String functionName;
    private final Class<?> delegateClass;
    private transient StroomExtensionFunctionCall delegate;

    public DelegateExtensionFunctionCall(final String functionName, final Class<?> delegateClass) {
        this.functionName = functionName;
        this.delegateClass = delegateClass;
    }

    @Override
    public Sequence call(final XPathContext context, final Sequence[] arguments) throws XPathException {
        return delegate.call(functionName, context, arguments);
    }

    public void setDelegate(final StroomExtensionFunctionCall delegate) {
        this.delegate = delegate;
    }

    public Class<?> getDelegateClass() {
        return delegateClass;
    }
}
