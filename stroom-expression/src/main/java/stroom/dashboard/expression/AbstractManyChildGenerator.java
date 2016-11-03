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

package stroom.dashboard.expression;

public abstract class AbstractManyChildGenerator extends AbstractGenerator {
    private static final long serialVersionUID = 513621715143449935L;

    final Generator[] childGenerators;

    public AbstractManyChildGenerator(final Generator[] childGenerators) {
        this.childGenerators = childGenerators;
    }

    @Override
    public void addChildKey(final String group) {
        if (childGenerators != null) {
            for (final Generator gen : childGenerators) {
                gen.addChildKey(group);
            }
        }
    }

    @Override
    public abstract void set(String[] values);

    @Override
    public abstract Object eval();

    @Override
    public void merge(final Generator generator) {
        addChildren((AbstractManyChildGenerator) generator);
    }

    public void addChildren(final AbstractManyChildGenerator generator) {
        for (int i = 0; i < childGenerators.length; i++) {
            childGenerators[i].merge(generator.childGenerators[i]);
        }
    }
}
