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

class EndianUtils_getIntLE_20_0_Test {

    @Test
    void testIntLE() throws Exception {
        byte[] data = new byte[] { 1, 2, 3, 4 };
        int expected = 0x04030201;
        int actual = EndianUtils.getIntLE(data);
        assertEquals(expected, actual);
    }
}
