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
package com.github.sobolewskikamil.tuber.language.node.expression.flowcontrol;

import com.github.sobolewskikamil.tuber.language.node.StatementNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BlockNodeTest {
    @Mock
    private StatementNode statementNode1;
    @Mock
    private StatementNode statementNode2;
    @Mock
    private StatementNode statementNode3;

    @Test
    void shouldReturnStatementNodes() {
        // given
        BlockNode blockNode = new BlockNode(new StatementNode[]{statementNode1, statementNode2, statementNode3});

        // when
        List<StatementNode> result = blockNode.getStatementNodes();

        // then
        assertThat(result).containsExactly(statementNode1, statementNode2, statementNode3);
    }

    @Test
    void shouldExecuteAllNodesInOrder() {
        // given
        VirtualFrame virtualFrame = mock(VirtualFrame.class);

        BlockNode blockNode = new BlockNode(new StatementNode[]{statementNode1, statementNode2, statementNode3});

        // when
        blockNode.executeVoid(virtualFrame);

        // then
        InOrder inOrder = Mockito.inOrder(statementNode1, statementNode2, statementNode3);
        inOrder.verify(statementNode1).executeVoid(virtualFrame);
        inOrder.verify(statementNode2).executeVoid(virtualFrame);
        inOrder.verify(statementNode3).executeVoid(virtualFrame);
    }
}
