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

package stroom.monitoring.shared;

import stroom.entity.shared.EntityRow;
import stroom.node.shared.Node;
import stroom.util.shared.Expander;
import stroom.util.shared.TreeRow;

public class NodeRow extends EntityRow<Node>implements TreeRow {
    private static final long serialVersionUID = -2511849708703770119L;

    private Expander expander;

    public NodeRow() {
        // Default constructor necessary for GWT serialisation.
    }

    public NodeRow(final Expander expander, final Node node) {
        super(node);
        this.expander = expander;
    }

    @Override
    public Expander getExpander() {
        return expander;
    }
}
