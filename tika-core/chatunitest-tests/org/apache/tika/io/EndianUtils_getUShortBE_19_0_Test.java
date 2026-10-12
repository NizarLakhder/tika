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

class EndianUtils_getUShortBE_19_0_Test {

    @Test
    void testGetUShortBE() throws Exception {
        byte[] data = new byte[] { 0x01, 0x02 };
        int offset = 0;
        int expected = 0x0102;
        int actual = EndianUtils.getUShortBE(data, offset);
        assertEquals(expected, actual);
    }
}
