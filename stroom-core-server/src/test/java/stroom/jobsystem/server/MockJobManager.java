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

package stroom.jobsystem.server;

import stroom.util.spring.StroomSpringProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import stroom.jobsystem.shared.JobManager;
import stroom.node.shared.Node;

@Profile(StroomSpringProfiles.TEST)
@Component
public class MockJobManager implements JobManager {
    @Override
    public Boolean isJobEnabled(String jobName) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean setJobEnabled(String jobName, boolean enabled) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean setNodeEnabled(Node node, boolean enabled) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean enableAllJobs() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean startJob(String jobName) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isClusterRunning() throws RuntimeException {
        return Boolean.TRUE;
    }
}
