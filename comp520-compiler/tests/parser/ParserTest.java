package parser;

import ast.ASTPrinter;
import ast.Program;
import lexer.Scanner;
import lexer.Token;
import lexer.Tokeniser;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    String[] programText = new String[]{
        "struct i {\n" +
                "    int i;\n" +
                "};\n" +
                "struct y {\n" +
                "    struct i y;\n" +
                "};\n" +
                "struct z {\n" +
                "    struct y z;\n" +
                "};\n" +
                "\n" +
                "\n" +
                "int main() {\n" +
                "    struct i l;\n" +
                "    struct y u;\n" +
                "    struct z p;\n" +
                "    return p.z.y.i;\n" +
                "}",
            "\n" +
                    "char* i;\n" +
                    "char* p;//random comment\n" +
                    "char* u;\n" +
                    "int h[10];\n" +
                    "/*random comment*/\n" +
                    "char main() {\n" +
                    "    int x;\n" +
                    "    x=(int)*i % (int)*p;\n" +
                    "    x = h[5];\n" +
                    "    return u[99];\n" +
                    "}",
            "\n" +
                    "char array[100];\n" +
                    "\n" +
                    "char main() {\n" +
                    "    char i;\n" +
                    "    i = *(&array);\n" +
                    "    return i;\n" +
                    "}",
            "\n" +
                    "struct binTree {\n" +
                    "    struct binTree left_tree;\n" +
                    "    struct binTree right_tree;\n" +
                    "    int value;\n" +
                    "};\n" +
                    "\n" +
                    "struct binTree null_tree;\n" +
                    "\n" +
                    "int max(int a, int b) {\n" +
                    "    if (a>b) {\n" +
                    "        return a;\n" +
                    "    } else {\n" +
                    "        return b;\n" +
                    "    }\n" +
                    "}\n" +
                    "\n" +
                    "int findDepth(struct binTree tree) {\n" +
                    "    int left_depth;\n" +
                    "    int right_depth;\n" +
                    "    if (&tree.left_tree == &null_tree) {\n" +
                    "        left_depth = 0;\n" +
                    "    } else {\n" +
                    "        left_depth = findDepth(tree.left_tree);\n" +
                    "    }\n" +
                    "    if (&tree.right_tree == &null_tree) {\n" +
                    "        right_depth = 0;\n" +
                    "    } else {\n" +
                    "        left_depth = findDepth(tree.right_tree);\n" +
                    "    }\n" +
                    "    return max(left_depth, right_depth);\n" +
                    "}\n",
            "struct x {\n" +
                    "    int x[3];\n" +
                    "};\n" +
                    "\n" +
                    "struct c {\n" +
                    "    struct x X;\n" +
                    "    int p[3];\n" +
                    "};\n" +
                    "\n" +
                    "struct y {\n" +
                    "    char p;\n" +
                    "    struct x X[1];\n" +
                    "    struct c C[4];\n" +
                    "};\n" +
                    "\n" +
                    "struct i {\n" +
                    "    int x;\n" +
                    "    struct y Y[0000];\n" +
                    "};\n" +
                    "\n" +
                    "int main() {\n" +
                    "    struct i I;\n" +
                    "    int u;\n" +
                    "    u = I.Y[4].C[2].X.x[0];\n" +
                    "}"
    };
    String[] astText = new String[] {
            "Program(\n" +
                    "        StructTypeDecl(\n" +
                    "                       StructType(\n" +
                    "                                  i)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               INT,\n" +
                    "                               i)\n" +
                    "                       )\n" +
                    "        ,\n" +
                    "        StructTypeDecl(\n" +
                    "                       StructType(\n" +
                    "                                  y)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               StructType(\n" +
                    "                                          i)\n" +
                    "                               ,\n" +
                    "                               y)\n" +
                    "                       )\n" +
                    "        ,\n" +
                    "        StructTypeDecl(\n" +
                    "                       StructType(\n" +
                    "                                  z)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               StructType(\n" +
                    "                                          y)\n" +
                    "                               ,\n" +
                    "                               z)\n" +
                    "                       )\n" +
                    "        ,\n" +
                    "        FunDecl(\n" +
                    "                INT,\n" +
                    "                main,\n" +
                    "                Block(\n" +
                    "                      VarDecl(\n" +
                    "                              StructType(\n" +
                    "                                         i)\n" +
                    "                              ,\n" +
                    "                              l)\n" +
                    "                      ,\n" +
                    "                      VarDecl(\n" +
                    "                              StructType(\n" +
                    "                                         y)\n" +
                    "                              ,\n" +
                    "                              u)\n" +
                    "                      ,\n" +
                    "                      VarDecl(\n" +
                    "                              StructType(\n" +
                    "                                         z)\n" +
                    "                              ,\n" +
                    "                              p)\n" +
                    "                      ,\n" +
                    "                      Return(\n" +
                    "                             FieldAccessExpr(\n" +
                    "                                             FieldAccessExpr(\n" +
                    "                                                             FieldAccessExpr(\n" +
                    "                                                                             VarExpr(\n" +
                    "                                                                                     p)\n" +
                    "                                                                             ,\n" +
                    "                                                                             z)\n" +
                    "                                                             ,\n" +
                    "                                                             y)\n" +
                    "                                             ,\n" +
                    "                                             i)\n" +
                    "                             )\n" +
                    "                      )\n" +
                    "                )\n" +
                    "        )\n",
            "Program(\n" +
                    "        VarDecl(\n" +
                    "                PointerType(\n" +
                    "                            CHAR)\n" +
                    "                ,\n" +
                    "                i)\n" +
                    "        ,\n" +
                    "        VarDecl(\n" +
                    "                PointerType(\n" +
                    "                            CHAR)\n" +
                    "                ,\n" +
                    "                p)\n" +
                    "        ,\n" +
                    "        VarDecl(\n" +
                    "                PointerType(\n" +
                    "                            CHAR)\n" +
                    "                ,\n" +
                    "                u)\n" +
                    "        ,\n" +
                    "        VarDecl(\n" +
                    "                ArrayType(\n" +
                    "                          INT,\n" +
                    "                          10)\n" +
                    "                ,\n" +
                    "                h)\n" +
                    "        ,\n" +
                    "        FunDecl(\n" +
                    "                CHAR,\n" +
                    "                main,\n" +
                    "                Block(\n" +
                    "                      VarDecl(\n" +
                    "                              INT,\n" +
                    "                              x)\n" +
                    "                      ,\n" +
                    "                      Assign(\n" +
                    "                             VarExpr(\n" +
                    "                                     x)\n" +
                    "                             ,\n" +
                    "                             BinOp(\n" +
                    "                                   TypecastExpr(\n" +
                    "                                                INT,\n" +
                    "                                                ValueAtExpr(\n" +
                    "                                                            VarExpr(\n" +
                    "                                                                    i)\n" +
                    "                                                            )\n" +
                    "                                                )\n" +
                    "                                   ,\n" +
                    "                                   MOD,\n" +
                    "                                   TypecastExpr(\n" +
                    "                                                INT,\n" +
                    "                                                ValueAtExpr(\n" +
                    "                                                            VarExpr(\n" +
                    "                                                                    p)\n" +
                    "                                                            )\n" +
                    "                                                )\n" +
                    "                                   )\n" +
                    "                             )\n" +
                    "                      ,\n" +
                    "                      Assign(\n" +
                    "                             VarExpr(\n" +
                    "                                     x)\n" +
                    "                             ,\n" +
                    "                             ArrayAccessExpr(\n" +
                    "                                             VarExpr(\n" +
                    "                                                     h)\n" +
                    "                                             ,\n" +
                    "                                             IntLiteral(\n" +
                    "                                                        5)\n" +
                    "                                             )\n" +
                    "                             )\n" +
                    "                      ,\n" +
                    "                      Return(\n" +
                    "                             ArrayAccessExpr(\n" +
                    "                                             VarExpr(\n" +
                    "                                                     u)\n" +
                    "                                             ,\n" +
                    "                                             IntLiteral(\n" +
                    "                                                        99)\n" +
                    "                                             )\n" +
                    "                             )\n" +
                    "                      )\n" +
                    "                )\n" +
                    "        )\n",
            "Program(\n" +
                    "        VarDecl(\n" +
                    "                ArrayType(\n" +
                    "                          CHAR,\n" +
                    "                          100)\n" +
                    "                ,\n" +
                    "                array)\n" +
                    "        ,\n" +
                    "        FunDecl(\n" +
                    "                CHAR,\n" +
                    "                main,\n" +
                    "                Block(\n" +
                    "                      VarDecl(\n" +
                    "                              CHAR,\n" +
                    "                              i)\n" +
                    "                      ,\n" +
                    "                      Assign(\n" +
                    "                             VarExpr(\n" +
                    "                                     i)\n" +
                    "                             ,\n" +
                    "                             ValueAtExpr(\n" +
                    "                                         AddressOfExpr(\n" +
                    "                                                       VarExpr(\n" +
                    "                                                               array)\n" +
                    "                                                       )\n" +
                    "                                         )\n" +
                    "                             )\n" +
                    "                      ,\n" +
                    "                      Return(\n" +
                    "                             VarExpr(\n" +
                    "                                     i)\n" +
                    "                             )\n" +
                    "                      )\n" +
                    "                )\n" +
                    "        )\n",
            "Program(\n" +
                    "        StructTypeDecl(\n" +
                    "                       StructType(\n" +
                    "                                  binTree)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               StructType(\n" +
                    "                                          binTree)\n" +
                    "                               ,\n" +
                    "                               left_tree)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               StructType(\n" +
                    "                                          binTree)\n" +
                    "                               ,\n" +
                    "                               right_tree)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               INT,\n" +
                    "                               value)\n" +
                    "                       )\n" +
                    "        ,\n" +
                    "        VarDecl(\n" +
                    "                StructType(\n" +
                    "                           binTree)\n" +
                    "                ,\n" +
                    "                null_tree)\n" +
                    "        ,\n" +
                    "        FunDecl(\n" +
                    "                INT,\n" +
                    "                max,\n" +
                    "                VarDecl(\n" +
                    "                        INT,\n" +
                    "                        a)\n" +
                    "                ,\n" +
                    "                VarDecl(\n" +
                    "                        INT,\n" +
                    "                        b)\n" +
                    "                ,\n" +
                    "                Block(\n" +
                    "                      If(\n" +
                    "                         BinOp(\n" +
                    "                               VarExpr(\n" +
                    "                                       a)\n" +
                    "                               ,\n" +
                    "                               GT,\n" +
                    "                               VarExpr(\n" +
                    "                                       b)\n" +
                    "                               )\n" +
                    "                         ,\n" +
                    "                         Block(\n" +
                    "                               Return(\n" +
                    "                                      VarExpr(\n" +
                    "                                              a)\n" +
                    "                                      )\n" +
                    "                               )\n" +
                    "                         ,\n" +
                    "                         Block(\n" +
                    "                               Return(\n" +
                    "                                      VarExpr(\n" +
                    "                                              b)\n" +
                    "                                      )\n" +
                    "                               )\n" +
                    "                         )\n" +
                    "                      )\n" +
                    "                )\n" +
                    "        ,\n" +
                    "        FunDecl(\n" +
                    "                INT,\n" +
                    "                findDepth,\n" +
                    "                VarDecl(\n" +
                    "                        StructType(\n" +
                    "                                   binTree)\n" +
                    "                        ,\n" +
                    "                        tree)\n" +
                    "                ,\n" +
                    "                Block(\n" +
                    "                      VarDecl(\n" +
                    "                              INT,\n" +
                    "                              left_depth)\n" +
                    "                      ,\n" +
                    "                      VarDecl(\n" +
                    "                              INT,\n" +
                    "                              right_depth)\n" +
                    "                      ,\n" +
                    "                      If(\n" +
                    "                         BinOp(\n" +
                    "                               AddressOfExpr(\n" +
                    "                                             FieldAccessExpr(\n" +
                    "                                                             VarExpr(\n" +
                    "                                                                     tree)\n" +
                    "                                                             ,\n" +
                    "                                                             left_tree)\n" +
                    "                                             )\n" +
                    "                               ,\n" +
                    "                               EQ,\n" +
                    "                               AddressOfExpr(\n" +
                    "                                             VarExpr(\n" +
                    "                                                     null_tree)\n" +
                    "                                             )\n" +
                    "                               )\n" +
                    "                         ,\n" +
                    "                         Block(\n" +
                    "                               Assign(\n" +
                    "                                      VarExpr(\n" +
                    "                                              left_depth)\n" +
                    "                                      ,\n" +
                    "                                      IntLiteral(\n" +
                    "                                                 0)\n" +
                    "                                      )\n" +
                    "                               )\n" +
                    "                         ,\n" +
                    "                         Block(\n" +
                    "                               Assign(\n" +
                    "                                      VarExpr(\n" +
                    "                                              left_depth)\n" +
                    "                                      ,\n" +
                    "                                      FunCallExpr(\n" +
                    "                                                  findDepth,\n" +
                    "                                                  FieldAccessExpr(\n" +
                    "                                                                  VarExpr(\n" +
                    "                                                                          tree)\n" +
                    "                                                                  ,\n" +
                    "                                                                  left_tree)\n" +
                    "                                                  )\n" +
                    "                                      )\n" +
                    "                               )\n" +
                    "                         )\n" +
                    "                      ,\n" +
                    "                      If(\n" +
                    "                         BinOp(\n" +
                    "                               AddressOfExpr(\n" +
                    "                                             FieldAccessExpr(\n" +
                    "                                                             VarExpr(\n" +
                    "                                                                     tree)\n" +
                    "                                                             ,\n" +
                    "                                                             right_tree)\n" +
                    "                                             )\n" +
                    "                               ,\n" +
                    "                               EQ,\n" +
                    "                               AddressOfExpr(\n" +
                    "                                             VarExpr(\n" +
                    "                                                     null_tree)\n" +
                    "                                             )\n" +
                    "                               )\n" +
                    "                         ,\n" +
                    "                         Block(\n" +
                    "                               Assign(\n" +
                    "                                      VarExpr(\n" +
                    "                                              right_depth)\n" +
                    "                                      ,\n" +
                    "                                      IntLiteral(\n" +
                    "                                                 0)\n" +
                    "                                      )\n" +
                    "                               )\n" +
                    "                         ,\n" +
                    "                         Block(\n" +
                    "                               Assign(\n" +
                    "                                      VarExpr(\n" +
                    "                                              left_depth)\n" +
                    "                                      ,\n" +
                    "                                      FunCallExpr(\n" +
                    "                                                  findDepth,\n" +
                    "                                                  FieldAccessExpr(\n" +
                    "                                                                  VarExpr(\n" +
                    "                                                                          tree)\n" +
                    "                                                                  ,\n" +
                    "                                                                  right_tree)\n" +
                    "                                                  )\n" +
                    "                                      )\n" +
                    "                               )\n" +
                    "                         )\n" +
                    "                      ,\n" +
                    "                      Return(\n" +
                    "                             FunCallExpr(\n" +
                    "                                         max,\n" +
                    "                                         VarExpr(\n" +
                    "                                                 left_depth)\n" +
                    "                                         ,\n" +
                    "                                         VarExpr(\n" +
                    "                                                 right_depth)\n" +
                    "                                         )\n" +
                    "                             )\n" +
                    "                      )\n" +
                    "                )\n" +
                    "        )\n",
            "Program(\n" +
                    "        StructTypeDecl(\n" +
                    "                       StructType(\n" +
                    "                                  x)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               ArrayType(\n" +
                    "                                         INT,\n" +
                    "                                         3)\n" +
                    "                               ,\n" +
                    "                               x)\n" +
                    "                       )\n" +
                    "        ,\n" +
                    "        StructTypeDecl(\n" +
                    "                       StructType(\n" +
                    "                                  c)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               StructType(\n" +
                    "                                          x)\n" +
                    "                               ,\n" +
                    "                               X)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               ArrayType(\n" +
                    "                                         INT,\n" +
                    "                                         3)\n" +
                    "                               ,\n" +
                    "                               p)\n" +
                    "                       )\n" +
                    "        ,\n" +
                    "        StructTypeDecl(\n" +
                    "                       StructType(\n" +
                    "                                  y)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               CHAR,\n" +
                    "                               p)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               ArrayType(\n" +
                    "                                         StructType(\n" +
                    "                                                    x)\n" +
                    "                                         ,\n" +
                    "                                         1)\n" +
                    "                               ,\n" +
                    "                               X)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               ArrayType(\n" +
                    "                                         StructType(\n" +
                    "                                                    c)\n" +
                    "                                         ,\n" +
                    "                                         4)\n" +
                    "                               ,\n" +
                    "                               C)\n" +
                    "                       )\n" +
                    "        ,\n" +
                    "        StructTypeDecl(\n" +
                    "                       StructType(\n" +
                    "                                  i)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               INT,\n" +
                    "                               x)\n" +
                    "                       ,\n" +
                    "                       VarDecl(\n" +
                    "                               ArrayType(\n" +
                    "                                         StructType(\n" +
                    "                                                    y)\n" +
                    "                                         ,\n" +
                    "                                         0)\n" +
                    "                               ,\n" +
                    "                               Y)\n" +
                    "                       )\n" +
                    "        ,\n" +
                    "        FunDecl(\n" +
                    "                INT,\n" +
                    "                main,\n" +
                    "                Block(\n" +
                    "                      VarDecl(\n" +
                    "                              StructType(\n" +
                    "                                         i)\n" +
                    "                              ,\n" +
                    "                              I)\n" +
                    "                      ,\n" +
                    "                      VarDecl(\n" +
                    "                              INT,\n" +
                    "                              u)\n" +
                    "                      ,\n" +
                    "                      Assign(\n" +
                    "                             VarExpr(\n" +
                    "                                     u)\n" +
                    "                             ,\n" +
                    "                             ArrayAccessExpr(\n" +
                    "                                             FieldAccessExpr(\n" +
                    "                                                             FieldAccessExpr(\n" +
                    "                                                                             ArrayAccessExpr(\n" +
                    "                                                                                             FieldAccessExpr(\n" +
                    "                                                                                                             ArrayAccessExpr(\n" +
                    "                                                                                                                             FieldAccessExpr(\n" +
                    "                                                                                                                                             VarExpr(\n" +
                    "                                                                                                                                                     I)\n" +
                    "                                                                                                                                             ,\n" +
                    "                                                                                                                                             Y)\n" +
                    "                                                                                                                             ,\n" +
                    "                                                                                                                             IntLiteral(\n" +
                    "                                                                                                                                        4)\n" +
                    "                                                                                                                             )\n" +
                    "                                                                                                             ,\n" +
                    "                                                                                                             C)\n" +
                    "                                                                                             ,\n" +
                    "                                                                                             IntLiteral(\n" +
                    "                                                                                                        2)\n" +
                    "                                                                                             )\n" +
                    "                                                                             ,\n" +
                    "                                                                             X)\n" +
                    "                                                             ,\n" +
                    "                                                             x)\n" +
                    "                                             ,\n" +
                    "                                             IntLiteral(\n" +
                    "                                                        0)\n" +
                    "                                             )\n" +
                    "                             )\n" +
                    "                      )\n" +
                    "                )\n" +
                    "        )\n"

    };

    @Test
    void parse() {
        for (int i=0; i<programText.length; i++) {
            try {
                File source = new File("temp");
                source.createNewFile();
                FileWriter writer = new FileWriter(source);
                writer.write(programText[i]);
                writer.flush();
                Scanner scanner = new Scanner(source);
                Tokeniser tokeniser = new Tokeniser(scanner);
                Parser parser = new Parser(tokeniser);
                Program programAst = parser.parse();
                if (parser.getErrorCount() == 0) {
                    PrintWriter writer1;
                    StringWriter sw = new StringWriter();
                    try {
                        writer1 = new PrintWriter(sw);
                        programAst.accept(new ASTPrinter(writer1));
                        writer1.flush();
                        String ast = sw.toString();
                        assertTrue(astText[i].equals(ast));
                        writer1.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("failed due to caught errors");
                    fail();
                }
            } catch (IOException e) {
                System.out.println("failed to create temporary source file");
                fail();
            }
        }
    }
}