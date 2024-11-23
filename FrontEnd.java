import java.io.BufferedReader; 

public class FrontEnd {
    public static String[] wordList = {"MEMOP", "LOADI", "ARITHOP", "OUTPUT", "NOP", "CONSTANT", "REGISTER", "COMMA", "INTO", "ENDFILE", "NEWLINE"};
    public static int idx = 0;
    public static int lineNum = 1;
    public static int errCt = 0;
    public static int opCt = 0;
    public static OpList internalOpList = new OpList();

    public static Token readWord(String line) {
        if (line == null) {
            // return EOF token
            Token endfile = new Token(9, "");
            return endfile;
        } else {
            line += '\n';
            char c = line.charAt(idx);
            if (c == ' ' || c == '\t') { // spaces
                // System.out.println("Found spaces");
                idx++;
                c = line.charAt(idx);
                while (c == ' ' || c == '\t') {
                    idx++;
                    c = line.charAt(idx);
                }
            } 
            if (c == '/') { // comments
                idx++;
                c = line.charAt(idx);
                if (c == '/') {
                    // System.out.println("COMMENT token found.");
                    Token comment = new Token(10, "\\n");
                    return comment;
                } else {
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else if (c == 'l') { // load, loadl, lshift
                idx++;
                c = line.charAt(idx);
                if (c == 'o') { // load, loadl
                    idx++;
                    c = line.charAt(idx);
                    if (c == 'a') { // load, loadl
                        idx++;
                        c = line.charAt(idx);
                        if (c == 'd') {
                            idx++;
                            c = line.charAt(idx);
                            if (c == 'I') {
                                idx++;
                                c = line.charAt(idx);
                                if (c == ' ' || c == '\t') {
                                    // return LOADL token
                                    // System.out.println("LOADI token found.");
                                    opCt++;
                                    Token loadI = new Token(1, "loadI");
                                    return loadI;
                                } else {
                                    // return error
                                    errCt++;
                                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                    return new Token(10, "eol");
                                }
                            } else if (c == ' ' || c == '\t') {
                                // return LOAD token
                                // System.out.println("LOAD token found.");
                                opCt++;
                                Token load = new Token(0, "load");
                                return load;
                            } else {
                                // return error
                                errCt++;
                                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                return new Token(10, "eol");
                            }
                        } else {
                            // return error
                            errCt++;
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                            return new Token(10, "eol");
                        }
                    } else {
                        // return error
                        errCt++;
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                        return new Token(10, "eol");
                    }
                } else if (c == 's') { // lshift
                    idx++;
                    c = line.charAt(idx);
                    if (c == 'h') {
                        idx++;
                        c = line.charAt(idx);
                        if (c == 'i') {
                            idx++;
                            c = line.charAt(idx);
                            if (c == 'f') {
                                idx++;
                                c = line.charAt(idx);
                                if (c == 't') {
                                    idx++;
                                    c = line.charAt(idx);
                                    if (c == ' ' || c == '\t') {
                                        // return LSHIFT token
                                        // System.out.println("LSHIFT token found.");
                                        opCt++;
                                        Token lshift = new Token(2, "lshift");
                                        return lshift;
                                    } else {
                                        // return error
                                        errCt++;
                                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                        return new Token(10, "eol");
                                    }
                                } else {
                                    // return error
                                    errCt++;
                                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                    return new Token(10, "eol");
                                }
                            } else {
                                // return error
                                errCt++;
                                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                return new Token(10, "eol");
                            }
                        } else {
                            // return error
                            errCt++;
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                            return new Token(10, "eol");
                        }
                    } else {
                        // return error
                        errCt++;
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                        return new Token(10, "eol");
                    }
                } else {
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else if (c == 's') { // store, sub
                idx++;
                c = line.charAt(idx);
                if (c == 't') {
                    idx++;
                    c = line.charAt(idx);
                    if (c == 'o') {
                        idx++;
                        c = line.charAt(idx);
                        if (c == 'r') {
                            idx++;
                            c = line.charAt(idx);
                            if (c == 'e') {
                                // return STORE token
                                idx++;
                                c = line.charAt(idx);
                                if (c == ' ' || c == '\t') {
                                    // return STORE token
                                    // System.out.println("STORE token found.");
                                    opCt++;
                                    Token store = new Token(0, "store");
                                    return store;
                                } else {
                                    // return error
                                    errCt++;
                                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                    return new Token(10, "eol");
                                }
                            } else {
                                // return error
                                errCt++;
                                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                return new Token(10, "eol");
                            }
                        } else {
                            // return error
                            errCt++;
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                            return new Token(10, "eol");
                        }
                    } else {
                        // return error
                        errCt++;
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                        return new Token(10, "eol");
                    }
                } else if (c == 'u') {
                    idx++;
                    c = line.charAt(idx);
                    if (c == 'b') {
                        idx++;
                        c = line.charAt(idx);
                        if (c == ' ' || c == '\t') {
                        // return SUB token
                        // System.out.println("SUB token found.");
                        opCt++;
                        Token sub = new Token(2, "sub");
                        return sub;
                        } else {
                            // return error
                            errCt++;
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                            return new Token(10, "eol");
                        }
                    } else {
                        // return error
                        errCt++;
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                        return new Token(10, "eol");
                    }
                } else {
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else if (c == 'a') { // add
                idx++;
                c = line.charAt(idx);
                if (c == 'd') {
                    idx++;
                    c = line.charAt(idx);
                    if (c == 'd') {
                        idx++;
                        c = line.charAt(idx);
                        if (c == ' ' || c == '\t') {
                            // return ADD token
                            // System.out.println("ADD token found.");
                            opCt++;
                            Token add = new Token(2, "add");
                            return add;
                        } else {
                            // return error
                            errCt++;
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                            return new Token(10, "eol");
                        }
                    } else {
                        // return error
                        errCt++;
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                        return new Token(10, "eol");
                    }
                } else {
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else if (c == 'm') { // mult
                idx++;
                c = line.charAt(idx);
                if (c == 'u') {
                    idx++;
                    c = line.charAt(idx);
                    if (c == 'l') {
                        idx++;
                        c = line.charAt(idx);
                        if (c == 't') {
                            idx++;
                            c = line.charAt(idx);
                            if (c == ' ' || c == '\t') {
                                // return MULT token
                                // System.out.println("MULT token found.");
                                opCt++;
                                Token mult = new Token(2, "mult");
                                return mult;
                            } else {
                                // return error
                                errCt++;
                                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                return new Token(10, "eol");
                            }
                        } else {
                            // return error
                            errCt++;
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                            return new Token(10, "eol");
                        }
                    } else {
                        // return error
                        errCt++;
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                        return new Token(10, "eol");
                    }
                } else {
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else if (c == 'r') { //rshift, register
                idx++;
                c = line.charAt(idx);
                if (c == 's') { // rshift
                    idx++;
                    c = line.charAt(idx);
                    if (c == 'h') {
                        idx++;
                        c = line.charAt(idx);
                        if (c == 'i') {
                            idx++;
                            c = line.charAt(idx);
                            if (c == 'f') {
                                idx++;
                                c = line.charAt(idx);
                                if (c == 't') {
                                    idx++;
                                    c = line.charAt(idx);
                                    if (c == ' ' || c == '\t') {
                                        // return RSHIFT token
                                        // System.out.println("RSHIFT token found.");
                                        opCt++;
                                        Token rshift = new Token(2, "rshift");
                                        return rshift;
                                    } else {
                                        // return error
                                        errCt++;
                                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                        return new Token(10, "eol");
                                    }
                                } else {
                                    // return error
                                    errCt++;
                                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                    return new Token(10, "eol");
                                }
                            } else {
                                // return error
                                errCt++;
                                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                return new Token(10, "eol");
                            }
                        } else {
                            // return error
                            errCt++;
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                            return new Token(10, "eol");
                        }
                    }
                } else if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') { // register
                    // System.out.println("You have reached the register part of this scanner with the number " + c);
                    String register = "r";
                    register += c;
                    idx++;
                    c = line.charAt(idx);
                    while (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') {
                        register += c;
                        idx++;
                        c = line.charAt(idx);
                    }
                    if (c == ' ' || c == '\t' || c == ',' || c == '=' || c == '\n') {
                        // return register token
                        // System.out.println("REGISTER token found.");
                        Token registerToken = new Token(6, register);
                        return registerToken;
                    } else {
                        // System.out.println("we entered the long register else clause");
                        while (c != ' ' && c != '\t' && c != ',' && c != '=' && c != '\n') {
                            register += c;
                            idx++;
                            c = line.charAt(idx);
                        }
                        // return error
                        errCt++;
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                        return new Token(10, "eol");
                    }
                } else {
                    while (c != ' ' && c != '\t' && c != ',' && c != '=' && c != '\n') {
                            idx++;
                            c = line.charAt(idx);
                    }
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else if (c =='o') { // output
                idx++;
                c = line.charAt(idx);
                if (c == 'u') {
                    idx++;
                    c = line.charAt(idx);
                    if (c == 't') {
                        idx++;
                        c = line.charAt(idx);
                        if (c == 'p') {
                            idx++;
                            c = line.charAt(idx);
                            if (c == 'u') {
                                idx++;
                                c = line.charAt(idx);
                                if (c == 't') {
                                    idx++;
                                    c = line.charAt(idx);
                                    if (c == ' ' || c == '\t') {
                                        // return OUTPUT token
                                        // System.out.println("OUTPUT token found.");
                                        opCt++;
                                        Token output = new Token(3, "output");
                                        return output;
                                    } else {
                                        // return error
                                        errCt++;
                                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                        return new Token(10, "eol");
                                    }
                                } else {
                                    // return error
                                    errCt++;
                                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                    return new Token(10, "eol");
                                }
                            } else {
                                // return error
                                errCt++;
                                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                                return new Token(10, "eol");
                            }
                        } else {
                            // return error
                            errCt++;
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                            return new Token(10, "eol");
                        }
                    } else {
                        // return error
                        errCt++;
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                        return new Token(10, "eol");
                    }
                } else {
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else if (c == 'n') { // nop
                idx++;
                c = line.charAt(idx);
                if (c == 'o') {
                    idx++;
                    c = line.charAt(idx);
                    if (c == 'p') {
                        idx++;
                        c = line.charAt(idx);
                        if (c == ' ' || c == '\t' || c == '\n') {
                            // return NOP token
                            // System.out.println("NOP token found.");
                            opCt++;
                            Token nop = new Token(4, "nop");
                            return nop;
                        } else {
                            // return error
                            errCt++;
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                            return new Token(10, "eol");
                        }
                    } else {
                        // return error
                        errCt++;
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                        return new Token(10, "eol");
                    }
                } else {
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else if (c == ',') { // comma
                idx++;
                c = line.charAt(idx);
                // return comma token
                // System.out.println("COMMA token found.");
                Token comma = new Token(7, ",");
                return comma;
            } else if (c == '=') { // into
                idx++;
                c = line.charAt(idx);
                if (c == '>') {
                    // return INTO token
                    idx++;
                    c = line.charAt(idx);
                    // System.out.println("INTO token found.");
                    Token into = new Token(8, "=>");
                    return into;
                } else {
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else if (c == '\n') { // EOL
                // return EOL
                // System.out.println("EOL token found.");
                Token eol = new Token(10, "\\n");
                return eol;
            } else if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') {
                String constant = "";
                constant += c;
                idx++;
                c = line.charAt(idx);
                while (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') {
                    constant += c;
                    idx++;
                    c = line.charAt(idx);
                }
                if (c == ' ' || c == '\t' || c == '\n' || c == '=') {
                    // return CONSTANT token
                    // System.out.println("CONSTANT token found.");
                    Token constantToken = new Token(5, constant);
                    return constantToken;
                } else {
                    // return error
                    errCt++;
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                    return new Token(10, "eol");
                }
            } else {
                // return error
                errCt++;
                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + c + '"' + " is not a valid word.");
                return new Token(10, "eol");
            }
        // System.out.println("No token found.");
        return new Token();
        }
    }

    public OpList doParse(BufferedReader br) throws Exception {
        try {
            String line = br.readLine();
            line += '\n';
            Token word = readWord(line);
            int pos = word.getPos();
            while (pos != 9) {
                switch (pos) {
                    case 0:
                        finishMemop(line, word.getLexeme());
                        break;
                    case 1:
                        finishLoadI(line);
                        break;
                    case 2:
                        finishArithop(line, word.getLexeme());
                        break;
                    case 3:
                        finishOutput(line);
                        break;
                    case 4:
                        finishNop(line);
                        break;
                    case 10:
                        // System.out.println("Found a comment.");
                        break;
                    default:
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + "No valid operation found.");
                        break;
                }
                line = br.readLine();
                lineNum++;
                idx = 0;
                if (line != null) {
                    line += '\n';
                }
                word = readWord(line);
                pos = word.getPos();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        // internalOpList.traverseForward();
        return internalOpList;
    }

    public static void finishMemop(String line, String lexeme) {
        try {
            Token reg1 = readWord(line);
            int pos = reg1.getPos();
            // not a REGISTER
            if (pos != 6) {
                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not a register");
                return;
            } else { // is a REGISTER
                Token into = readWord(line);
                pos = into.getPos();
                // not INTO
                if (pos != 8) {
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not =>");
                    return;
                }
                Token reg2 = readWord(line);
                pos = reg2.getPos();
                // not a register
                if (pos != 6) {
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not a register");
                    return;
                } else { // is a REGISTER
                    Token eol = readWord(line);
                    pos = eol.getPos();
                    // if EOL
                    if (pos == 10) {
                        int opcode = -1;
                        if (lexeme.equals("load")) {
                            opcode = 0;
                        } else if (lexeme.equals("store")) {
                            opcode = 2;
                        }
                        // build the IR for this Op
                        // Add it to the list of Ops
                        internalOpList.insertAtEnd(new InternalRep(lineNum, opcode, Integer.parseInt(reg1.getLexeme().substring(1)), -1, -1, -1, -1, -1, -1, -1, Integer.parseInt(reg2.getLexeme().substring(1)), -1, -1, -1));
                    } else { // not EOL
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not the end of the line");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void finishLoadI(String line) {
        try {
            Token constant = readWord(line);
            int pos = constant.getPos();
            // not a CONSTANT
            if (pos != 5) {
                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not a constant");
                return;
            } else { // is a CONSTANT
                Token into = readWord(line);
                pos = into.getPos();
                // not into
                if (pos != 8) {
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not =>");
                    return;
                } else { // is INTO
                    Token register = readWord(line);
                    pos = register.getPos();
                    // not a REGISTER
                    if (pos != 6) {
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not a register");
                        return;
                    } else { // is a REGISTER
                        Token eol = readWord(line);
                        pos = eol.getPos();
                        // if EOL
                        if (pos == 10) {
                            // build the IR for this Op
                            // Add it to the list of Ops
                            internalOpList.insertAtEnd(new InternalRep(lineNum, 1, Integer.parseInt(constant.getLexeme()), -1, -1, -1, -1, -1, -1, -1, Integer.parseInt(register.getLexeme().substring(1)), -1, -1, -1));
                        } else { // not EOL
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not the end of the line");
                            return;
                        }
                    }
                } 
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void finishArithop(String line, String lexeme) {
        try {
            Token reg1 = readWord(line);
            int pos = reg1.getPos();
            // not a REGISTER
            if (pos != 6) {
                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not a register");
                return;
            } else { // is a REGISTER
                Token comma = readWord(line);
                pos = comma.getPos();
                // is not a comma
                if (pos != 7) {
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not a comma");
                    return;
                } else { // is a COMMA
                    Token reg2 = readWord(line);
                    pos = reg2.getPos();
                    // not a REGISTER
                    if (pos != 6) {
                        System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not a register");
                        return;
                    } else { // is a REGISTER
                        Token into = readWord(line);
                        pos = into.getPos();
                        // is not INTO
                        if (pos != 8) {
                            System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not =>");
                            return;
                        } else { // is INTO
                            Token reg3 = readWord(line);
                            pos = reg3.getPos();
                            // not a REGISTER
                            if (pos != 6) {
                                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not a register");
                                return;
                            } else {
                                Token eol = readWord(line);
                                pos = eol.getPos();
                                // if EOL
                                if (pos == 10) {
                                    // build the IR for this Op
                                    // Add it to the list of Ops
                                    int opcode = -1;
                                    if (lexeme.equals("add")) {
                                        opcode = 3;
                                    } else if (lexeme.equals("sub")) {
                                        opcode = 4;
                                    } else if (lexeme.equals("mult")) {
                                        opcode = 5;
                                    } else if (lexeme.equals("lshift")) {
                                        opcode = 6;
                                    } else if (lexeme.equals("rshift")) {
                                        opcode = 7;
                                    }
                                    internalOpList.insertAtEnd(new InternalRep(lineNum, opcode, Integer.parseInt(reg1.getLexeme().substring(1)), -1, -1, -1, Integer.parseInt(reg2.getLexeme().substring(1)), -1, -1, -1, Integer.parseInt(reg3.getLexeme().substring(1)), -1, -1, -1));

                                } else { // not EOL
                                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not the end of the line");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void finishOutput(String line) {
        try {
            Token constant = readWord(line);
            int pos = constant.getPos();
            // if not CONSTANT
            if (pos != 5) {
                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not a constant");
                return;
            } else { // is a CONSTANT
                Token eol = readWord(line);
                pos = eol.getPos();
                // if EOL
                if (pos == 10) {
                    // build the IR for this Op
                    // Add it to the list of Ops
                    internalOpList.insertAtEnd(new InternalRep(lineNum, 8, Integer.parseInt(constant.getLexeme()), -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1));
                } else { // not EOL
                    System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not the end of the line");
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void finishNop(String line) {
        try {
            Token word = readWord(line);
            int pos = word.getPos();
            // not EOL
            if (pos != 10) {
                System.err.println("ERROR " + Integer.toString(lineNum) + ":\t" + '"' + wordList[pos] + '"' + " is not the end of the line");
                return;
            } else {
                // build the IR for this Op
                // Add it to the list of Ops
                internalOpList.insertAtEnd(new InternalRep(lineNum, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}