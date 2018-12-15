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
import com.github.sobolewskikamil.tuber.language.node.type.ArrayType;
import com.github.sobolewskikamil.tuber.language.node.type.NullType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class NotEqualNodeTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private NotEqualNode node;

    @Test
    void shouldReturnTrueForNotEqualLongs() {
        // when
        boolean result = node.notEqual(1, 2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForEqualLongs() {
        // when
        boolean result = node.notEqual(1, 1);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForNotEqualDoubles() {
        // when
        boolean result = node.notEqual(1.0, 2.0);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForEqualDoubles() {
        // when
        boolean result = node.notEqual(1.0, 1.0);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForNotEqualBooleans() {
        // when
        boolean result = node.notEqual(true, false);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForEqualBooleans() {
        // when
        boolean result = node.notEqual(true, true);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForNotEqualStrings() {
        // when
        boolean result = node.notEqual("test1", "test2");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForEqualStrings() {
        // when
        boolean result = node.notEqual("test", "test");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForNotEqualNullTypes() {
        // when
        boolean result = node.notEqual(null, NullType.getInstance());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForEqualNullTypes() {
        // when
        boolean result = node.notEqual(NullType.getInstance(), NullType.getInstance());

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForNotEqualArrayTypes() {
        // when
        boolean result = node.notEqual(new ArrayType(1, 2), new ArrayType(1, 2, "test"));

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForEqualArrayTypes() {
        // when
        boolean result = node.notEqual(new ArrayType(1, 2, "test"), new ArrayType(1, 2, "test"));

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForNotEqualObjects() {
        // when
        boolean result = node.notEqual(new BigDecimal(1), new BigDecimal(2));

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForEqualObjects() {
        // when
        boolean result = node.notEqual(new BigDecimal(1), new BigDecimal(1));

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForDifferentClasses() {
        // when
        boolean result = node.areDifferentClasses("test", 1);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForSameClasses() {
        // when
        boolean result = node.areDifferentClasses("test2", "test2");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldThrowTypeException() {
        // when
        TuberException exception = catchThrowableOfType(() -> node.typeError("test1", "test2"), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation \"!=\" not defined for String \"test1\", String \"test2\".");
        assertThat(exception.getLocation()).isSameAs(node);
    }
}
