package impl;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.Optional;

import impl.parserSelectors.ExtensionParserSelector;

public class Main {
    private void run(Path path) {
        ExtensionParserSelector selector = ParserSelectorFactory.buildExtensionParserSelector();
        String pathStringLowerCase = path.toString().toLowerCase();
        String ext = pathStringLowerCase.substring(pathStringLowerCase.lastIndexOf('.'));
        Optional<AbstractParser> parser = selector.select(ext);
        if (!parser.isPresent()) {
            System.err.printf("error: no parser found for extension %s\n", ext);
            System.exit(1);
        }

        try {
            Report report = parser.get().parseFile(path);
            System.out.println(report);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter filename: ");
        Path path = Path.of(scanner.nextLine());
        new Main().run(path);
    }
}
