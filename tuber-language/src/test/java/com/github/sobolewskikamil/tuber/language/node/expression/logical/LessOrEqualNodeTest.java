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
package com.github.sobolewskikamil.tuber.language.node.expression.logical;

import com.github.sobolewskikamil.tuber.language.exception.TuberException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;

@ExtendWith(MockitoExtension.class)
class LessOrEqualNodeTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private LessOrEqualNode node;

    @Test
    void shouldReturnTrueForLessLongs() {
        // when
        boolean result = node.lessOrEqual(1, 2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueForEqualLongs() {
        // when
        boolean result = node.lessOrEqual(2, 2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotLessLongs() {
        // when
        boolean result = node.lessOrEqual(2, 1);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForLessDoubles() {
        // when
        boolean result = node.lessOrEqual(1.0, 2.0);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueForEqualDoubles() {
        // when
        boolean result = node.lessOrEqual(2.0, 2.0);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotLessDoubles() {
        // when
        boolean result = node.lessOrEqual(2.0, 1.0);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldThrowTypeException() {
        // when
        TuberException exception = catchThrowableOfType(() -> node.typeError("test1", "test2"), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation \"<=\" not defined for String \"test1\", String \"test2\".");
        assertThat(exception.getLocation()).isSameAs(node);
    }
}
