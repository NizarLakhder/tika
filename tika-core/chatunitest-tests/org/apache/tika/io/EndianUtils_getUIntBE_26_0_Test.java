package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

class EndianUtils_getUIntBE_26_0_Test {

    @Test
    void testEndianUtils() throws IOException, TikaException {
        byte[] data = new byte[8];
        data[0] = (byte) 0xFF;
        data[1] = (byte) 0xFF;
        data[2] = (byte) 0xFF;
        data[3] = (byte) 0xFF;
        data[4] = (byte) 0xFF;
        data[5] = (byte) 0xFF;
        data[6] = (byte) 0xFF;
        data[7] = (byte) 0xFF;
        long result = EndianUtils.getUIntBE(data);
        assertEquals(0xFFFFFFFFl, result);
    }
}
