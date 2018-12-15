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
import org.graalvm.polyglot.PolyglotException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LogicalOperationsTest {
    private ByteArrayOutputStream out;
    private TuberLauncher launcher;

    @BeforeEach
    void setup() {
        out = new ByteArrayOutputStream();
        launcher = new TuberLauncher(new NullInputStream(10), out);
    }

    @Nested
    @DisplayName("Not")
    class Not {
        @Test
        void shouldNotBoolean() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = !false;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldThrowExceptionWheNotNotBoolean() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = !1;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("And")
    class And {
        @Test
        void shouldAndBooleans() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = true && false;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(false);
        }

        @Test
        void shouldApplyShortCircuitForAnd() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = false && func();" +
                    "}" +
                    "" +
                    "def func() {" +
                    "   println(\"func\");" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            assertThat(out.toString()).isEmpty();
        }

        @Test
        void shouldThrowExceptionWhenAndNotBooleans() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 && 1.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Or")
    class Or {
        @Test
        void shouldOrBooleans() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = true || false;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldApplyShortCircuitForOr() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = true || func();" +
                    "}" +
                    "" +
                    "def func() {" +
                    "   println(\"func\");" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            assertThat(out.toString()).isEmpty();
        }

        @Test
        void shouldThrowExceptionWhenOrNotBooleans() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 || 1.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Equal")
    class Equal {
        @Test
        void shouldEqualLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 == 1;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldEqualDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1.0 == 1.0;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldEqualBooleans() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = true == true;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldEqualStrings() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = \"test\" == \"test\";" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldEqualNulls() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = null == null;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldEqualArrays() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = {1, 1.0, true, null, {1}} == {1, 1.0, true, null, {1}};" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldEqualDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = \"test\" == 1;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("Not equal")
    class NotEqual {
        @Test
        void shouldNotEqualLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 != 2;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldNotEqualDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1.0 != 2.0;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldNotEqualBooleans() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = true != false;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldNotEqualStrings() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = \"test1\" != \"test2\";" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldNotEqualNulls() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = null != null;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(false);
        }

        @Test
        void shouldEqualArrays() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = {1, 1.0, true, null, {1}} != {1, 1.0, true, null, {2}};" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldNotEqualDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = \"test\" != 1;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("Greater")
    class Greater {
        @Test
        void shouldGreaterLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2 > 1;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldGreaterDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2.0 > 1.0;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldThrowExceptionWhenGreaterDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2 > 1.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Greater or equal")
    class GreaterOrEqual {
        @Test
        void shouldGreaterOrEqualLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2 >= 1;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldGreaterOrEqualDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2.0 >= 1.0;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldThrowExceptionWhenGreaterOrEqualDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 2 >= 1.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Less")
    class Less {
        @Test
        void shouldLessLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 < 2;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldLessDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1.0 < 2.0;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldThrowExceptionWhenLessDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1.0 < 2;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }

    @Nested
    @DisplayName("Less or equal")
    class LessOrEqual {
        @Test
        void shouldLessOrEqualLongs() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 <= 2;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldLessOrEqualDoubles() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1.0 <= 2.0;" +
                    "   println(a);" +
                    "}";

            // when
            launcher.launchFromString(source);

            // then
            boolean result = Boolean.valueOf(out.toString().trim());
            assertThat(result).isEqualTo(true);
        }

        @Test
        void shouldThrowExceptionWhenLessOrEqualDifferentTypes() {
            // given
            String source = "" +
                    "def main() {" +
                    "   a = 1 <= 2.0;" +
                    "   println(a);" +
                    "}";

            // when / then
            assertThatThrownBy(() -> launcher.launchFromString(source))
                    .isExactlyInstanceOf(PolyglotException.class);
        }
    }
}
