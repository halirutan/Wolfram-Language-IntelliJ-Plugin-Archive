package de.halirutan.mathematica;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import de.halirutan.mathematica.fileTypes.MathematicaFileType;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (4/4/13)
 */
public class MathematicaFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(MathematicaFileType.INSTANCE, "m;nb;mma");
    }

}

