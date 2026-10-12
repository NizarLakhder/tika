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

public class EndianUtils_getShortLE_13_0_Test {

    @Test
    public void testGetShortLE() throws Exception {
        byte[] data = new byte[] { 0x01, 0x02 };
        int offset = 0;
        short expected = 0x0201;
        short actual = EndianUtils.getShortLE(data, offset);
        assertEquals(expected, actual);
    }
}
