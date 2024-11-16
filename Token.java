public class Token {
    private int pos;
    private String lexeme;
    public static String[] wordList = {"MEMOP", "LOADI", "ARITHOP", "OUTPUT", "NOP", "CONSTANT", "REGISTER", "COMMA", "INTO", "ENDFILE", "NEWLINE"};

    public Token() {
        this.pos = -1;
        this.lexeme = "";
    }

    public Token(int partofSpeech, String spelling) {
        this.pos = partofSpeech;
        this.lexeme = spelling;
    }

    public int getPos() {
        return pos;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String toString() {
        return "< " + wordList[pos] + ", " + "\"" + lexeme + "\"" + " >";
    }

}