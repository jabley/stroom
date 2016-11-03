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

package stroom.importexport.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

import stroom.entity.shared.EntityActionConfirmation;
import stroom.util.shared.ResourceKey;

public class ImportConfigConfirmEvent extends GwtEvent<ImportConfigConfirmEvent.Handler> {
    public interface Handler extends EventHandler {
        void onConfirmImport(ImportConfigConfirmEvent event);
    }

    private static Type<Handler> TYPE;

    private ResourceKey resourceKey;
    private List<EntityActionConfirmation> confirmList;

    private ImportConfigConfirmEvent(final ResourceKey resourceKey, final List<EntityActionConfirmation> confirmList) {
        this.resourceKey = resourceKey;
        this.confirmList = confirmList;
    }

    public static void fire(final HasHandlers source, final ResourceKey resourceKey,
            final List<EntityActionConfirmation> confirmList) {
        source.fireEvent(new ImportConfigConfirmEvent(resourceKey, confirmList));
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(final Handler handler) {
        handler.onConfirmImport(this);
    }

    public ResourceKey getResourceKey() {
        return resourceKey;
    }

    public List<EntityActionConfirmation> getConfirmList() {
        return confirmList;
    }
}
