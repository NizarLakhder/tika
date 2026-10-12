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

class EndianUtils_readUShortLE_2_0_Test {

    @Test
    void testReadUShortLE() throws IOException, TikaException {
        byte[] data = new byte[] { 0x01, 0x02 };
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        int result = EndianUtils.readUShortLE(inputStream);
        assertEquals(258, result);
    }
}
