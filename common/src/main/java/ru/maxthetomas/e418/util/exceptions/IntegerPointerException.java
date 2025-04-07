package ru.maxthetomas.e418.util.exceptions;

import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * A special little class for all of your game crashing needs. :)
 */
public class IntegerPointerException extends RuntimeException {
    private static final List<String> EXCEPTIONS = List.of(
            "Unknown integer referenced or found during execution.",
            "HTTP Error 404 when attempting to fetch a resource.",
            "HTTP Error 418 when attempting to fetch a resource.",
            "Attempt to index global 'integer' (a nil value).",
            "I'm a teapot.",
            "This page isn't working: www.maxthetomas.ru redirected you too many times.",
            "This pointer does not exist.",
            "Cannot combine \"I am\" and \"Steve\".",
            "Fatal error turned out to be re-e-eally fatal :skull:",
            "Node graph out of date. Rebuilding...",
            "Ping failed",
            "Cannot print: Printer is on fire!",
            "Cannot print: No cyan paint!",
            "No message provided.",
            "C:\\startup.lua not found!"
    );

    public IntegerPointerException(String message) {
        super(message);
    }

    /**
     * Intentional videogame crash with a random message.
     */
    public static void youJustLostTheGame() {
        youJustLostTheGame(EXCEPTIONS.get(RandomSource.create().nextInt(EXCEPTIONS.size())));
    }

    /**
     * Intentional videogame crash.
     */
    public static void youJustLostTheGame(String message) {
        Util.backgroundExecutor().execute(() -> {
            Minecraft.getInstance().emergencySaveAndCrash(
                    CrashReport.forThrowable(new IntegerPointerException(message), message)
            );
        });
    }
}
