package com.o.spotifireo;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;

public class GlobalFunctions {

    public GlobalFunctions() {
    }

    public  Metadata getMp3Metadata(File file) throws FileNotFoundException {
        Metadata meta = null;
        try {

            InputStream input = new FileInputStream(file);
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();
            meta = metadata;

        } catch (FileNotFoundException e) {

            throw new FileNotFoundException("FileNotFound");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }

        return meta;
    }
}
