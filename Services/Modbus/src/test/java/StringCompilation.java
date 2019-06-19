import javax.tools.*;



import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;



public class StringCompilation {

    private static final File outputFile;

    static {
        String outputPath = System.getProperty("user.dir") +
                File.separatorChar + "compiledClasses";
        outputFile = new File(outputPath);
        System.out.println(outputFile);
        if (!outputFile.exists()) {
            try {
                Files.createDirectory(outputFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<>();

        StandardJavaFileManager fileManager =
                compiler.getStandardFileManager(diagnostics, null, null);


        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                Arrays.asList(outputFile));

        JavaCompiler.CompilationTask task = compiler.getTask(null,
                fileManager, diagnostics, null, null, getCompilationUnits());

        if (!task.call()) {
            diagnostics.getDiagnostics().forEach(System.out::println);
        }
        fileManager.close();

        //loading and using our compiled class
        ClassLoader loader = new URLClassLoader(new URL[]{outputFile.toURI().toURL()});
        Class<ITest> test = (Class<ITest>) loader.loadClass("Test");
        ITest iTest = test.newInstance();
        iTest.doSomething();
    }

    public static Iterable<? extends JavaFileObject> getCompilationUnits() {
        JavaStringObject stringObject =
                new JavaStringObject("Test", getSource());
        return Arrays.asList(stringObject);
    }

    public static String getSource() {
        return "public class Test implements ITest{" +
                "public void doSomething(){" +
                "System.out.println(\" testing\");}}";
    }
}
