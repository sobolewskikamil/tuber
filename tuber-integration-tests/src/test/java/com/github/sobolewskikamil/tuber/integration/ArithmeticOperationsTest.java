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
import org.assertj.core.data.Offset;
import org.graalvm.polyglot.PolyglotException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ArithmeticOperationsTest {
    private ByteArrayOutputStream out;
    private TuberLauncher launcher;

    @BeforeEach
    void setup() {
        out = new ByteArrayOutputStream();
        launcher = new TuberLauncher(new NullInputStream(10), out);
    }

    @Nested
    @DisplayName("Add")
    class Add {
        @Test
        void shouldAddLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 + 2;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            long result = Long.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(3L);
        }

        @Test
        void shouldAddDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1.1 + 2.2;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            double result = Double.valueOf(out.toString().trim());
            assertThat(result).isCloseTo(3.3, Offset.offset(0.00000001));
        }

        @Test
        void shouldAddArrayTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = {1, 1.0} + {true, null};" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            assertThat(out.toString()).isEqualToIgnoringWhitespace("[1, 1.0, true, null]");
        }

        @Test
        void shouldAddStrings() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = \"test\" + \"test\";" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            assertThat(out.toString()).isEqualToIgnoringWhitespace("testtest");
        }

        @Test
        void shouldAddWhenOneOperandIsString() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = \"test\" + 1;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            assertThat(out.toString()).isEqualToIgnoringWhitespace("test1");
        }

        @Test
        void shouldThrowExceptionWhenAddingDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1.0 + 1;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Div")
    class Div {
        @Test
        void shouldDivLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2 / 2;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            Double result = Double.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(1.0);
        }

        @Test
        void shouldDivDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1.0 / 2.0;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            double result = Double.valueOf(out.toString().trim());
            assertThat(result).isCloseTo(0.5, Offset.offset(0.00000001));
        }

        @Test
        void shouldThrowExceptionWhenDividingDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 / 1.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Exp")
    class Exp {
        @Test
        void shouldExpLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2^2;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            double result = Double.valueOf(out.toString().trim());
            double expected = Math.pow(2, 2);
            assertThat(result).isCloseTo(expected, Offset.offset(0.00000001));
        }

        @Test
        void shouldExpDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 7.5^1.5;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            double result = Double.valueOf(out.toString().trim());
            double expected = Math.pow(7.5, 1.5);
            assertThat(result).isCloseTo(expected, Offset.offset(0.00000001));
        }

        @Test
        void shouldThrowExceptionWhenExpDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1^2.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Mod")
    class Mod {
        @Test
        void shouldModLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 5 % 2;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            long result = Long.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(1L);
        }

        @Test
        void shouldModDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 5.0%2.0;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            double result = Double.valueOf(out.toString().trim());
            assertThat(result).isCloseTo(1.0, Offset.offset(0.00000001));
        }

        @Test
        void shouldThrowExceptionWhenModDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1%2.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Mul")
    class Mul {
        @Test
        void shouldMulLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 * 2;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            long result = Long.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(2L);
        }

        @Test
        void shouldMulDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2.5 * 2.5;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            double result = Double.valueOf(out.toString().trim());
            assertThat(result).isCloseTo(6.25, Offset.offset(0.00000001));
        }

        @Test
        void shouldThrowExceptionWhenMulDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1*2.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Sub")
    class Sub {
        @Test
        void shouldSubLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2 - 1;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            long result = Long.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(1L);
        }

        @Test
        void shouldSubDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2.5 - 1.5;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            double result = Double.valueOf(out.toString().trim());
            assertThat(result).isCloseTo(1.0, Offset.offset(0.00000001));
        }

        @Test
        void shouldThrowExceptionWhenSubDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 - 2.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }
}
