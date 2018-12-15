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
package com.github.sobolewskikamil.tuber.language.node.expression.builtin;

import com.github.sobolewskikamil.tuber.language.node.type.ArrayType;
import com.github.sobolewskikamil.tuber.language.node.type.NullType;
import com.github.sobolewskikamil.tuber.language.runtime.Context;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doCallRealMethod;

@ExtendWith(MockitoExtension.class)
class PrintlnBuiltinNodeTest {
    @Mock
    private PrintlnBuiltinNode node;
    @Mock
    private Context context;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        Mockito.lenient().when(context.getOutput()).thenReturn(new PrintWriter(out, true));
        Mockito.lenient().when(node.getContext()).thenReturn(context);
    }

    @Test
    void shouldPrintlnLong() {
        // given
        doCallRealMethod().when(node).println(anyLong());

        // when
        long result = node.println(1L);

        // then
        assertThat(result).isEqualTo(1L);
        assertThat(out.toString()).isEqualTo("1\n");
    }

    @Test
    void shouldPrintlnDouble() {
        // given
        doCallRealMethod().when(node).println(anyDouble());

        // when
        double result = node.println(1.0);

        // then
        assertThat(result).isEqualTo(1.0);
        assertThat(out.toString()).isEqualTo("1.0\n");
    }

    @Test
    void shouldPrintlnBoolean() {
        // given
        doCallRealMethod().when(node).println(anyBoolean());

        // when
        boolean result = node.println(false);

        // then
        assertThat(result).isEqualTo(false);
        assertThat(out.toString()).isEqualTo("false\n");
    }

    @Test
    void shouldPrintlnString() {
        // given
        doCallRealMethod().when(node).println(anyString());

        // when
        String result = node.println("test");

        // then
        assertThat(result).isEqualTo("test");
        assertThat(out.toString()).isEqualTo("test\n");
    }

    @Test
    void shouldPrintlnNullType() {
        // given
        doCallRealMethod().when(node).println(any(NullType.class));

        // when
        NullType result = node.println(NullType.getInstance());

        // then
        assertThat(result).isSameAs(NullType.getInstance());
        assertThat(out.toString()).isEqualTo("null\n");
    }

    @Test
    void shouldPrintlnArrayType() {
        // given
        doCallRealMethod().when(node).println(any(ArrayType.class));

        // when
        Object[] values = new Object[]{1, 1.0, "test", true, NullType.getInstance(), ImmutableList.of(2, ImmutableList.of())};
        ArrayType result = node.println(new ArrayType(values));

        // then
        assertThat(result).isEqualTo(new ArrayType(values));
        assertThat(out.toString()).isEqualTo("[1, 1.0, test, true, null, [2, []]]\n");
    }

    @Test
    void shouldPrintlnObject() {
        // given
        doCallRealMethod().when(node).println(any(BigDecimal.class));

        // when
        Object result = node.println(new BigDecimal(15.0));

        // then
        assertThat(result).isEqualTo(new BigDecimal(15.0));
        assertThat(out.toString()).isEqualTo("15\n");
    }
}
