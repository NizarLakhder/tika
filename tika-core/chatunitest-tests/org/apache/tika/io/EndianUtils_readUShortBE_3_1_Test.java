package org.apache.tika.io;

import java.io.ByteArrayInputStream;
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

public class EndianUtils_readUShortBE_3_1_Test {

    @Test
    public void testReadUShortBE() throws IOException, TikaException {
        InputStream stream = new ByteArrayInputStream(new byte[] { 0x01, 0x02 });
        int result = EndianUtils.readUShortBE(stream);
        assertEquals(258, result);
    }
}
