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

package stroom;

import stroom.dashboard.spring.DashboardConfiguration;
import stroom.index.spring.IndexConfiguration;
import stroom.logging.spring.EventLoggingConfiguration;
import stroom.script.spring.ScriptConfiguration;
import stroom.search.spring.SearchConfiguration;
import stroom.security.spring.SecurityConfiguration;
import stroom.spring.CachedServiceConfiguration;
import stroom.spring.PersistenceConfiguration;
import stroom.spring.ProcessTestServerComponentScanConfiguration;
import stroom.spring.ScopeConfiguration;
import stroom.spring.ScopeTestConfiguration;
import stroom.spring.ServerConfiguration;
import stroom.util.spring.StroomSpringProfiles;
import stroom.visualisation.spring.VisualisationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles(value = {StroomSpringProfiles.TEST, StroomSpringProfiles.IT, SecurityConfiguration.MOCK_SECURITY})
@ContextConfiguration(classes = {ScopeConfiguration.class, PersistenceConfiguration.class,
        ProcessTestServerComponentScanConfiguration.class, ServerConfiguration.class, CachedServiceConfiguration.class,
        SecurityConfiguration.class, ScopeTestConfiguration.class, EventLoggingConfiguration.class,
        IndexConfiguration.class, SearchConfiguration.class, ScriptConfiguration.class,
        VisualisationConfiguration.class, DashboardConfiguration.class})
public abstract class AbstractProcessIntegrationTest extends StroomIntegrationTest {
}
