package org.apache.tika.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

public class EndianUtils_readShortBE_1_2_Test {

    @Test
    public void testReadShortBE() throws IOException, TikaException {
        byte[] data = new byte[] { (byte) 0xFF, (byte) 0x00 };
        InputStream stream = new ByteArrayInputStream(data);
        short result = EndianUtils.readShortBE(stream);
        Assertions.assertEquals(0xFFFF, result);
    }
}
