package com.neutrinodust.useful_ores.config;

import com.neutrinodust.useful_ores.Constants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TomlConfig {

    private final Path file;
    private final Map<String, Object> loaded = new HashMap<>();
    private final StringBuilder out = new StringBuilder();
    private final Deque<String> path = new ArrayDeque<>();
    private final Deque<Boolean> headerWritten = new ArrayDeque<>();

    public TomlConfig(Path file, String... headerComment) {
        this.file = file;
        if (Files.exists(file)) {
            try {
                parse(Files.readString(file, StandardCharsets.UTF_8));
            } catch (IOException | RuntimeException e) {
                Constants.LOG.error("Could not read {} - using defaults for unreadable values", file, e);
            }
        }
        for (String line : headerComment) out.append("# ").append(line).append('\n');
    }

    public TomlConfig push(String name, String... comment) {
        path.addLast(name);
        boolean eager = comment.length > 0;
        headerWritten.addLast(false);
        if (eager) {
            out.append('\n');
            for (String c : comment) out.append("# ").append(c).append('\n');
            writeHeader();
        }
        return this;
    }

    public TomlConfig pop() {
        path.removeLast();
        headerWritten.removeLast();
        return this;
    }

    private void writeHeader() {
        if (headerWritten.peekLast()) return;
        out.append('[').append(String.join(".", path)).append("]\n");
        headerWritten.removeLast();
        headerWritten.addLast(true);
    }

    public boolean bool(String key, boolean def, String... comment) {
        Object raw = loaded.get(fullKey(key));
        boolean v = raw instanceof Boolean b ? b : def;
        emit(key, String.valueOf(v), comment, "default: " + def);
        return v;
    }

    public int integer(String key, int def, int min, int max, String... comment) {
        Object raw = loaded.get(fullKey(key));
        long v = raw instanceof Number n ? n.longValue() : def;
        v = Math.max(min, Math.min(max, v));
        emit(key, String.valueOf(v), comment, "default: " + def + ", range: " + min + " ~ " + (max == Integer.MAX_VALUE ? "max" : max));
        return (int) v;
    }

    public double decimal(String key, double def, double min, double max, String... comment) {
        Object raw = loaded.get(fullKey(key));
        double v = raw instanceof Number n ? n.doubleValue() : def;
        v = Math.max(min, Math.min(max, v));
        emit(key, formatDouble(v), comment, "default: " + formatDouble(def) + ", range: " + formatDouble(min) + " ~ " + formatDouble(max));
        return v;
    }

    @SuppressWarnings("unchecked")
    public List<String> stringList(String key, List<String> def, String... comment) {
        Object raw = loaded.get(fullKey(key));
        List<String> v = new ArrayList<>();
        if (raw instanceof List<?> list) {
            for (Object o : list) if (o instanceof String s) v.add(s);
        } else {
            v.addAll(def);
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < v.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(quote(v.get(i)));
        }
        sb.append(']');
        emit(key, sb.toString(), comment, null);
        return v;
    }

    private void emit(String key, String value, String[] comment, String info) {
        writeHeader();
        for (String c : comment) out.append("# ").append(c).append('\n');
        if (info != null) out.append("# (").append(info).append(")\n");
        out.append(key).append(" = ").append(value).append("\n\n");
    }

    private String fullKey(String key) {
        return path.isEmpty() ? key : String.join(".", path) + "." + key;
    }

    private static String formatDouble(double d) {
        String s = String.valueOf(d);
        return s.contains("E") || s.contains("e") ? String.format(java.util.Locale.ROOT, "%.6f", d) : s;
    }

    private static String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    public void save() {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, out.toString().replaceAll("\n{3,}", "\n\n"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Constants.LOG.error("Could not write {}", file, e);
        }
    }

    private void parse(String text) {
        String section = "";
        String[] lines = text.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            String line = stripComment(lines[i]).trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("[") && line.endsWith("]") && !line.contains("=")) {
                section = line.substring(1, line.length() - 1).trim();
                continue;
            }
            int eq = line.indexOf('=');
            if (eq < 0) continue;
            String key = line.substring(0, eq).trim();
            String value = line.substring(eq + 1).trim();
            while (value.startsWith("[") && !balanced(value) && i + 1 < lines.length) {
                value += " " + stripComment(lines[++i]).trim();
            }
            Object parsed = parseValue(value);
            if (parsed != null) loaded.put(section.isEmpty() ? key : section + "." + key, parsed);
        }
    }

    private static Object parseValue(String v) {
        try {
            if (v.equals("true")) return Boolean.TRUE;
            if (v.equals("false")) return Boolean.FALSE;
            if (v.startsWith("\"")) return unquote(v);
            if (v.startsWith("[")) {
                List<Object> list = new ArrayList<>();
                String inner = v.substring(1, v.lastIndexOf(']')).trim();
                int i = 0;
                while (i < inner.length()) {
                    char c = inner.charAt(i);
                    if (c == '"') {
                        int end = i + 1;
                        while (end < inner.length() && (inner.charAt(end) != '"' || inner.charAt(end - 1) == '\\')) end++;
                        list.add(unquote(inner.substring(i, Math.min(end + 1, inner.length()))));
                        i = end + 1;
                    } else {
                        i++;
                    }
                }
                return list;
            }
            String num = v.replace("_", "");
            if (num.matches("[+-]?\\d+")) return Long.parseLong(num);
            return Double.parseDouble(num);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static String unquote(String s) {
        String body = s.substring(1, s.endsWith("\"") && s.length() > 1 ? s.length() - 1 : s.length());
        return body.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static boolean balanced(String v) {
        int depth = 0;
        boolean inStr = false;
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (c == '"' && (i == 0 || v.charAt(i - 1) != '\\')) inStr = !inStr;
            else if (!inStr && c == '[') depth++;
            else if (!inStr && c == ']') depth--;
        }
        return depth <= 0;
    }

    private static String stripComment(String line) {
        boolean inStr = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"' && (i == 0 || line.charAt(i - 1) != '\\')) inStr = !inStr;
            else if (c == '#' && !inStr) return line.substring(0, i);
        }
        return line;
    }
}
