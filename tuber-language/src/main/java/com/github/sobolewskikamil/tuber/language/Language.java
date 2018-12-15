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
package com.github.sobolewskikamil.tuber.language;

import com.github.sobolewskikamil.tuber.language.node.EvalRootNode;
import com.github.sobolewskikamil.tuber.language.parser.ParserFacade;
import com.github.sobolewskikamil.tuber.language.parser.TuberParserFacade;
import com.github.sobolewskikamil.tuber.language.runtime.Context;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Registration;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.source.Source;

import java.util.Map;

@Registration(id = Language.ID, name = "TB", mimeType = Language.MIME_TYPE, contextPolicy = TruffleLanguage.ContextPolicy.SHARED)
@ProvidedTags({StandardTags.CallTag.class, StandardTags.StatementTag.class, StandardTags.RootTag.class, StandardTags.ExpressionTag.class, DebuggerTags.AlwaysHalt.class})
public class Language extends TruffleLanguage<Context> {
    public static final String ID = "tb";
    public static final String MIME_TYPE = "application/x-tb";

    private final ParserFacade parserFacade = new TuberParserFacade();

    @Override
    protected CallTarget parse(ParsingRequest request) {
        Source source = request.getSource();
        Map<String, RootCallTarget> functions = parserFacade.parseLanguage(this, source.getCharacters().toString());
        RootCallTarget mainCallTarget = functions.get("main");
        if (mainCallTarget == null) {
            throw new IllegalStateException("Main method missing");
        }
        EvalRootNode evalRootNode = new EvalRootNode(this, mainCallTarget, functions);
        return Truffle.getRuntime().createCallTarget(evalRootNode);
    }

    @Override
    protected Context createContext(Env env) {
        return new Context(this, env);
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        return false;
    }
}
