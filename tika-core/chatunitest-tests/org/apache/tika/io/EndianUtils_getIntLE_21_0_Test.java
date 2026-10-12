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

public class EndianUtils_getIntLE_21_0_Test {

    @Test
    public void testGetIntLE() throws Exception {
        byte[] data = new byte[] { 1, 2, 3, 4 };
        int result = EndianUtils.getIntLE(data, 0);
        assertEquals(16909060, result);
    }
}
