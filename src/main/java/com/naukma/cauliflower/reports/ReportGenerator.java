package com.naukma.cauliflower.reports;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Slavko_O on 09.12.2014.
 */
public interface ReportGenerator {
    abstract void writeInStream(OutputStream out) throws IOException;
}
