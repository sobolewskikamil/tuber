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
package com.github.sobolewskikamil.tuber.language.utils;

import com.oracle.truffle.api.nodes.NodeInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NodeUtilsTest {
    @Test
    void shouldReturnNullWhenClassIsNull() {
        // when
        NodeInfo nodeInfo = NodeUtils.getNodeInfo(null);

        // then
        assertThat(nodeInfo).isNull();
    }

    @Test
    void shouldReturnNodeInfoForClass() {
        // when
        NodeInfo nodeInfo = NodeUtils.getNodeInfo(SuperTestClass.class);

        // then
        assertThat(nodeInfo.shortName()).isEqualTo("SuperTestClass");
    }

    @Test
    void shouldTraverseClassHierarchyForNodeInfo() {
        // when
        NodeInfo nodeInfo = NodeUtils.getNodeInfo(TestClass.class);

        // then
        assertThat(nodeInfo.shortName()).isEqualTo("SuperTestClass");
    }

    @Test
    void shouldReturnNullWhenNoClassInHierarchyHasClassInfo() {
        // when
        NodeInfo nodeInfo = NodeUtils.getNodeInfo(String.class);

        // then
        assertThat(nodeInfo).isNull();
    }

    @NodeInfo(shortName = "SuperTestClass")
    private static class SuperTestClass {
    }

    private static class TestClass extends SuperTestClass {
    }
}
