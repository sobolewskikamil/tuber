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
import com.github.sobolewskikamil.tuber.language.utils.NodeUtils;
import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

import java.util.StringJoiner;

public class TuberException extends RuntimeException implements TruffleException {
    private final Node location;

    private TuberException(String message, Node location) {
        super(message);
        this.location = location;
    }

    public static TuberException ofError(Node location, Object... arguments) {
        return new TuberException(formatErrorMessage(location, arguments), location);
    }

    @Override
    public Node getLocation() {
        return location;
    }

    private static String formatErrorMessage(Node location, Object[] arguments) {
        StringBuilder result = new StringBuilder();
        result.append("Error: operation");

        NodeInfo nodeInfo = NodeUtils.getNodeInfo(location.getClass());
        if (nodeInfo != null) {
            result.append(" \"").append(nodeInfo.shortName()).append("\"");
        }

        result.append(" not defined");

        StringJoiner stringJoiner = new StringJoiner(", ");
        for (Object value : arguments) {
            if (value instanceof Long || value instanceof Double) {
                stringJoiner.add(String.format("Number %s", value));
            } else if (value instanceof Boolean) {
                stringJoiner.add(String.format("Boolean %s", value));
            } else if (value instanceof String) {
                stringJoiner.add(String.format("String \"%s\"", value));
            } else if (value == NullType.getInstance()) {
                stringJoiner.add("NULL");
            } else if (value instanceof ArrayType) {
                stringJoiner.add(String.format("Array %s", value));
            } else if (value == null) {
                stringJoiner.add("ANY");
            } else {
                stringJoiner.add(value.toString());
            }
        }

        if (stringJoiner.length() != 0) {
            result.append(" for ");
            result.append(stringJoiner.toString());
        }

        return result.append(".").toString();
    }
}
