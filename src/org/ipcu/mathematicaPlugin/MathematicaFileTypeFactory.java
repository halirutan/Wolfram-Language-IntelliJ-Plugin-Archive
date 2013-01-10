package org.ipcu.mathematicaPlugin;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.ipcu.mathematicaPlugin.fileTypes.MathematicaFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 12/23/12
 * Time: 9:53 PM
 * Purpose:
 */
public class MathematicaFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(MathematicaFileType.INSTANCE, "m;nb;mma");
    }

}

