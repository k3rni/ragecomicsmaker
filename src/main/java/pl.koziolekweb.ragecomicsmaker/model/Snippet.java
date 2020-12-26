package pl.koziolekweb.ragecomicsmaker.model;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class Snippet {
    private String name;
    private String code;

    public static Snippet loadSnippet(String resourcePath, String title) {
        URL res = Snippet.class.getResource(resourcePath);
        String code;
        try {
            code = IOUtils.toString(res, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new Snippet(title, code);
    }


    public Snippet(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
