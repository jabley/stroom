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

package stroom.importexport.server;

import java.io.File;
import java.util.List;

import stroom.entity.shared.EntityActionConfirmation;
import stroom.entity.shared.FindFolderCriteria;
import stroom.util.shared.SharedList;

/**
 * API to get data in and out.
 */
public interface ImportExportService {
    /**
     * Get a list of entities for Stroom to be able to import.
     */
    SharedList<EntityActionConfirmation> createImportConfirmationList(File data);

    /**
     * Perform an import using a confirmation list.
     */
    void performImportWithConfirmation(File data, List<EntityActionConfirmation> confirmList);

    /**
     * Perform an import without using a confirmation list.
     */
    void performImportWithoutConfirmation(File data);

    /**
     * Export a Stroom repository
     *
     * Also in the zip file output content that can be exploded and stored in
     * source control. Used for tracking changes with XSLT and feeds.
     */
    void exportConfig(FindFolderCriteria criteria, File data, boolean ignoreErrors, List<String> messageList);
}