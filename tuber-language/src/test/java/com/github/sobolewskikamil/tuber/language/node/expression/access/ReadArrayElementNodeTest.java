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

import com.github.sobolewskikamil.tuber.language.exception.TuberException;
import com.github.sobolewskikamil.tuber.language.node.expression.ExpressionNode;
import com.github.sobolewskikamil.tuber.language.node.type.ArrayType;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadArrayElementNodeTest {
    private ReadArrayElementNode node;
    @Mock
    private ExpressionNode sourceNode;
    @Mock
    private ExpressionNode indexNode;
    @Mock
    private VirtualFrame frame;

    @BeforeEach
    void setUp() {
        node = new ReadArrayElementNode(sourceNode, indexNode);
    }

    @Test
    void shouldReturnArrayElementByIndex() throws UnexpectedResultException {
        // given
        when(sourceNode.executeArrayType(frame)).thenReturn(new ArrayType("test1", "test2", "test3"));
        when(indexNode.executeLong(frame)).thenReturn(1L);

        // when
        Object result = node.executeGeneric(frame);

        // then
        assertThat(result).isEqualTo("test2");
    }

    @Test
    void shouldThrowExceptionWhenNoElementOnGivenIndex() throws UnexpectedResultException {
        // given
        when(sourceNode.executeArrayType(frame)).thenReturn(new ArrayType());
        when(indexNode.executeLong(frame)).thenReturn(1L);

        // when / then
        assertThatThrownBy(() -> node.executeGeneric(frame))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldThrowExceptionWhenSourceNodeThrowsUnexpectedResultException() throws UnexpectedResultException {
        // given
        when(sourceNode.executeArrayType(frame)).thenThrow(new UnexpectedResultException("test"));

        // when
        TuberException exception = catchThrowableOfType(() -> node.executeGeneric(frame), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation not defined.");
        assertThat(exception.getLocation()).isSameAs(node);
    }

    @Test
    void shouldThrowExceptionWhenIndexNodeThrowsUnexpectedResultException() throws UnexpectedResultException {
        // given
        when(sourceNode.executeArrayType(frame)).thenReturn(new ArrayType());
        when(indexNode.executeLong(frame)).thenThrow(new UnexpectedResultException("test"));

        // when
        TuberException exception = catchThrowableOfType(() -> node.executeGeneric(frame), TuberException.class);

        // then
        assertThat(exception).hasMessage("Error: operation not defined.");
        assertThat(exception.getLocation()).isSameAs(node);
    }
}
