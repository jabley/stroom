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

package stroom.xmlschema.client.view;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

import stroom.widget.tickbox.client.view.TickBox;
import stroom.xmlschema.client.presenter.XMLSchemaSettingsPresenter.XMLSchemaSettingsView;

public class XMLSchemaSettingsViewImpl extends ViewImpl implements XMLSchemaSettingsView {
    public interface Binder extends UiBinder<Widget, XMLSchemaSettingsViewImpl> {
    }

    private final Widget widget;

    @UiField
    TextArea description;
    @UiField
    TextBox namespaceURI;
    @UiField
    TextBox systemId;
    @UiField
    TextBox schemaGroup;
    @UiField
    TickBox deprecated;

    @Inject
    public XMLSchemaSettingsViewImpl(final Binder binder) {
        widget = binder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public TextArea getDescription() {
        return description;
    }

    @Override
    public TextBox getNamespaceURI() {
        return namespaceURI;
    }

    @Override
    public TextBox getSystemId() {
        return systemId;
    }

    @Override
    public TextBox getSchemaGroup() {
        return schemaGroup;
    }

    @Override
    public TickBox getDeprecated() {
        return deprecated;
    }
}
