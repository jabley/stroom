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

package stroom.explorer.client.presenter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class SetUtil {
    private SetUtil() {
        // Util
    }

    public static <T> Set<T> toSet(final T... varargs) {
        if (varargs == null || varargs.length == 0) {
            return null;
        } else {
            return new HashSet<T>(Arrays.asList(varargs));
        }
    }

    public static <T> Set<T> copySet(final Set<T> set) {
        if (set == null) {
            return null;
        }

        return new HashSet<T>(set);
    }
}
