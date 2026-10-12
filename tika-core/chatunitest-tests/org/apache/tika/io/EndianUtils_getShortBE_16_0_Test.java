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

public class EndianUtils_getShortBE_16_0_Test {

    @Test
    public void testGetShortBE() throws IOException, TikaException {
        byte[] data = new byte[] { (byte) 0xFF, (byte) 0x00 };
        short expected = (short) 0xFFFF;
        short actual = EndianUtils.getShortBE(data);
        assertEquals(expected, actual);
    }
}
