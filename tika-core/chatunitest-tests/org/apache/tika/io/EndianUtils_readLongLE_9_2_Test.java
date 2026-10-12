package org.apache.tika.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

public class EndianUtils_readLongLE_9_2_Test {

    @Test
    public void testReadLongLE() throws IOException, TikaException {
        InputStream stream = new ByteArrayInputStream(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 });
        long result = EndianUtils.readLongLE(stream);
        assertEquals(0x0807060504030201L, result);
    }
}
