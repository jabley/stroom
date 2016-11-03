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

package stroom.entity.client.event;

import java.util.List;

import stroom.explorer.shared.EntityData;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

import stroom.explorer.shared.ExplorerData;

public class ShowCreateEntityDialogEvent extends GwtEvent<ShowCreateEntityDialogEvent.Handler> {
    public interface Handler extends EventHandler {
        void onCreate(final ShowCreateEntityDialogEvent event);
    }

    private static Type<Handler> TYPE;

    private final ExplorerData selected;
    private final String entityType;
    private final String entityDisplayType;
    private final boolean allowNullFolder;

    private ShowCreateEntityDialogEvent(final ExplorerData selected, final String entityType, final String entityDisplayType, final boolean allowNullFolder) {
        this.selected = selected;
        this.entityType = entityType;
        this.entityDisplayType = entityDisplayType;
        this.allowNullFolder = allowNullFolder;
    }

    public static void fire(final HasHandlers handlers, final ExplorerData selected, final String entityType, final String entityDisplayType, final boolean allowNullFolder) {
        handlers.fireEvent(
                new ShowCreateEntityDialogEvent(selected, entityType, entityDisplayType, allowNullFolder));
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    @Override
    public final Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final Handler handler) {
        handler.onCreate(this);
    }

    public ExplorerData getSelected() {
        return selected;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityDisplayType() {
        return entityDisplayType;
    }

    public boolean isAllowNullFolder() {
        return allowNullFolder;
    }
}