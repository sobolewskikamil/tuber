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

import com.github.sobolewskikamil.tuber.language.Language;
import com.github.sobolewskikamil.tuber.language.node.RootNode;
import com.github.sobolewskikamil.tuber.language.node.expression.ExpressionNode;
import com.github.sobolewskikamil.tuber.language.node.expression.access.ReadArgumentNode;
import com.github.sobolewskikamil.tuber.language.node.expression.builtin.*;
import com.github.sobolewskikamil.tuber.language.utils.NodeUtils;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameDescriptor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.IntStream;

public class Context {
    private final Language language;
    private final FunctionRegistry functionRegistry;
    private final BufferedReader input;
    private final PrintWriter output;

    public Context(Language language, Env env) {
        this.language = language;
        this.functionRegistry = new FunctionRegistry();
        this.input = new BufferedReader(new InputStreamReader(env.in()));
        this.output = new PrintWriter(env.out(), true);
        installBuiltins();
    }

    public FunctionRegistry getFunctionRegistry() {
        return functionRegistry;
    }

    public BufferedReader getInput() {
        return input;
    }

    public PrintWriter getOutput() {
        return output;
    }

    private void installBuiltins() {
        installBuiltin(ReadlnBuiltinNodeFactory.getInstance());
        installBuiltin(PrintlnBuiltinNodeFactory.getInstance());
        installBuiltin(NewArrayBuiltinNodeFactory.getInstance());
        installBuiltin(ArrayLengthBuiltinNodeFactory.getInstance());
        installBuiltin(CurrentTimeMillisBuiltinNodeFactory.getInstance());
    }

    private void installBuiltin(NodeFactory<? extends BuiltinNode> factory) {
        int argumentCount = factory.getExecutionSignature().size();
        ExpressionNode[] argumentNodes = IntStream.range(0, argumentCount)
                .mapToObj(ReadArgumentNode::new)
                .toArray(ExpressionNode[]::new);

        BuiltinNode builtinNode = factory.createNode((Object) argumentNodes);
        String name = NodeUtils.getNodeInfo(builtinNode.getClass()).shortName();

        RootNode rootNode = new RootNode(language, new FrameDescriptor(), builtinNode);
        getFunctionRegistry().register(name, Truffle.getRuntime().createCallTarget(rootNode));
    }
}
