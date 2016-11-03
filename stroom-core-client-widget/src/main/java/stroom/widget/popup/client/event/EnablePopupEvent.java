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

package stroom.widget.popup.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;

public class EnablePopupEvent extends GwtEvent<EnablePopupEvent.Handler> {
    public interface Handler extends EventHandler {
        void onEnable(EnablePopupEvent event);
    }

    private static Type<Handler> TYPE;

    private final PresenterWidget<?> presenterWidget;

    private EnablePopupEvent(final PresenterWidget<?> presenterWidget) {
        this.presenterWidget = presenterWidget;
    }

    public static void fire(final HasHandlers handlers, final PresenterWidget<?> presenterWidget) {
        handlers.fireEvent(new EnablePopupEvent(presenterWidget));
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }
        return TYPE;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(final Handler handler) {
        handler.onEnable(this);
    }

    public PresenterWidget<?> getPresenterWidget() {
        return presenterWidget;
    }
}
