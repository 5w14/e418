package ru.maxthetomas.votvevents.util.exceptions;

import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;

public class IntegerPointerException extends RuntimeException {
    public IntegerPointerException(String message) {
        super(message);
    }

    public static void youJustLostTheGame() {
        youJustLostTheGame("Unknown integer referenced or found during execution.");
    }

    public static void youJustLostTheGame(String message) {
        Util.backgroundExecutor().execute(() -> {
            Minecraft.getInstance().emergencySaveAndCrash(
                    CrashReport.forThrowable(new IntegerPointerException(message), message)
            );
        });
    }
}
