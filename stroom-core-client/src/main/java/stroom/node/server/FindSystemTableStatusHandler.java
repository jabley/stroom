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

package stroom.node.server;

import javax.annotation.Resource;

import stroom.entity.shared.BaseResultList;
import stroom.entity.shared.ResultList;
import stroom.node.shared.FindSystemTableStatusAction;
import stroom.node.shared.NodeService;
import stroom.node.shared.SystemTableStatus;
import stroom.task.server.AbstractTaskHandler;
import stroom.task.server.TaskHandlerBean;

@TaskHandlerBean(task = FindSystemTableStatusAction.class)
public class FindSystemTableStatusHandler
        extends AbstractTaskHandler<FindSystemTableStatusAction, ResultList<SystemTableStatus>> {
    @Resource
    private NodeService nodeService;

    @Override
    public BaseResultList<SystemTableStatus> exec(final FindSystemTableStatusAction action) {
        return BaseResultList.createUnboundedList(nodeService.findSystemTableStatus());
    }
}
