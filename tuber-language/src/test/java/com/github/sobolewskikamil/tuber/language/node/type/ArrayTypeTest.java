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
package com.github.sobolewskikamil.tuber.language.node.type;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ArrayTypeTest {
    @Test
    void shouldReturnSameValuesAsPassedInConstructor() {
        // given
        Object[] values = new Object[]{"test1", "test2"};
        ArrayType arrayType = new ArrayType(values);

        // when
        Object[] result = arrayType.getValues();

        // then
        assertThat(result).isEqualTo(result);
    }

    @Test
    void shouldReturnValueFromIndex() {
        // given
        ArrayType arrayType = new ArrayType("test1", "test2");

        // when
        Optional<Object> result = arrayType.get(0);

        // then
        assertThat(result).hasValue("test1");
    }

    @Test
    void shouldReturnEmptyOptionalWhenIndexIsGreaterThanSize() {
        // given
        ArrayType arrayType = new ArrayType("test1", "test2");

        // when
        Optional<Object> result = arrayType.get(3);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptionalWhenIndexIsEqualToSize() {
        // given
        ArrayType arrayType = new ArrayType("test1", "test2");

        // when
        Optional<Object> result = arrayType.get(2);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldPutValueOnNotUsedIndex() {
        // given
        ArrayType arrayType = new ArrayType(6);

        // when
        arrayType.add(5, "test");

        // then
        Optional<Object> result = arrayType.get(5);
        assertThat(result).hasValue("test");
    }

    @Test
    void shouldPutValueOnUsedIndex() {
        // given
        ArrayType arrayType = new ArrayType(6);

        // when
        arrayType.add(5, "test1");
        arrayType.add(5, "test2");

        // then
        Optional<Object> result = arrayType.get(5);
        assertThat(result).hasValue("test2");
    }

    @Test
    void testEquals() {
        // given
        class ArrayTypeSubClass extends ArrayType {
            private ArrayTypeSubClass(Object[] values) {
                super(values);
            }
        }

        Object[] values = new Object[]{"test1", "test2"};
        ArrayType arrayType = new ArrayType(values);

        // when / then
        assertThat(arrayType)
                .isEqualTo(arrayType)
                .isEqualTo(new ArrayType(values))
                .isNotEqualTo(null)
                .isNotEqualTo(new ArrayTypeSubClass(values));
    }

    @Test
    void testHashCode() {
        // given
        Object[] values = new Object[]{"test1", "test2"};
        ArrayType arrayType = new ArrayType(values);

        // when / then
        assertThat(arrayType.hashCode()).isEqualTo(new ArrayType(values).hashCode());
        assertThat(arrayType.hashCode()).isNotEqualTo(new ArrayType().hashCode());
    }

    @Test
    void testToString() {
        // given
        Object[] values = new Object[]{"test1", "test2"};
        ArrayType arrayType = new ArrayType(values);

        // when
        String result = arrayType.toString();

        // then
        String expected = Arrays.deepToString(values);
        assertThat(result).isEqualTo(expected);
    }
}
