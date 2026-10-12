package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.EndianUtils;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

class EndianUtils_getShortBE_17_0_Test {

    @Test
    void testGetShortBE() throws IOException, TikaException {
        byte[] data = new byte[2];
        data[0] = (byte) 0xFF;
        data[1] = (byte) 0xAB;
        short result = EndianUtils.getShortBE(data, 0);
        assertEquals((short) 0xFFFFABFF, result);
    }

    @Test
    void testInvalidData() {
        byte[] data = new byte[1];
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> EndianUtils.getShortBE(data, 0));
        assertEquals("Invalid data length for getShortBE", exception.getMessage());
    }
}
