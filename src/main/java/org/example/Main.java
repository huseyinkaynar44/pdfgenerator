package org.example;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println(Base64.getEncoder().encodeToString(generate()));
    }

    private static byte[] generate() throws Exception {
        Document doc = null;
        doc = Jsoup.parse(new File("src/main/java/org/example/deneme.html"));
        doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        Map<String, String> params =prepareParams();

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFont(new File("src/main/java/org/example/arial.ttf"),"Arial" );
        builder.useFont(new File("src/main/java/org/example/arial-bold.ttf"), "Arial Bold");
        builder.useFastMode();
        builder.withHtmlContent(prepareHtml(doc.html(),params), "");
        builder.toStream(os);
        builder.useUnicodeBidiSplitter(new ICUBidiSplitter.ICUBidiSplitterFactory());
        builder.useUnicodeBidiReorderer(new ICUBidiReorderer());

        builder.run();
        os.close();

        File outputFile = new File("src/main/java/org/example/deneme.pdf");
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(os.toByteArray());
        }
        return os.toByteArray();
    }

    public static Map<String, String> prepareParams(){
        Map<String, String> params = new HashMap<>();
        params.put("referenceNo","denemeRef");
        params.put("bankName","denemeBank");

        return params;
    }

    public static String prepareHtml(String template, Map<String, String> parameters) {
        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile(new StringReader(template), "content");
        StringWriter writer = new StringWriter();
        mustache.execute(writer, parameters);
        return writer.toString();
    }


}