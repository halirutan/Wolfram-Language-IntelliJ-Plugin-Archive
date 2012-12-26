package org.ipcu.mathematicaPlugin;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 12/23/12
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class MathematicaFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(MathematicaFileType.INSTANCE, MathematicaFileType.DEFAULT_EXTENSION);
    }
}
