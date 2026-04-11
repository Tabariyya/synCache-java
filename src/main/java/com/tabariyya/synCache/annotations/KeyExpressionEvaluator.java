package com.tabariyya.synCache.annotations;


import java.lang.reflect.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyExpressionEvaluator {

    private static final Pattern TOKEN =
            Pattern.compile("#\\w+(?:\\.\\w+)*");

    public static Object eval(String expr, Method method, Object[] args, Object result) {

        if (expr == null || !expr.contains("#"))
            return expr;

        Matcher m = TOKEN.matcher(expr);
        StringBuffer out = new StringBuffer();

        while (m.find()) {
            String token = m.group();
            Object value = resolveToken(token, method, args, result);
            m.appendReplacement(out, Matcher.quoteReplacement(String.valueOf(value)));
        }

        m.appendTail(out);
        return out.toString();
    }

    private static Object resolveToken(
            String token,
            Method method,
            Object[] args,
            Object result
    ) {
        // remove leading #
        String body = token.substring(1);
        String[] parts = body.split("\\.");

        Object value;

        if (parts[0].equals("result")) {
            value = result;
        } else {
            value = resolveParam(method, args, parts[0]);
        }

        for (int i = 1; i < parts.length; i++) {
            value = readProperty(value, parts[i]);
        }

        return value;
    }

    private static Object resolveParam(Method method, Object[] args, String name) {
        Parameter[] params = method.getParameters();

        for (int i = 0; i < params.length; i++) {
            if (params[i].getName().equals(name)) {
                return args[i];
            }
        }

        throw new RuntimeException(
                "Cannot resolve parameter '" + name + "'. " +
                        "Compile with -parameters");
    }

    private static Object readProperty(Object obj, String name) {
        if (obj == null) return null;

        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).get(name);
        }

        try {
            Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot read property '" + name +
                            "' from " + obj.getClass(), e);
        }
    }
}
