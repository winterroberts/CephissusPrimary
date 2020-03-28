package net.aionstudios.cephissus.filesystem;

import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class RecursiveWatcherService {

    private File rootFolder;
    private WatchService watcher;
    private ExecutorService executor;
    
    public void init() throws IOException {
    	rootFolder = new File("replicate/");
        watcher = FileSystems.getDefault().newWatchService();
        executor = Executors.newSingleThreadExecutor();
        startRecursiveWatcher();
    }

    public void cleanup() {
        try {
            watcher.close();
        } catch (IOException e) {
        	System.err.println(e.getMessage());
        	e.printStackTrace();
        }
        executor.shutdown();
    }

    private void startRecursiveWatcher() throws IOException {
    	System.out.println("Starting recursive watcher");
        final Map<WatchKey, Path> keys = new HashMap<>();

        Consumer<Path> register = p -> {
            if (!p.toFile().exists() || !p.toFile().isDirectory()) {
                throw new RuntimeException("folder " + p + " does not exist or is not a directory");
            }
            try {
                Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    	System.out.println("registering " + dir + " in watcher service");
                        WatchKey watchKey = dir.register(watcher, new WatchEvent.Kind[]{ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
                        keys.put(watchKey, dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Error registering path " + p);
            }
        };

        register.accept(rootFolder.toPath());

        executor.submit(() -> {
            while (true) {
                final WatchKey key;
                try {
                    key = watcher.take(); // wait for a key to be available
                } catch (InterruptedException ex) {
                    return;
                }

                final Path dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey " + key + " not recognized!");
                    continue;
                }

                for(final WatchEvent<?> e : key.pollEvents()) {
                	if (e.kind() != OVERFLOW) {
                		final Path absPath = dir.resolve((Path) e.context());
                		if (e.kind() == ENTRY_CREATE) {
                			final File f = absPath.toFile();
                			if(absPath.toFile().isDirectory()) {
                				register.accept(absPath);
                				dirRecurseCreate(f);
                			} else {
                                System.out.println("Detected new file " + f.getAbsolutePath());
                			}
                			logEvent(f.toPath().toAbsolutePath(), ENTRY_CREATE, f.isDirectory());
                		} else if (e.kind() == ENTRY_DELETE) {
                			final File f = absPath.toFile();
                            System.out.println("Deleted file " + f.getAbsolutePath());
                            logEvent(f.toPath().toAbsolutePath(), ENTRY_DELETE, f.isDirectory());
                		} else if (e.kind() == ENTRY_MODIFY) {
                			final File f = absPath.toFile();
                            System.out.println("Modified file " + f.getAbsolutePath());
                            logEvent(f.toPath().toAbsolutePath(), ENTRY_MODIFY, f.isDirectory());
                		}
                	}
                }
                key.reset();
            }
        });
    }
    
    private void dirRecurseCreate(File f) {
		if (f.exists()) {
			if (f.isDirectory()) {
				for(String child : f.list()) {
					File c = new File(fixFileName(f.toPath().toString())+"/"+fixFileName(child));
					dirRecurseCreate(c);
					logEvent(f.toPath().toAbsolutePath(), ENTRY_CREATE, true);
				}
			} else {
				System.out.println("Detected new file " + f.getAbsolutePath());
				logEvent(f.toPath().toAbsolutePath(), ENTRY_CREATE, false);
			}
		}
	}
    
    private String fixFileName(String s) {
		while(s.contains("\\\\")) {
			s = s.replace("\\\\", "\\");
		}
		return s.replace("\\", "/");
	}
    
    
    private void logEvent(Path pathAbsolute, Kind<Path> kind, boolean directory) {
    	if(kind == ENTRY_MODIFY && directory) {
    		return;
    	}
        Path pathBase = Paths.get("./replicate/").toAbsolutePath();
        Path pathRelative = pathBase.relativize(pathAbsolute);
        new FileDelta(pathRelative, kind==ENTRY_MODIFY?"MODIFY":(kind==ENTRY_DELETE?"DELETE":"CREATE"), System.currentTimeMillis(), directory);
    }
    
}