package me.plascmabue.cobblemonbattlefactory.debug;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger fichier dédié aux combats BattleFactory : écrit TOUT le déroulé (watchdog, dump d'état, force IA,
 * journal Showdown de fin) dans {@code config/BattleFactory/battle-debug.log}, séparé du latest.log noyé
 * dans le spam serveur. Chaque ligne est horodatée. Thread-safe (append synchronisé). Best-effort : toute
 * erreur d'écriture est avalée pour ne jamais casser un combat.
 */
public final class BattleLog {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final Object LOCK = new Object();
    private static Path file;
    private static boolean disabled = false;

    private BattleLog() {}

    private static Path file() {
        if (file == null) {
            file = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").resolve("battle-debug.log");
        }
        return file;
    }

    /** Écrit une ligne horodatée dans le fichier de log des combats. */
    public static void log(String message) {
        if (disabled) return;
        String line = "[" + LocalTime.now().format(TS) + "] " + message + System.lineSeparator();
        synchronized (LOCK) {
            try {
                Path f = file();
                Files.createDirectories(f.getParent());
                Files.writeString(f, line, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException | RuntimeException e) {
                disabled = true;
            }
        }
    }

    /** Variante formatée facultative (style SLF4J avec {}). */
    public static void log(String fmt, Object... args) {
        if (disabled) return;
        String msg = fmt;
        for (Object a : args) {
            int i = msg.indexOf("{}");
            if (i < 0) break;
            msg = msg.substring(0, i) + String.valueOf(a) + msg.substring(i + 2);
        }
        log(msg);
    }
}
