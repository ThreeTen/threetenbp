/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.j2cl.java.time;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Numerous Ser classes use {@link java.io.Externalizable#readExternal} to consume the TZDB, which causes a problem
 * because the emulated class is empty and missing the readExternal method. The imports to the JRE class have been
 * replaced with this class so readExternal works.
 */
public interface Externalizable extends java.io.Externalizable {

    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;

    default void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException(); // included to keep compiler happy during COMPILE_GWT_INCOMPATIBLE_STRIPPED
    }
}
