package org.apache.tika.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.InputStream;

public class EndianUtils_readLongBE_10_2_Test {

    @Test
    public void testReadLongBE() throws IOException, TikaException {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xFF;
        bytes[2] = (byte) 0xFF;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0xFF;
        bytes[5] = (byte) 0xFF;
        bytes[6] = (byte) 0xFF;
        bytes[7] = (byte) 0xFF;
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        long result = EndianUtils.readLongBE(stream);
        assertEquals(0xFFFFFFFFFFFFFFFFL, result);
    }
}
