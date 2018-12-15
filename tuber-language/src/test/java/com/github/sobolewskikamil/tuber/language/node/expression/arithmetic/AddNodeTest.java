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
import com.github.sobolewskikamil.tuber.language.node.type.ArrayType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class AddNodeTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private AddNode node;

    @Test
    void shouldAddLongs() {
        // when
        long result = node.add(1L, 2L);

        // then
        assertThat(result).isEqualTo(3L);
    }

    @Test
    void shouldAddDoubles() {
        // when
        double result = node.add(1.0, 2.0);

        // then
        assertThat(result).isEqualTo(3.0);
    }

    @Test
    void shouldAddArrayTypes() {
        // when
        ArrayType result = node.add(new ArrayType(1, 2), new ArrayType("test1", "test2"));

        // then
        assertThat(result).isEqualTo(new ArrayType(1, 2, "test1", "test2"));
    }

    @Test
    void shouldAddObjectsByCallingToString() {
        // when
        String result = node.add("test", 1);

        // then
        assertThat(result).isEqualTo("test1");
    }

    @Test
    void shouldReturnTrueForOneString() {
        // when
        boolean result = node.isAnyString("test", 1);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueForTwoStrings() {
        // when
        boolean result = node.isAnyString("test1", "test2");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNoStrings() {
        // when
        boolean result = node.isAnyString(1, 2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldThrowTypeException() {
        // when
        TuberException exception = catchThrowableOfType(() -> node.typeError(1, 2), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation \"+\" not defined for 1, 2.");
        assertThat(exception.getLocation()).isSameAs(node);
    }
}
