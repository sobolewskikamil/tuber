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
package com.github.sobolewskikamil.tuber.language.node.expression.access;

import com.github.sobolewskikamil.tuber.language.node.type.NullType;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadArgumentNodeTest {
    @Mock
    private VirtualFrame frame;

    @Test
    void shouldReturnArgumentFromGivenIndex() {
        // given
        when(frame.getArguments()).thenReturn(new Object[]{"test1", "test2", "test3"});
        ReadArgumentNode node = new ReadArgumentNode(1);

        // when
        Object result = node.executeGeneric(frame);

        // then
        assertThat(result).isEqualTo("test2");
    }

    @Test
    void shouldReturnNullWhenIndexExceedsNumberOfArguments() {
        // given
        when(frame.getArguments()).thenReturn(new Object[]{"test1", "test2"});
        ReadArgumentNode node = new ReadArgumentNode(2);

        // when
        Object result = node.executeGeneric(frame);

        // then
        assertThat(result).isSameAs(NullType.getInstance());
    }
}
