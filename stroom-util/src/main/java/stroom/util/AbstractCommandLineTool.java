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

package stroom.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import stroom.util.zip.HeaderMap;

/**
 * Base class for command line tools that handles setting a load of args on the
 * program as name value pairs.
 */
public abstract class AbstractCommandLineTool {
    private static class Example extends AbstractCommandLineTool {
        @Override
        public void run() {
            throw new RuntimeException();
        }
    }

    private HeaderMap map;
    private List<String> validArguments;
    private int maxPropLength = 0;

    public abstract void run();

    protected void checkArgs() {
    }

    protected void failArg(final String arg, final String msg) {
        throw new RuntimeException(arg + " - " + msg);
    }

    public void init(final String[] args) throws Exception {
        map = new HeaderMap();
        validArguments = new ArrayList<String>();

        map.loadArgs(args);

        final BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());

        for (final PropertyDescriptor field : beanInfo.getPropertyDescriptors()) {
            if (field.getWriteMethod() != null) {
                if (field.getName().length() > maxPropLength) {
                    maxPropLength = field.getName().length();
                }
                if (map.containsKey(field.getName())) {
                    validArguments.add(field.getName());
                    field.getWriteMethod().invoke(this, getAsType(field));
                }
            }
        }

        checkArgs();
    }

    private Object getAsType(final PropertyDescriptor descriptor) {
        final Class<?> propertyClass = descriptor.getPropertyType();
        if (propertyClass.equals(String.class)) {
            return map.get(descriptor.getName());
        }
        if (propertyClass.equals(Boolean.class) || propertyClass.equals(Boolean.TYPE)) {
            return Boolean.parseBoolean(map.get(descriptor.getName()));
        }
        if (propertyClass.equals(Integer.class) || propertyClass.equals(Integer.TYPE)) {
            return Integer.parseInt(map.get(descriptor.getName()));
        }
        if (propertyClass.equals(Long.class) || propertyClass.equals(Long.TYPE)) {
            return Long.parseLong(map.get(descriptor.getName()));
        }
        throw new RuntimeException("AbstractCommandLineTool does not know about properties of type " + propertyClass);
    }

    public void doMain(final String[] args) throws Exception {
        init(args);
        run();
    }

    public void traceArguments(final PrintStream printStream) {
        try {
            doTraceArguments(printStream);
        } catch (final Exception ex) {
            printStream.println(ex.getMessage());
        }
    }

    private void doTraceArguments(final PrintStream printStream) throws Exception {
        final BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
        for (final PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            // Only do properties with getters
            if (pd.getWriteMethod() != null) {
                // Simple getter ?
                String suffix = " (default)";
                if (map.containsKey(pd.getName())) {
                    suffix = " (arg)";
                }
                String value = "";

                if (pd.getReadMethod() != null && pd.getReadMethod().getParameterTypes().length == 0) {
                    value = String.valueOf(pd.getReadMethod().invoke(this));
                } else {
                    // No simple getter
                    Field field = null;
                    try {
                        field = this.getClass().getDeclaredField(pd.getName());
                    } catch (final NoSuchFieldException nsfex) {
                        // Ignore
                    }
                    if (field != null) {
                        field.setAccessible(true);
                        value = String.valueOf(field.get(this));
                    } else {
                        value = "?";

                    }
                }
                printStream.println(StringUtils.rightPad(pd.getName(), maxPropLength) + " = " + value + suffix);
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        final Example example = new Example();
        example.doMain(args);
    }

}
