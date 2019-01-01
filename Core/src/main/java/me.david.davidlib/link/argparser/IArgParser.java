package me.david.davidlib.link.argparser;

public interface IArgParser {

    void parseArgs(Object obj);
    void parseArgs(Object obj, String label);

    void parseArgs(Object obj, String[] args);
    void parseArgs(Object obj, String label, String[] args);

}