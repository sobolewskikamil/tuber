/*
 * Copyright (c) 2018 Kamil Sobolewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.sobolewskikamil.tuber.integration;

import com.github.sobolewskikamil.tuber.launcher.TuberLauncher;
import org.apache.commons.io.input.NullInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Java6Assertions.assertThat;

class IOTest {
    @Test
    void shouldReadln() {
        // given
        String source = "" +
                "def main() {" +
                "   a = readln();" +
                "   println(a);" +
                "}";
        String expected = "test";
        ByteArrayInputStream in = new ByteArrayInputStream(expected.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TuberLauncher launcher = new TuberLauncher(in, out);

        // when
        launcher.launchFromString(source);

        // then
        assertThat(out.toString()).isEqualToIgnoringWhitespace(expected);
    }

    @Nested
    @DisplayName("Println")
    class Println {
        private ByteArrayOutputStream out;
        private TuberLauncher executor;

        @BeforeEach
        void setup() {
            out = new ByteArrayOutputStream();
            executor = new TuberLauncher(new NullInputStream(10), out);
        }

        @Test
        void shouldPrintlnLong() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1;" +
                    "   println(a);" +
                    "}";

            // when
            executor.launchFromString(source);

            // then
            assertThat(out.toString()).isEqualToIgnoringWhitespace("1");
        }

        @Test
        void shouldPrintlnDouble() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1.0;" +
                    "   println(a);" +
                    "}";

            // when
            executor.launchFromString(source);

            // then
            assertThat(out.toString()).isEqualToIgnoringWhitespace("1.0");
        }

        @Test
        void shouldPrintlnBoolean() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = true;" +
                    "   println(a);" +
                    "}";

            // when
            executor.launchFromString(source);

            // then
            assertThat(out.toString()).isEqualToIgnoringWhitespace("true");
        }

        @Test
        void shouldPrintlnString() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = \"test\";" +
                    "   println(a);" +
                    "}";

            // when
            executor.launchFromString(source);

            // then
            assertThat(out.toString()).isEqualToIgnoringWhitespace("test");
        }

        @Test
        void shouldPrintlnNullType() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = null;" +
                    "   println(a);" +
                    "}";

            // when
            executor.launchFromString(source);

            // then
            assertThat(out.toString()).isEqualToIgnoringWhitespace("null");
        }

        @Test
        void shouldPrintlnArrayType() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = {1, 1.0, true, \"test\", {1}};" +
                    "   println(a);" +
                    "}";

            // when
            executor.launchFromString(source);

            // then
            assertThat(out.toString()).isEqualToIgnoringWhitespace("[1, 1.0, true, test, [1]]");
        }
    }
}
