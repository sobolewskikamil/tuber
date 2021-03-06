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
package com.github.sobolewskikamil.tuber.language.node.expression.arithmetic;

import com.github.sobolewskikamil.tuber.language.exception.TuberException;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ExpNodeTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private ExpNode node;

    @Test
    void shouldExpLongs() {
        // when
        long result = node.exp(2L, 2L);

        // then
        assertThat(result).isEqualTo(4L);
    }

    @Test
    void shouldExpDoubles() {
        // when
        double result = node.exp(7.5, 1.5);

        // then
        double expected = Math.pow(7.5, 1.5);
        assertThat(result).isCloseTo(expected, Offset.offset(0.00000001));
    }

    @Test
    void shouldThrowTypeException() {
        // when
        TuberException exception = catchThrowableOfType(() -> node.typeError(1, 2), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation \"^\" not defined for 1, 2.");
        assertThat(exception.getLocation()).isSameAs(node);
    }
}
