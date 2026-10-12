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

class EndianUtils_readUIntLE_4_0_Test {

    @Test
    void testReadUIntLE() throws IOException, TikaException {
        InputStream stream = new ByteArrayInputStream(new byte[] { 0x01, 0x02, 0x03, 0x04 });
        assertEquals(0x04030201L, EndianUtils.readUIntLE(stream));
        stream = new ByteArrayInputStream(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 });
        assertEquals(0x08070605L, EndianUtils.readUIntLE(stream));
    }
}
