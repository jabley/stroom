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

package stroom.streamstore.client.view;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import stroom.alert.client.event.AlertEvent;
import stroom.dispatch.client.AsyncCallbackAdaptor;
import stroom.node.client.ClientPropertyCache;
import stroom.node.shared.ClientProperties;

public class ClassificationLabel extends Composite {
    private static class LabelColour {
        private final String name;
        private final String colour;

        public LabelColour(final String name, final String colour) {
            this.name = name;
            this.colour = colour;
        }

        public String getName() {
            return name;
        }

        public String getColour() {
            return colour;
        }
    }

    private static Binder binder = GWT.create(Binder.class);

    public interface Binder extends UiBinder<Widget, ClassificationLabel> {
    }

    @UiField
    Label classification;

    private final List<LabelColour> labelColours = new ArrayList<LabelColour>();

    @Inject
    public ClassificationLabel(final ClientPropertyCache clientPropertyCache) {
        initWidget(binder.createAndBindUi(this));

        clientPropertyCache.get(new AsyncCallbackAdaptor<ClientProperties>() {
            @Override
            public void onSuccess(final ClientProperties result) {
                final String csv = result.get(ClientProperties.LABEL_COLOURS);
                if (csv != null) {
                    final String[] parts = csv.split(",");
                    for (final String part : parts) {
                        final String[] kv = part.split("=");
                        if (kv.length == 2) {
                            final LabelColour labelColour = new LabelColour(kv[0].toUpperCase(), kv[1]);
                            labelColours.add(labelColour);
                        }
                    }
                }
            }

            @Override
            public void onFailure(final Throwable caught) {
                AlertEvent.fireError(ClassificationLabel.this, caught.getMessage(), null);
            }
        });
    }

    public void setClassification(final String text) {
        String upper = text;
        if (upper == null) {
            upper = "";
        }

        upper = upper.toUpperCase();
        classification.setText(upper);

        classification.getElement().getStyle().setColor("black");

        String colour = "#888888";
        for (final LabelColour labelColour : labelColours) {
            if (upper.indexOf(labelColour.getName()) != -1) {
                colour = labelColour.getColour();
                break;
            }
        }

        classification.getElement().getStyle().setBackgroundColor(colour);
    }
}
