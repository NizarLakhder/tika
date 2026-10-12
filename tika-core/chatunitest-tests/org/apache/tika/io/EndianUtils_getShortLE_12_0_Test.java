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

class EndianUtils_getShortLE_12_0_Test {

    @Test
    void testGetShortLE() throws IOException, TikaException {
        byte[] data = new byte[] { 0x01, 0x02 };
        short expected = 0x0102;
        short actual = EndianUtils.getShortLE(data);
        assertEquals(expected, actual);
    }

    @Test
    void testGetShortLEWithOffset() throws IOException, TikaException {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        short expected = 0x0201;
        short actual = EndianUtils.getShortLE(data, 2);
        assertEquals(expected, actual);
    }

    @Test
    void testGetShortLEWithInvalidData() throws IOException, TikaException {
        byte[] data = new byte[] { 0x01, 0x02, 0x03 };
        assertThrows(IOException.class, () -> EndianUtils.getShortLE(data));
    }
}
