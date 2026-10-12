package org.apache.tika.io;

import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

class EndianUtils_getUIntBE_27_0_Test {

    @Test
    void testGetUIntBE() throws Exception {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
        int offset = 0;
        long expectedRetNum = 0x0102030405060708L;
        long retNum = EndianUtils.getUIntBE(data, offset);
        assertEquals(expectedRetNum, retNum);
    }
}
