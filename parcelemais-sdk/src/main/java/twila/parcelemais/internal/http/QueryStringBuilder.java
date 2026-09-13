package twila.parcelemais.internal.http;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class QueryStringBuilder {

    private final List<String> parameters = new ArrayList<>();

    public QueryStringBuilder add(String name, String value) {
        if (value != null)
            parameters.add(encode(name) + "=" + encode(value));

        return this;
    }

    public QueryStringBuilder add(String name, Integer value) {
        return add(name, value != null ? value.toString() : null);
    }

    public QueryStringBuilder add(String name, Long value) {
        return add(name, value != null ? value.toString() : null);
    }

    public QueryStringBuilder add(String name, BigDecimal value) {
        return add(name, value != null ? value.toPlainString() : null);
    }

    public QueryStringBuilder add(String name, OffsetDateTime value) {
        return add(name, value != null ? value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null);
    }

    public String build(String path) {
        if (parameters.isEmpty())
            return path;

        return path + "?" + String.join("&", parameters);
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("UTF-8 não suportado pela JVM.", ex);
        }
    }
}
