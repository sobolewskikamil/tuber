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
package com.github.sobolewskikamil.tuber.language.runtime;

import com.google.common.collect.ImmutableMap;
import com.oracle.truffle.api.RootCallTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class FunctionRegistryTest {
    private FunctionRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
    }

    @Test
    void shouldReturnEmptyOptionalForNotExistingRecord() {
        // when
        Optional<RootCallTarget> result = registry.lookup("not existing");

        // then
        assertThat(result).isNotPresent();
    }

    @Test
    void shouldReturnRegisteredFunction() {
        // given
        RootCallTarget function = mock(RootCallTarget.class);
        registry.register("test", function);

        // when
        Optional<RootCallTarget> result = registry.lookup("test");

        // then
        assertThat(result).contains(function);
    }

    @Test
    void shouldReturnMultipleRegisteredFunction() {
        // given
        RootCallTarget function1 = mock(RootCallTarget.class);
        RootCallTarget function2 = mock(RootCallTarget.class);
        Map<String, RootCallTarget> functions = ImmutableMap.of(
                "test1", function1,
                "test2", function2
        );
        registry.registerAll(functions);

        // when
        Optional<RootCallTarget> result1 = registry.lookup("test1");
        Optional<RootCallTarget> result2 = registry.lookup("test2");

        // then
        assertThat(result1).contains(function1);
        assertThat(result2).contains(function2);
    }
}
