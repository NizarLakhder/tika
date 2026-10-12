package org.apache.tika.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.InputStream;

public class EndianUtils_readUIntBE_5_0_Test {

    @Test
    public void testReadUIntBE() throws IOException, TikaException {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        InputStream stream = new ByteArrayInputStream(data);
        long result = EndianUtils.readUIntBE(stream);
        assertEquals(0x01020304L, result);
    }
}
