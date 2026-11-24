package impl;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.Optional;

public class Main {
    private void run(Path path) throws Exception {
        ParserSelector<String> selector = ParserSelectorFactory.buildExtensionParserSelector();
        String pathStringLowerCase = path.toString().toLowerCase();
        int dot = pathStringLowerCase.lastIndexOf('.');
        if (dot < 0) {
            System.err.println("error: no extension");
            return;
        }
        String ext = pathStringLowerCase.substring(dot);
        Optional<AbstractParser> parser = selector.select(ext);
        if (!parser.isPresent()) {
            System.err.printf("error: no parser found for extension %s\n", ext);
            return;
        }

        Report report = parser.get().parseFile(path);
        System.out.println(report);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Main app = new Main();
        for (;;) {
            System.out.print("Enter filename: ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()
                || line.equals("quit")
                || line.equals("exit")) break;
            try {
                app.run(Path.of(line));
            } catch (Exception e) {
                System.err.println("error: " + e);
            }
        }
    }
}
