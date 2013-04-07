package de.halirutan.mathematica.codeInsight;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 3/22/13
 * Time: 4:13 AM
 * Purpose:
 */
public class MathematicaCommenter implements Commenter {
    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return "(*";
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return "*)";
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
