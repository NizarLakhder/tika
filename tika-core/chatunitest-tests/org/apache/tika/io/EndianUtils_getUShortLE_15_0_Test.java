package org.apache.tika.io;

import org.apache.tika.io.EndianUtils;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

class EndianUtils_getUShortLE_15_0_Test {

    @Test
    void testGetUShortLE() throws Exception {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        int offset = 0;
        int result = EndianUtils.getUShortLE(data, offset);
        assertEquals(257, result);
    }
}
