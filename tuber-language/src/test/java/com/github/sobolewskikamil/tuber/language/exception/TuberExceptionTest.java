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
package com.github.sobolewskikamil.tuber.language.exception;

import com.github.sobolewskikamil.tuber.language.node.type.ArrayType;
import com.github.sobolewskikamil.tuber.language.node.type.NullType;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TuberExceptionTest {
    @Test
    void shouldCreateErrorWithoutOperationWhenNodeDoesNotHaveNodeInfo() {
        // given
        Node node = mock(Node.class);

        // when
        TuberException exception = TuberException.ofError(node);

        // then
        assertThat(exception.getLocation()).isSameAs(node);
        assertThat(exception).hasMessage("Error: operation not defined.");
    }

    @Test
    void shouldCreateErrorWithOperationWhenNodeHasNodeInfo() {
        // given
        Node node = new TestNode();

        // when
        TuberException exception = TuberException.ofError(node);

        // then
        assertThat(exception.getLocation()).isSameAs(node);
        assertThat(exception).hasMessage("Error: operation \"op\" not defined.");
    }

    @Test
    void shouldCreateErrorWithoutOperationAndWithArguments() {
        // given
        Node node = mock(Node.class);

        // when
        TuberException exception = TuberException.ofError(node, getArguments());

        // then
        assertThat(exception.getLocation()).isSameAs(node);
        assertThat(exception)
                .hasMessage("Error: operation not defined for Number 1, Number 1.0, Boolean true, String \"test\", Array [1, 2.0], NULL, ANY, 1.");
    }

    @Test
    void shouldCreateErrorWithOperationAndArguments() {
        // given
        Node node = new TestNode();

        // when
        TuberException exception = TuberException.ofError(node, getArguments());

        // then
        assertThat(exception.getLocation()).isSameAs(node);
        assertThat(exception)
                .hasMessage("Error: operation \"op\" not defined for Number 1, Number 1.0, Boolean true, String \"test\", Array [1, 2.0], NULL, ANY, 1.");
    }

    private Object[] getArguments() {
        return new Object[]{1L, 1.0, true, "test", new ArrayType(1, 2.0), NullType.getInstance(), null, BigInteger.valueOf(1L)};
    }

    @NodeInfo(shortName = "op")
    private static final class TestNode extends Node {
    }
}
