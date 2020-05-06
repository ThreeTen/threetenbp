package org.threeten.bp.zone;

import walkingkooka.text.CharSequences;
import walkingkooka.text.Indentation;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class TZDBDatTool {

    public static void main(final String[] args) throws IOException {
        new TZDBDatTool(Printers.sysOut()
                .indenting(Indentation.with("  ")))
                .print();
    }

    private TZDBDatTool(final IndentingPrinter printer) {
        super();
        this.printer = printer;
    }

    void print() throws IOException {
        final String datFile = "/org/threeten/bp/TZDB.dat";
        try (final InputStream input = this.getClass().getResourceAsStream(datFile)) {
            if (null == input) {
                throw new NullPointerException("Timezone data file " + CharSequences.quoteAndEscape(datFile) + " not found");
            }

            final IndentingPrinter printer = this.printer;

            printer.print("static String tzdb() {");
            printer.lineStart();

            printer.indent();
            {
                printer.print("return java.util.Arrays.asList(");
                printer.lineStart();
                printer.indent();
                {
                    final String content = Base64.getEncoder().encodeToString(input.readAllBytes());
                    final int contentLength = content.length();

                    int i = 0;
                    do {
                        final String chunk = content.substring(i, Math.min(i + MAX_STRING_LITERAL_LENGTH, contentLength));

                        i += chunk.length();

                        final boolean last = i == contentLength;
                        final String separator =  last?
                                "" :
                                ",";

                        printer.print(CharSequences.quote(chunk) + separator);
                        printer.lineStart();
                    } while(i < contentLength);
                }
                printer.outdent();

                printer.print(").stream()");
                printer.lineStart();

                printer.print(".collect(java.util.stream.Collectors.joining());");
                printer.lineStart();
            }

            printer.outdent();

            printer.print("}");
            printer.lineStart();
        }
    }

    private final static int MAX_STRING_LITERAL_LENGTH = 256 * 256 - 3;

    private final IndentingPrinter printer;
}
