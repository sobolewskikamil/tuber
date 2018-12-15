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
class EqualNodeTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private EqualNode node;

    @Test
    void shouldReturnTrueForEqualLongs() {
        // when
        boolean result = node.equal(1, 1);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotEqualLongs() {
        // when
        boolean result = node.equal(1, 2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForEqualDoubles() {
        // when
        boolean result = node.equal(1.0, 1.0);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotEqualDoubles() {
        // when
        boolean result = node.equal(1.0, 2.0);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForEqualBooleans() {
        // when
        boolean result = node.equal(true, true);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotEqualBooleans() {
        // when
        boolean result = node.equal(true, false);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForEqualStrings() {
        // when
        boolean result = node.equal("test", "test");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotEqualStrings() {
        // when
        boolean result = node.equal("test1", "test2");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForEqualNullTypes() {
        // when
        boolean result = node.equal(NullType.getInstance(), NullType.getInstance());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotEqualNullTypes() {
        // when
        boolean result = node.equal(null, NullType.getInstance());

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForEqualArrayTypes() {
        // when
        boolean result = node.equal(new ArrayType(1, 2, "test"), new ArrayType(1, 2, "test"));

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotEqualArrayTypes() {
        // when
        boolean result = node.equal(new ArrayType(1, 2), new ArrayType(1, 2, "test"));

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForEqualObjects() {
        // when
        boolean result = node.equal(new BigDecimal(1), new BigDecimal(1));

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForNotEqualObjects() {
        // when
        boolean result = node.equal(new BigDecimal(1), new BigDecimal(2));

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
        assertThat(exception).hasMessage("Error: operation \"==\" not defined for String \"test1\", String \"test2\".");
        assertThat(exception.getLocation()).isSameAs(node);
    }
}
