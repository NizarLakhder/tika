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

public class EndianUtils_getIntBE_23_0_Test {

    private static final int LONG_SIZE = 8;

    @Test
    public void testGetIntBE() throws Exception {
        byte[] data = new byte[LONG_SIZE];
        int i = 0;
        data[i++] = (byte) 0xFF;
        data[i++] = (byte) 0xFF;
        data[i++] = (byte) 0xFF;
        data[i++] = (byte) 0xFF;
        data[i++] = (byte) 0xFF;
        data[i++] = (byte) 0xFF;
        data[i++] = (byte) 0xFF;
        data[i++] = (byte) 0xFF;
        int result = EndianUtils.getIntBE(data, 0);
        assertEquals(0xFFFFFFFFFFFFFFFFL, (long) result);
    }
}
