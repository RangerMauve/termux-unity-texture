package moe.mauve.ranger.termuxunitytexture;

import android.view.KeyEvent;

import com.termux.terminal.KeyHandler;
import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;

import java.io.File;

/**
 * Created by Mauve on 2018-03-12.
 */

// Debug this shiz using
// adb logcat -s Unity PackageManager dalvikvm DEBUG

public class TerminalInstance {
    public static final String FILES_PATH = "/data/data/com.termux/files";
    public static final String PREFIX_PATH = FILES_PATH + "/usr";
    public static final String HOME_PATH = FILES_PATH + "/home";

    private static TerminalInstance activeInstance = null;

    private String renderedState;
    private TerminalSession session;
    private int topRow = 0;
    private boolean shouldRender = true;

    public void setActive(boolean active){
        if(active)
            activeInstance = this;
        else activeInstance = null;
    }

    public static void processKey(KeyEvent e) {
        if(activeInstance == null)
            return;

        int mod = 0;
        if(e.isCtrlPressed())
            mod = mod | KeyHandler.KEYMOD_ALT;
        if(e.isShiftPressed())
            mod = mod | KeyHandler.KEYMOD_SHIFT;
        if(e.isAltPressed())
            mod = mod | KeyHandler.KEYMOD_ALT;

        int code = e.getKeyCode();

        String toWrite = KeyHandler.getCode(code, mod, false, false);

        activeInstance.session.write(toWrite);
    }

    public TerminalInstance() {
        this.session = createTermSession();
        this.session.updateSize(80, 24);
    }

    public String render(){
        try {
            if(this.shouldRender){
                this.renderedState = TerminalHTMLRenderer.render(this.session.getEmulator(), this.topRow);
                this.shouldRender = false;
            }
            return  this.renderedState;
        } catch(Exception e){
            return e.getMessage();
        }
    }

    public void setScroll(int topRow){
        if(this.topRow != topRow){
            shouldRender = true;
            this.topRow = topRow;
        }
    }

    // Taken from termux-float https://github.com/termux/termux-float/blob/906ca2d1561ec312a2dd2762235677ca0b0711a0/app/src/main/java/com/termux/window/TermuxFloatService.java
    private TerminalSession createTermSession() {
        new File(HOME_PATH).mkdirs();

        final String termEnv = "TERM=xterm-256color";
        final String homeEnv = "HOME=" + TerminalInstance.HOME_PATH;
        final String prefixEnv = "PREFIX=" + TerminalInstance.PREFIX_PATH;
        final String androidRootEnv = "ANDROID_ROOT=" + System.getenv("ANDROID_ROOT");
        final String androidDataEnv = "ANDROID_DATA=" + System.getenv("ANDROID_DATA");
        // EXTERNAL_STORAGE is needed for /system/bin/am to work on at least
        // Samsung S7 - see https://plus.google.com/110070148244138185604/posts/gp8Lk3aCGp3.
        final String externalStorageEnv = "EXTERNAL_STORAGE=" + System.getenv("EXTERNAL_STORAGE");
        final String ps1Env = "PS1=$ ";
        final String ldEnv = "LD_LIBRARY_PATH=" + TerminalInstance.PREFIX_PATH + "/lib";
        final String langEnv = "LANG=en_US.UTF-8";
        final String pathEnv = "PATH=" + TerminalInstance.PREFIX_PATH + "/bin:" + TerminalInstance.PREFIX_PATH + "/bin/applets";
        String[] env = new String[]{termEnv, homeEnv, prefixEnv, ps1Env, ldEnv, langEnv, pathEnv, androidRootEnv, androidDataEnv, externalStorageEnv};

        String executablePath = null;
        String[] args;
        String shellName = null;

        for (String shellBinary : new String[]{"login", "bash", "zsh"}) {
            File shellFile = new File(PREFIX_PATH + "/bin/" + shellBinary);
            if (shellFile.canExecute()) {
                executablePath = shellFile.getAbsolutePath();
                shellName = "-" + shellBinary;
                break;
            }
        }

        if (executablePath == null) {
            // Fall back to system shell as last resort:
            executablePath = "/system/bin/sh";
            shellName = "-sh";
        }

        args = new String[]{shellName};

        return new TerminalSession(executablePath, HOME_PATH, args, env, new TerminalSession.SessionChangedCallback() {
            @Override
            public void onTitleChanged(TerminalSession changedSession) {
                shouldRender = true;
                // Ignore for now.
            }

            @Override
            public void onTextChanged(TerminalSession changedSession) {
                shouldRender = true;
                // Rerender
            }

            @Override
            public void onSessionFinished(TerminalSession finishedSession) {
                // Close the thing?
            }

            @Override
            public void onClipboardText(TerminalSession pastingSession, String text) {
                // Save to local variable?
            }

            @Override
            public void onBell(TerminalSession riningSession) {
                // Notify about ring
            }

            @Override
            public void onColorsChanged(TerminalSession session) {
                shouldRender = true;
                // Rerender
            }
        });
    }
}
