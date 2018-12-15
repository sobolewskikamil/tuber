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

import com.github.sobolewskikamil.tuber.language.runtime.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadlnBuiltinNodeTest {
    @Mock
    private ReadlnBuiltinNode node;
    @Mock
    private Context context;

    @BeforeEach
    void setUp() {
        when(node.getContext()).thenReturn(context);
    }

    @Test
    void shouldReadln() {
        // given
        doCallRealMethod().when(node).readln();

        String expected = "test";
        ByteArrayInputStream in = new ByteArrayInputStream(expected.getBytes());
        Mockito.lenient().when(context.getInput()).thenReturn(new BufferedReader(new InputStreamReader(in)));

        // when
        String result = node.readln();

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldReadlnEmptyStringForEndOfStream() {
        // given
        doCallRealMethod().when(node).readln();

        ByteArrayInputStream in = new ByteArrayInputStream(new byte[]{});
        Mockito.lenient().when(context.getInput()).thenReturn(new BufferedReader(new InputStreamReader(in)));

        // when
        String result = node.readln();

        // then
        assertThat(result).isEqualTo("");
    }

    @Test
    void shouldThrowExceptionWhenReaderThrowsIOException() throws IOException {
        // given
        doCallRealMethod().when(node).readln();

        BufferedReader bufferedReader = mock(BufferedReader.class);
        when(bufferedReader.readLine()).thenThrow(IOException.class);
        Mockito.lenient().when(context.getInput()).thenReturn(bufferedReader);

        // when / then
        assertThatThrownBy(() -> node.readln())
                .isExactlyInstanceOf(UncheckedIOException.class);
    }
}
