package moe.mauve.ranger.termuxunitytexture;

import com.termux.terminal.TerminalBuffer;
import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalRow;
import com.termux.terminal.TextStyle;

/**
 * Created by Mauve on 2018-03-12.
 */

public class TerminalHTMLRenderer {
    static String render(TerminalEmulator mEmulator, int topRow) {
        StringBuilder builder = new StringBuilder();

        final int endRow = topRow + mEmulator.mRows;
        final int columns = mEmulator.mColumns;
        final int cursorCol = mEmulator.getCursorCol();
        final int cursorRow = mEmulator.getCursorRow();
        final boolean cursorVisible = mEmulator.isShowingCursor();
        final TerminalBuffer screen = mEmulator.getScreen();
        final int[] palette = mEmulator.mColors.mCurrentColors;
        final int cursorShape = mEmulator.getCursorStyle();

        for (int row = topRow; row < endRow; row++) {
            final int cursorX = (row == cursorRow && cursorVisible) ? cursorCol : -1;

            TerminalRow lineObject = screen.allocateFullLineIfNecessary(screen.externalToInternalRow(row));
            final char[] line = lineObject.mText;

            int currentCharIndex = 0;
            long lastRunStyle = 0;

            builder.append("<div>");
            builder.append(line);
            builder.append("</div>");
        }

        return builder.toString();
    }
}
