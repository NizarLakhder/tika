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

class EndianUtils_readIntME_8_1_Test {

    @Test
    void testReadIntME() throws IOException, TikaException {
        byte[] bytes = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        InputStream stream = new ByteArrayInputStream(bytes);
        int result = EndianUtils.readIntME(stream);
        assertEquals(0x04030201, result);
    }

    @Test
    void testReadIntMEWithUnderflow() throws IOException, TikaException {
        byte[] bytes = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        InputStream stream = new ByteArrayInputStream(bytes);
        IOException exception = assertThrows(IOException.class, () -> EndianUtils.readIntME(stream));
        assertTrue(exception.getMessage().contains("BufferUnderrunException"));
    }
}
